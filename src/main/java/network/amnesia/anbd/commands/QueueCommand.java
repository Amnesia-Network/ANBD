package network.amnesia.anbd.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.Utils;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;
import network.amnesia.anbd.music.TrackInfo;

@ICommand(name = "queue", category = CommandCategory.MUSIC, description = "List the music queue")
public class QueueCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        StringBuilder sb = new StringBuilder();
        int index = 1;

        for (AudioTrack track : musicManager.getTrackScheduler().getQueue()) {
            sb.append(String.format("`%d` **%s** (%s)\n", index, track.getInfo().title, Utils.formatTime(track.getDuration())));
            index++;
            if (index > 20) break;
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(String.format("Queue%s", musicManager.getTrackScheduler().isLoop() ? " \uD83D\uDD01" : "")); // 🔁
        eb.setTitle(String.format("%s [%s/%s]",
                musicManager.getAudioPlayer().getPlayingTrack().getInfo().title,
                Utils.formatTime(musicManager.getAudioPlayer().getPlayingTrack().getPosition()),
                Utils.formatTime(musicManager.getAudioPlayer().getPlayingTrack().getDuration())),
                musicManager.getAudioPlayer().getPlayingTrack().getInfo().uri);
        eb.setThumbnail(TrackInfo.parse(musicManager.getAudioPlayer().getPlayingTrack()).getImage());

        eb.setDescription(sb.toString());

        event.replyEmbeds(eb.build()).queue();

        return Outcome.SUCCESS;
    }
}
