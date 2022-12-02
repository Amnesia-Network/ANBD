package network.amnesia.anbd.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.configs.ConfigManager;
import network.amnesia.anbd.configs.GuildConfig;

@ICommand(name = "settings\\musicvoicechannel", category = CommandCategory.GUILD, defaultPermissions = {Permission.ADMINISTRATOR}, description = "Manage guild settings")
public class SettingsMusicVoiceChannelCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        event.replyFormat("%s music voice channel: %s", event.getGuild().getName(), ConfigManager.getGuildConfig(event.getGuild()).getMusicVoiceChannel().getAsMention()).setEphemeral(true).queue();
        return Outcome.SUCCESS;
    }

    public Outcome invoke(SlashCommandInteractionEvent event, GuildChannelUnion channel) {
        if (!(channel instanceof VoiceChannel)) {
            event.replyFormat("%s Please provide a valid Voice Channel", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        event.deferReply().queue();

        GuildConfig guildConfig = ConfigManager.getGuildConfig(channel.getGuild());
        guildConfig.setMusicVoiceChannel((VoiceChannel) channel);
        guildConfig.save();

        event.getHook().editOriginalFormat("%s music voice channel set to %s", Constants.CHECK_EMOTE, ConfigManager.getGuildConfig(event.getGuild()).getMusicVoiceChannel().getAsMention()).queue();
        return Outcome.SUCCESS;
    }

    @Override
    public SubcommandData getSubcommandData() {
        return super.getSubcommandData().addOption(OptionType.CHANNEL, "channel", "Default music voice channel");
    }
}
