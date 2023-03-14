package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.io.*;
import java.util.ArrayList;

import static network.amnesia.anbd.Constants.LOADING_EMOTE;

@ICommand(name = "massrename", category = CommandCategory.MODERATION, description = "Mass Rename from a theme column (without theme -> reset)", restricted = true)
public class MassRenameCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event) {
        return invoke(event, null);
    }
    public Outcome invoke(SlashCommandInteractionEvent event, String theme) {
        //LOAD VALUES
        InputStreamReader isr = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("names.csv"));
        BufferedReader reader = null;
        String line = "";

        try {
            reader = new BufferedReader(isr);
            boolean isFirst = true;
            while((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if(isFirst) {
                    headers = row;
                    isFirst = false;
                    continue;
                }
                values.add(row);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //END OF LOAD
        if(theme == null) {
            //MASS RENAME RESET
            values.forEach(l ->
                event.getGuild().retrieveMemberById(Long.parseLong(l[0])).queue(member -> {
                    member.modifyNickname("").queue();
                })
            );
            event.reply("Reset in progress " + LOADING_EMOTE + " (This may take a few seconds).\nWARNING : The bot cannot rename the server owner or users who have a role higher than its own.").setEphemeral(true).queue();
            return Outcome.SUCCESS;
        }
        //FIND THEME POSITION
        int counter = 0;
        for(String s : headers) {
            if(s.equalsIgnoreCase(theme)) {
                themePos = counter;
            }
            counter++;
        }
        //MASS RENAME BY THEME
        if(themePos != 00) {
            values.forEach(l ->
                    event.getGuild().retrieveMemberById(Long.parseLong(l[0])).queue(member -> {
                        if(l[themePos] != "/") {
                            member.modifyNickname(l[themePos]).queue();
                        }
                    })
            );
        } else {
            event.reply("Theme not found.").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }
        event.reply("Mass rename in progress " + LOADING_EMOTE + " (This may take a few seconds).\nWARNING : The bot cannot rename the server owner or users who have a role higher than its own.").setEphemeral(true).queue();
        return Outcome.SUCCESS;
    }
    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "theme", "Name of the column from which you want to mass rename", false);
    }

    private String[] headers = {};
    private ArrayList<String[]> values = new ArrayList<String[]>();
    private int themePos = 00;
}
