package network.amnesia.anbd.configs.documents;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;


@Document(collection = "guild-configs", schemaVersion = "1.0")
public class GuildConfigDocument {

    @Id
    private Long guildId;
    private Long musicTextChannelId;
    private Long musicVoiceChannelId;

    private String hangmanGameData;

    GuildConfigDocument() {
    }

    public GuildConfigDocument(Long guildId) {
        this.guildId = guildId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getMusicTextChannelId() {
        return musicTextChannelId;
    }

    public void setMusicTextChannelId(Long musicTextChannelId) {
        this.musicTextChannelId = musicTextChannelId;
    }

    public Long getMusicVoiceChannelId() {
        return musicVoiceChannelId;
    }

    public void setMusicVoiceChannelId(Long musicVoiceChannelId) {
        this.musicVoiceChannelId = musicVoiceChannelId;
    }
}
