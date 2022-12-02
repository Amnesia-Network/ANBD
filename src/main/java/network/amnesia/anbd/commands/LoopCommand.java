package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;

@ICommand(name = "loop", category = CommandCategory.MUSIC, description = "Loop music player")
public class LoopCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        musicManager.getTrackScheduler().setLoop(!musicManager.getTrackScheduler().isLoop());
        if (musicManager.getTrackScheduler().isLoop()) {
            event.reply(":repeat: Loop on").queue();
        } else {
            event.reply(":arrow_right: Loop off").queue();
        }
        return Outcome.SUCCESS;
    }
}
