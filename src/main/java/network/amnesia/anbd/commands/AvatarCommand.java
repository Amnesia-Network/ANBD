package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

@ICommand(name = "avatar", category = CommandCategory.INFO, description = "Display your avatar or someone else's avatar")
public class AvatarCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event) {
        return invoke(event, null);
    }
    public Outcome invoke(SlashCommandInteractionEvent event, User user) {

        if(user == null) {
            user = event.getUser();
        }
        String userAvatarUrl = user.getAvatarUrl();
        String userName = user.getAsTag();

        EmbedBuilder embed = new EmbedBuilder();
        embed.addField("Username" , userName, false);
        embed.addField("Avatar Url", userAvatarUrl == null ? "This user has no avatar" : userAvatarUrl + "?size=2048", false);
        embed.setThumbnail(userAvatarUrl);
        embed.setColor(1195176);

        event.replyEmbeds(embed.build()).queue();
        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.USER, "user", "The user to get avatar for", false);
    }
}
