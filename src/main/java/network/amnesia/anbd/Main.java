package network.amnesia.anbd;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import network.amnesia.anbd.command.CommandManager;
import network.amnesia.anbd.configs.ConfigManager;
import network.amnesia.anbd.factories.FactoryFactory;
import network.amnesia.anbd.music.MusicManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;

public class Main {

    private static final Logger LOG = LogManager.getLogger();
    public static final Dotenv DOT_ENV = Dotenv.load();

    private static final CommandManager COMMAND_MANAGER = new CommandManager();
    private static final JDA JDA;

    public static final long APP_START_TIME = System.currentTimeMillis();
    private static final CountDownLatch JDAReady = new CountDownLatch(1);

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    static {
        LOG.info("ANBD Starting...");

        JDABuilder builder = JDABuilder.createDefault(DOT_ENV.get("BOT_TOKEN"));

        builder.setActivity(Activity.playing("Starting..."));
        builder.enableCache(CacheFlag.VOICE_STATE);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        JDA = builder.build();
    }

    public static synchronized void JDAReady() {
        if (JDAReady.getCount() == 0) throw new IllegalStateException("Main#ready was already ready");
        JDAReady.countDown();
    }
    private static void load() {

        if (!new FactoryFactory().register()) {
            LOG.fatal("Failed to register factories, check logs above for error.");
        }
    }

    public static JDA getJDA() {
        return JDA;
    }

    public static CommandManager getCommandManager() {
        return COMMAND_MANAGER;
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        return playerManager;
    }

    public static void main(String[] args) throws InterruptedException {
        load();

        JDAReady.await();

        ConfigManager.noop();

        MusicManager.registerSources();

        getJDA().getPresence().setActivity(Activity.playing("Sea of Thieves"));
        LOG.info("ANBD Ready! ({}ms)", System.currentTimeMillis() - APP_START_TIME);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(-1)));
    }

    public static void exit(int status) {
        getJDA().getPresence().setActivity(Activity.playing("Shutting down..."));

        RuntimeStatistics.print();
        LOG.info("");
        LOG.info("Exiting with status: {}", status);
        shutdown(status);
        System.exit(status);
    }

    private static void shutdown(int status) {
        getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
        if (status == 0) getJDA().shutdown();
        else getJDA().shutdownNow();
    }
}
