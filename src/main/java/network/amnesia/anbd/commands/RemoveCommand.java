package network.amnesia.anbd.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;

@ICommand(name = "remove", category = CommandCategory.MUSIC, description = "Remove song from the queue")
public class RemoveCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, int index) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        if (index < 0 || index > musicManager.getTrackScheduler().getQueue().size()) {
            event.replyFormat("%s Please select an index between [1-%d]", Constants.X_EMOTE, musicManager.getTrackScheduler().getQueue().size()).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        event.deferReply().queue();


        int i = 1;
        AudioTrack track = null;

        for (AudioTrack t : musicManager.getTrackScheduler().getQueue()) {
            if (index == i) {
                track = t;
                break;
            }
            i++;
        }

        if (track != null) {
            musicManager.getTrackScheduler().getQueue().remove(track);

            event.getHook().editOriginalFormat(":put_litter_in_its_place: Removed **%s** from queue", track.getInfo().title).queue();
            return Outcome.SUCCESS;
        }

        event.getHook().editOriginalFormat("%s Song not found in queue", Constants.X_EMOTE).queue();
        return Outcome.INCORRECT_USAGE;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "index", "Index of song in queue", true);
    }
}
