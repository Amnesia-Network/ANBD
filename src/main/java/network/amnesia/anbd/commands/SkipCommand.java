package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;

@ICommand(name = "skip", category = CommandCategory.MUSIC, description = "Skip to next song")
public class SkipCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }
        event.deferReply().queue();

        musicManager.getTrackScheduler().nextTrack();
        event.getHook().editOriginalFormat(":fast_forward: Skipped").queue();
        return Outcome.SUCCESS;
    }
}
