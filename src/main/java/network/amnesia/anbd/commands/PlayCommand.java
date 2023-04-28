package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Button;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.LoadResultHandler;
import network.amnesia.anbd.music.MusicManager;
import network.amnesia.anbd.music.TrackInfo;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@ICommand(name = "play", category = CommandCategory.MUSIC, description = "Play or resume a song")
public class PlayCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        musicManager.getAudioPlayer().setPaused(false);
        event.replyEmbeds(TrackInfo.parse(musicManager.getAudioPlayer().getPlayingTrack()).getStatusEmbed()).setActionRow(
                Button.primary("music-pause", "Pause"),
                Button.primary("music-skip", "Skip"),
                Button.danger("music-stop", "Stop")
        ).queue();

        return Outcome.SUCCESS;
    }

    public Outcome invoke(SlashCommandInteractionEvent event, String query) {
        event.deferReply().queue();

        if (query.startsWith("sc: ")) {
            query = query.replace("sc: ", "scsearch: ");
        } else if (!query.startsWith("http")) {
            query = "ytsearch: " + query;
        }

        boolean playNow = false;

        if (query.endsWith("-now")) {
            query = query.substring(0, query.length() - 4).trim();
            playNow = true;
        }

        MusicManager.forGuild(event.getGuild()).connectVoice((VoiceChannel) event.getMember().getVoiceState().getChannel());

        CompletableFuture<Outcome> futureOutcome = new CompletableFuture<>();

        MusicManager.getAudioPlayerManager().loadItemOrdered(MusicManager.forGuild(event.getGuild()), query, new LoadResultHandler(event, !query.contains("http"), playNow, futureOutcome));

        try {
            return futureOutcome.join();
        } catch (CancellationException | CompletionException e) {
            return Outcome.ERROR;
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "query", "query or song url", false);
    }
}
