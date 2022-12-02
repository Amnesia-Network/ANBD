package network.amnesia.anbd.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.Utils;
import network.amnesia.anbd.command.Command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class LoadResultHandler implements AudioLoadResultHandler {
    private final SlashCommandInteractionEvent event;
    private final boolean loadFirstOnly;
    private final boolean playNow;

    private final CompletableFuture<Command.Outcome> futureOutcome;

    public LoadResultHandler(SlashCommandInteractionEvent event, boolean loadFirstOnly, boolean playNow, CompletableFuture<Command.Outcome> futureOutcome) {
        this.event = event;
        this.loadFirstOnly = loadFirstOnly;
        this.playNow = playNow;
        this.futureOutcome = futureOutcome;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        trackOrPlaylistLoaded(track, null);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        if (this.loadFirstOnly) {
            trackLoaded(firstTrack);
            return;
        }

        trackOrPlaylistLoaded(firstTrack, playlist);
    }

    private void trackOrPlaylistLoaded(AudioTrack track, AudioPlaylist playlist) {
        TrackInfo trackInfo = TrackInfo.parse(track);
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) musicManager.getTrackScheduler().skipNextNotification();

        boolean playingNow = musicManager.getTrackScheduler().queue(track, playNow);

        LinkedBlockingQueue<AudioTrack> currentQueue = null;

        if (playlist != null) {
            if (playingNow) {
                currentQueue = new LinkedBlockingQueue<>(musicManager.getTrackScheduler().getQueue());
                musicManager.getTrackScheduler().getQueue().clear();
            }

            playlist.getTracks().stream().filter(t -> !t.equals(track)).forEach(musicManager.getTrackScheduler()::queue);

            if (playNow && currentQueue != null) {
                musicManager.getTrackScheduler().getQueue().addAll(currentQueue);
            }
        }

        EmbedBuilder eb = new EmbedBuilder(trackInfo.getStatusEmbed(playlist, !playingNow));

        if (!playingNow) {
            eb.addField("Estimated time until playing", Utils.formatTime(musicManager.getTrackScheduler().getQueueDuration()), true);
            eb.addField("Position in queue", String.valueOf(musicManager.getTrackScheduler().queueSize() + (musicManager.getAudioPlayer().getPlayingTrack() == null ? 0 : 1)), true);
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();

        futureOutcome.complete(Command.Outcome.SUCCESS);
    }

    @Override
    public void noMatches() {
        event.getHook().editOriginalFormat("%s No matches found", Constants.X_EMOTE).queue();
        futureOutcome.complete(Command.Outcome.ERROR);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        event.getHook().editOriginalFormat("%s Could not play: %s", Constants.X_EMOTE, exception.getMessage()).queue();
        futureOutcome.complete(Command.Outcome.ERROR);
    }
}
