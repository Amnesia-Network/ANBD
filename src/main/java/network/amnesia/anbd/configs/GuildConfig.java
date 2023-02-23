package network.amnesia.anbd.configs;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.configs.documents.GuildConfigDocument;
import network.amnesia.anbd.games.HangmanGame;

import java.util.Set;


public class GuildConfig {

    private final GuildConfigDocument guildConfigDocument;

    GuildConfig(GuildConfigDocument guildConfigDocument) {
        this.guildConfigDocument = guildConfigDocument;
    }

    public Guild getGuild() {
        return Main.getJDA().getGuildById(guildConfigDocument.getGuildId());
    }

    public TextChannel getMusicTextChannel() {
        if (guildConfigDocument.getMusicTextChannelId() == null) return null;
        return Main.getJDA().getTextChannelById(guildConfigDocument.getMusicTextChannelId());
    }

    public void setMusicTextChannel(TextChannel musicTextChannel) {
        guildConfigDocument.setMusicTextChannelId(musicTextChannel.getIdLong());
    }

    public void setMusicTextChannel(long musicTextChannelId) {
        guildConfigDocument.setMusicTextChannelId(musicTextChannelId);
    }

    public VoiceChannel getMusicVoiceChannel() {
        if (guildConfigDocument.getMusicVoiceChannelId() == null) return null;
        return Main.getJDA().getVoiceChannelById(guildConfigDocument.getMusicVoiceChannelId());
    }

    public void setMusicVoiceChannel(VoiceChannel musicVoiceChannel) {
        guildConfigDocument.setMusicVoiceChannelId(musicVoiceChannel.getIdLong());
    }

    public void setMusicVoiceChannel(long musicVoiceChannelId) {
        guildConfigDocument.setMusicVoiceChannelId(musicVoiceChannelId);
    }

    public void save() {
        ConfigManager.saveConfig(guildConfigDocument);
    }

    public GuildConfigDocument getDocument() {
        return guildConfigDocument;
    }

    public Set<HangmanGame> getHangmanGames() {
        return Set.of();
    }
}
