package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.Utils;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.music.MusicManager;
import network.amnesia.anbd.music.TrackInfo;

@ICommand(name = "seek", category = CommandCategory.MUSIC, description = "Seek to position in playing song")
public class SeekCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, String timestamp) {
        MusicManager musicManager = MusicManager.forGuild(event.getGuild());

        if (!musicManager.isPlaying()) {
            event.replyFormat("%s Nothing is playing", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        if (!musicManager.getAudioPlayer().getPlayingTrack().isSeekable()) {
            event.replyFormat("%s Cannot seek on LIVE song", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        try {
            long time = Utils.stringToTime(timestamp);

            if (time < 0 || time > musicManager.getAudioPlayer().getPlayingTrack().getDuration()) {
                String duration = Utils.formatTime(musicManager.getAudioPlayer().getPlayingTrack().getDuration());

                String zero = duration.length() > 5 ? "00:00:00" : "00:00";
                event.replyFormat("Please seek between [%s-%s]", zero, duration).setEphemeral(true).queue();
                return Outcome.INCORRECT_USAGE;
            }

            musicManager.getAudioPlayer().getPlayingTrack().setPosition(time);

            event.deferReply().queue();

            EmbedBuilder eb = new EmbedBuilder(TrackInfo.parse(musicManager.getAudioPlayer().getPlayingTrack()).getStatusEmbed());

            eb.setTitle(String.format("%s [%s/%s]",
                            musicManager.getAudioPlayer().getPlayingTrack().getInfo().title,
                            Utils.formatTime(time),
                            Utils.formatTime(musicManager.getAudioPlayer().getPlayingTrack().getDuration())),
                    musicManager.getAudioPlayer().getPlayingTrack().getInfo().uri);

            event.getHook().editOriginalEmbeds(eb.build()).queue();

            return Outcome.SUCCESS;

        } catch (NumberFormatException e) {
            event.replyFormat("%s Invalid timestamp", Constants.X_EMOTE).queue();
            return Outcome.INCORRECT_USAGE;
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "timestamp", "Timestamp to seek to (mm:ss)", true);
    }
}
