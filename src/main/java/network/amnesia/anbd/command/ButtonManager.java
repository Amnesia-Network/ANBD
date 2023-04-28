package network.amnesia.anbd.command;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.music.MusicManager;
import network.amnesia.anbd.music.TrackInfo;

public class ButtonManager {

    private static final BiMap<String, ButtonCallback> CALLBACKS = HashBiMap.create();

    public ButtonManager() {
        // Permanent callbacks
        registerCallback("music-play", e -> {
            MusicManager musicManager = MusicManager.forGuild(e.getGuild());

            if (!musicManager.isPlaying()) {
                e.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
                return;
            }

            musicManager.getAudioPlayer().setPaused(false);
            e.editMessageEmbeds(TrackInfo.parse(musicManager.getAudioPlayer().getPlayingTrack()).getStatusEmbed()).setActionRow(
                    Button.primary("music-pause", "Pause"),
                    Button.primary("music-skip", "Skip"),
                    Button.danger("music-stop", "Stop")
            ).queue();
            //e.reply("▶️ Music player resumed (" + e.getMember().getAsMention() + ")").queue();
        });
        registerCallback("music-pause", e -> {
            MusicManager musicManager = MusicManager.forGuild(e.getGuild());

            if (!musicManager.isPlaying()) {
                e.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
                return;
            }

            musicManager.getAudioPlayer().setPaused(true);
            e.editMessageEmbeds(TrackInfo.parse(musicManager.getAudioPlayer().getPlayingTrack()).getStatusEmbed()).setActionRow(
                    Button.success("music-play", "Play"),
                    Button.primary("music-skip", "Skip"),
                    Button.danger("music-stop", "Stop")
            ).queue();
            //e.reply("⏸️ Music player paused (" + e.getMember().getAsMention() + ")").queue();
        });
        registerCallback("music-skip", e -> {
            MusicManager musicManager = MusicManager.forGuild(e.getGuild());
            if (!musicManager.isPlaying()) {
                e.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
                return;
            }
            e.reply("⏩ Skipped (" + e.getMember().getAsMention() + ")").queue();
            musicManager.getTrackScheduler().nextTrack();
        });
        registerCallback("music-stop", e -> {
            MusicManager musicManager = MusicManager.forGuild(e.getGuild());

            if (!musicManager.isPlaying() && !musicManager.getAudioManager().isConnected()) {
                e.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
                return;
            }

            musicManager.getAudioPlayer().stopTrack();
            musicManager.getTrackScheduler().getQueue().clear();
            musicManager.getTrackScheduler().setLoop(false);
            musicManager.getAudioPlayer().setVolume(100);
            musicManager.getAudioManager().closeAudioConnection();
            e.reply("⏹️ Music player stopped (" + e.getMember().getAsMention() + ")").queue();
        });
    }

    public void registerCallback(String id, ButtonCallback callback) {
        CALLBACKS.put(id, callback);
    }

    public void removeCallback(String id) {
        CALLBACKS.remove(id);
    }

    public void removeCallback(ButtonCallback buttonCallback) {
        CALLBACKS.remove(CALLBACKS.inverse().get(buttonCallback));
    }

    public ButtonCallback getCallback(String id) {
        return CALLBACKS.get(id);
    }

    public void handleEvent(ButtonInteractionEvent event) {
        ButtonCallback callback = getCallback(event.getComponentId());
        if (callback != null) callback.run(event);
    }
}
