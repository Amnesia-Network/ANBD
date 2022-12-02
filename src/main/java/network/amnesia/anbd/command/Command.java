package network.amnesia.anbd.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.exceptions.IllegalClassImplementationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Command {

    private static final Logger LOG = LogManager.getLogger();

    public final Outcome _invoke(SlashCommandInteractionEvent event) {
        if (getICommand().preview() && Main.DOT_ENV.get("ENV").equals("prod")) {
            event.replyFormat("%s Command preview not available", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        if (getICommand().restricted() && Arrays.stream(Constants.RESTRICTED_ACCESS_IDS).noneMatch(id -> id == event.getUser().getIdLong())) {
            event.replyFormat("%s Restricted command", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        Outcome outcome = autoInvoke(event);

        if (outcome != Outcome.ERROR) return outcome;

        String errorMessage = outcome.hasMessage() ? outcome.getMessage() : "An internal error occurred while attempting to perform this command";
        if (!event.isAcknowledged()) event.reply(errorMessage).setEphemeral(true)
                .delay(10, TimeUnit.SECONDS)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
        else event.getHook().editOriginal(errorMessage)
                .delay(4, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();

        if (outcome.hasMessage() && outcome.hasThrowable()) LOG.error(outcome.getMessage(), outcome.getThrowable());
        else if (outcome.hasMessage()) LOG.error(outcome.getMessage());
        else if (outcome.hasThrowable()) LOG.error(outcome.getThrowable());

        return Outcome.ERROR;
    }

    public Outcome invoke(SlashCommandInteractionEvent event) {return Outcome.UNIMPLEMENTED;}

    @SuppressWarnings("unchecked")
    private Outcome autoInvoke(SlashCommandInteractionEvent event) {
        List<OptionMapping> options = event.getOptions();
        Stream<OptionData> requiredOptions = getCommandData().getOptions().stream().filter(OptionData::isRequired);

        if (options.size() < requiredOptions.count()) {
            Set<OptionData> missingOptions = requiredOptions.filter(requiredOption ->
                    options.stream().anyMatch(option -> requiredOption.getName().equals(option.getName()))
            ).collect(Collectors.toUnmodifiableSet());

            event.replyFormat("Missing option `%s`", missingOptions.iterator().next().getName()).setEphemeral(true).queue();

            return Outcome.INCORRECT_USAGE;
        }

        Set<Method> methods = ReflectionUtils.get(ReflectionUtils.Methods.of(this.getClass()),
                method -> method.getModifiers() == Modifier.PUBLIC
                        && method.getName().equals("invoke")
                        && method.getReturnType().equals(Outcome.class)
                        && method.getParameterCount() == options.size() + 1);

        if (methods.stream().anyMatch(method -> method.getDeclaringClass().equals(Command.class))) return invoke(event);

        Set<String> providedOptions = options.stream().map(OptionMapping::getName).collect(Collectors.toSet());
        providedOptions.add("event");

        methods = methods.stream().filter(method -> Arrays.stream(method.getParameters()).allMatch(parameter -> providedOptions.contains(parameter.getName()))).collect(Collectors.toUnmodifiableSet());

        Method method = methods.iterator().next();

        Parameter[] parameters = method.getParameters();

        if (!parameters[0].getName().equals("event") && !parameters[0].getType().equals(SlashCommandInteractionEvent.class))
            throw new IllegalClassImplementationException("First argument of Command#invoke should be named \"event\" and of type SlashCommandInteractionEvent");

        Object[] parameterValues = new Object[parameters.length];
        parameterValues[0] = event;

        for (int i = 1; i < parameters.length; i++) {
            OptionMapping option = event.getOption(parameters[i].getName());

            if (option == null) throw new IllegalClassImplementationException(String.format("Parameter \"%s %s\" must be the name of an option", parameters[i].getType(), parameters[i].getName()));

            parameterValues[i] = castOptionToParameterType(option, parameters[i].getType());
        }

        Object returnValue = ReflectionUtils.invoke(method, this, parameterValues);

        if (returnValue instanceof Outcome) return (Outcome) returnValue;

        if (returnValue instanceof InvocationTargetException) {
            Throwable exception = ((InvocationTargetException) returnValue).getTargetException();
            if (exception instanceof ExceptionInInitializerError) LOG.error(((ExceptionInInitializerError) exception).getException());
            else LOG.error(exception);
        } else LOG.error(returnValue);

        return Outcome.ERROR;
    }

    public final ICommand getICommand() {
        ICommand iCommand = this.getClass().getAnnotation(ICommand.class);
        if (iCommand == null) {
            throw new IllegalClassImplementationException("Command should implement @ICommand");
        }
        return iCommand;
    }

    public SlashCommandData getCommandData() {
        if (isSubCommand()) throw new IllegalStateException("Cannot get command data of subcommand");
        return new CommandDataImpl(getCommandName(), getICommand().description())
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getICommand().defaultPermissions()))
                .setGuildOnly(getICommand().guildOnly());
    }

    public String getCommandName() {
        return getICommand().name().split("\\\\")[0];
    }

    public String getSubCommandName() {
        return getICommand().name().split("\\\\")[1];
    }

    public boolean isSubCommand() {
        return getICommand().name().contains("\\");
    }

    public SubcommandData getSubcommandData() {
        if (!isSubCommand()) throw new IllegalStateException("Cannot get subcommand data of non-subcommand");
        return new SubcommandData(getSubCommandName(), getICommand().description());
    }

    private Object castOptionToParameterType(OptionMapping option, Class<?> type) {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return option.getAsInt();
        }

        if (type.equals(long.class) || type.equals(Long.class)) {
            return option.getAsLong();
        }

        if (type.equals(double.class) || type.equals(Double.class)) {
            return option.getAsDouble();
        }

        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return option.getAsBoolean();
        }

        if (type.equals(String.class)) {
            return option.getAsString();
        }

        if (type.equals(Member.class)) {
            return option.getAsMember();
        }

        if (type.equals(User.class)) {
            return option.getAsUser();
        }

        if (type.equals(IMentionable.class)) {
            return option.getAsMentionable();
        }

        if (type.equals(GuildChannelUnion.class)) {
            return option.getAsChannel();
        }

        if (type.equals(Mentions.class)) {
            return option.getMentions();
        }

        if (type.equals(Message.Attachment.class)) {
            return option.getAsAttachment();
        }

        throw new IllegalStateException("Option type not recognized");
    }

    public enum Outcome {
        SUCCESS,
        INCORRECT_USAGE,
        ERROR,
        UNIMPLEMENTED;

        private Throwable throwable;
        private String message;

        public Throwable getThrowable() {
            return throwable;
        }

        public boolean hasThrowable() {
            return throwable != null;
        }

        public Outcome setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public boolean hasMessage() {
            return message != null;
        }

        public Outcome setMessage(String message) {
            this.message = message;
            return this;
        }
    }
}
