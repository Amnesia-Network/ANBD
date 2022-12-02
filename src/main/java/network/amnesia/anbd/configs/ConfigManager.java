package network.amnesia.anbd.configs;

import com.google.common.base.MoreObjects;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.CryptoUtil;
import io.jsondb.crypto.Default1Cipher;
import io.jsondb.crypto.ICipher;
import net.dv8tion.jda.api.entities.Guild;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.configs.documents.GuildConfigDocument;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class ConfigManager {
    private static final JsonDBTemplate jsonDBTemplate;
    private static final HashMap<Guild, GuildConfig> GUILD_CONFIGS = new HashMap<>();

    static {
        String dbFilesLocation = "./configs/";

        String baseScanPackage = "network.amnesia.anbd.configs.documents";

        ICipher cipher;
        try {
            cipher = new Default1Cipher(CryptoUtil.generate128BitKey(Main.DOT_ENV.get("ENCRYPTION_PASSWORD"), Main.DOT_ENV.get("ENCRYPTION_SALT")));
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        jsonDBTemplate = new JsonDBTemplate(dbFilesLocation, baseScanPackage, cipher);

        createCollectionIfMissing(GuildConfigDocument.class);
    }

    private static void createCollectionIfMissing(Class<?> clazz) {
        if (!jsonDBTemplate.collectionExists(clazz)) jsonDBTemplate.createCollection(clazz);
    }

    public static GuildConfig getGuildConfig(Guild guild) {
        return GUILD_CONFIGS.computeIfAbsent(guild, ConfigManager::loadGuildConfig);
    }

    private static GuildConfig loadGuildConfig(Guild guild) {
        GuildConfigDocument guildConfigDocument = MoreObjects.firstNonNull(jsonDBTemplate.findById(guild.getIdLong(),
                GuildConfigDocument.class), new GuildConfigDocument(guild.getIdLong()));

        return new GuildConfig(guildConfigDocument);
    }

    public static void saveConfig(Object config) {
        jsonDBTemplate.upsert(config);
    }

    public static void noop() {}
}
