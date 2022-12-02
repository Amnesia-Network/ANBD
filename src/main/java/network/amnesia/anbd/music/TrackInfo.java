package network.amnesia.anbd.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import network.amnesia.anbd.Utils;
import opengraph.OpenGraph;

public class TrackInfo {

    private final AudioTrack track;
    private final OpenGraph openGraph;

    public static TrackInfo parse(AudioTrack track) {
        return new TrackInfo(track);
    }

    private TrackInfo(AudioTrack track) {
        this.track = track;

        OpenGraph temp = null;
        try {
            if (track.getInfo().uri.contains("youtube.com") || track.getInfo().uri.contains("soundcloud.com") || track.getInfo().uri.contains("twitch.tv")) temp = new OpenGraph(track.getInfo().uri, true);
        } catch (Exception ignored) {}

        openGraph = temp;
    }

    public String getImage() {
        if (openGraph == null) return "";
        return openGraph.getContent("image");
    }

    public String getDescription() {
        if (openGraph == null) return "";
        return unescapeHTML(openGraph.getContent("description"));
    }

    public boolean hasOGP() {
        return openGraph != null;
    }

    private static String unescapeHTML(String string) {
        return string.replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&#39;", "'")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&num;", "#");
    }

    public MessageEmbed getStatusEmbed() {
        return getStatusEmbed( null, false);
    }

    public MessageEmbed getStatusEmbed(boolean addedToQueue) {
        return getStatusEmbed(null, false);
    }

    public MessageEmbed getStatusEmbed(AudioPlaylist playlist, boolean addedToQueue) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(String.format("**%s**", track.getInfo().title), track.getInfo().uri);
        eb.setAuthor(addedToQueue ? "Added to Queue" : "Now Playing");

        if (hasOGP()) {
            eb.setThumbnail(getImage());
            eb.setDescription(getDescription());
        }

        eb.addField("Channel", track.getInfo().author, true);
        eb.addField("Song Duration", (track.getInfo().isStream) ? "LIVE" : Utils.formatTime(track.getDuration()), true);

        if (playlist != null) {
            eb.addField("Playlist", String.format("%s (%d)", playlist.getName(), playlist.getTracks().size()), true);
        }

        eb.setColor(1195176);

        return eb.build();
    }
}
