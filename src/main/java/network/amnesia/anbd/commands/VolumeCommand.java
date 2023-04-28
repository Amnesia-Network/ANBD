package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;

@ICommand(name = "volume", category = CommandCategory.MUSIC, description = "Set the music volume")
public class VolumeCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, int volume) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        if (volume < 0 || (volume > 200 && !event.getMember().hasPermission(Permission.MANAGE_CHANNEL))) {
            event.reply("Please set volume between [0-200]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        } else if (volume > 1000) {
            event.reply("Please set volume between [0-1000]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        musicManager.getAudioPlayer().setVolume(volume);
        event.replyFormat("%s Volume set to %d%%", volume > 200 ? ":warning:" : volume > 125 ? ":loud_sound:" : volume > 75 ? ":sound:" : volume > 0 ? ":speaker:" : ":mute:", volume).queue();

        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "volume", "Volume to set [0-200]", true);
    }
}
