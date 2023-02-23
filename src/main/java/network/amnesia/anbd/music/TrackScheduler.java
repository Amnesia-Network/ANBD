package network.amnesia.anbd.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import network.amnesia.anbd.Utils;
import network.amnesia.anbd.configs.ConfigManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final MusicManager musicManager;
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    private boolean loop;

    private boolean skipNextNotification;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(MusicManager musicManager, AudioPlayer player) {
        this.musicManager = musicManager;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public boolean queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently
        // playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            return false;
        }
        return true;
    }

    public boolean queue(AudioTrack track, boolean playNow) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently
        // playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, !playNow)) {
            queue.offer(track);
            return false;
        }
        return true;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (isLoop()) {
            queue.add(player.getPlayingTrack().makeClone());
        }
        player.startTrack(queue.poll(), false);

        if (queue.isEmpty() && !musicManager.isPlaying()) {
            musicManager.disconnect();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void shuffle() {
        List<AudioTrack> newQueue = new ArrayList<>(queue);
        Collections.shuffle(newQueue);
        queue.clear();
        queue.addAll(newQueue);
    }

    public long getQueueDuration() {
        return queue.stream().mapToLong(AudioTrack::getDuration).sum()
                + (player.getPlayingTrack() == null ? 0 :
                   (player.getPlayingTrack().getDuration() - player.getPlayingTrack().getPosition()));
    }

    public int queueSize() {
        return queue.size();
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean hasNextTrack() {
        return !queue.isEmpty();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (shouldSkipNextNotification()) return;

        TrackInfo trackInfo = TrackInfo.parse(track);

        Utils.firstNonNull(
                ConfigManager.getGuildConfig(musicManager.getGuild()).getMusicTextChannel(),
                musicManager.getGuild().getDefaultChannel().asTextChannel()
        ).sendMessageEmbeds(trackInfo.getStatusEmbed()).queue();    }

    public void skipNextNotification() {
        skipNextNotification = true;
    }

    public boolean shouldSkipNextNotification() {
        if (skipNextNotification) {
            skipNextNotification = false;
            return true;
        }
        return false;
    }
}