package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;

@ICommand(name = "stop", category = CommandCategory.MUSIC, description = "Stop playing music, clear queue and leave channel")
public class StopCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying() && !musicManager.getAudioManager().isConnected()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        musicManager.getAudioPlayer().stopTrack();
        musicManager.getTrackScheduler().getQueue().clear();
        musicManager.getTrackScheduler().setLoop(false);
        musicManager.getAudioPlayer().setVolume(100);
        musicManager.getAudioManager().closeAudioConnection();
        event.replyFormat(":stop_button: Music player stopped").queue();
        return Outcome.SUCCESS;
    }
}
