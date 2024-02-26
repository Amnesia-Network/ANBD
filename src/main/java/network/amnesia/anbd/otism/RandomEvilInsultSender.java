package network.amnesia.anbd.otism;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static network.amnesia.anbd.Main.getJDA;
import static network.amnesia.anbd.otism.OtismConstants.*;

public class RandomEvilInsultSender extends Thread {
    private static int previousDay = 0;
    private static int nextTime = getRandomNextTime();
    Guild AN = getJDA().getGuildById(AN_GUILD_ID);
    TextChannel helloTextChannel = AN.getTextChannelById(HELLO_TEXT_CHANNEL_ID);

    private static int getRandomNextTime() {
        int[] hours = {0, 1, 2, 3, 4, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}; // !]4-10[
        Random rand = new Random();
        int randNum = rand.nextInt(hours.length);
        System.out.println("[EVIL_INSULT] Next time : " + (previousDay == 0 && randNum > LocalDateTime.now().getHour() ? "Today at " : "Tomorrow at ") + hours[randNum]);
        return hours[randNum];
    }

    public void run() {
        while (true) {
            if (LocalDateTime.now().getDayOfMonth() != previousDay && LocalDateTime.now().getHour() == nextTime) {
                Document doc = null;
                try {
                    doc = Jsoup.connect(getUrl()).get();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String randomEvilInsult = doc.text();
                Member randomMember = getRandomMember();

                helloTextChannel.sendMessage(randomMember.getAsMention() + " " + randomEvilInsult).queue();
                System.out.println("[EVIL_INSULT] New random insult sent to " + randomMember.getNickname());

                previousDay = LocalDateTime.now().getDayOfMonth();
                nextTime = getRandomNextTime();
            }
            try {
                Thread.sleep(44 * 60 * 1000); // check every 44 min
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getUrl() {
        Random rand = new Random();
        int randNum = rand.nextInt(25); // [0-24]
        return "https://evilinsult.com/generate_insult.php?lang=" + (randNum != 4 ? "en" : "fr"); // 1/25 --> 4% fr
    }

    private Member getRandomMember() {
        // GET ACTIVE MEMBERS
        ArrayList<Long> activeMembersId = new ArrayList<>();
        for (Message message : helloTextChannel.getHistory().retrievePast(100).complete()) {
            if (!message.getAuthor().isBot()) {
                activeMembersId.add(message.getAuthor().getIdLong());
            }
        }
        List<Long> distinctActiveMembersId = activeMembersId.stream().distinct().toList();

        // GET PING MEMBERS
        ArrayList<Long> pingMembersId = new ArrayList<>();
        List<Member> members = AN.loadMembers().get();
        for (Member member : members) {
            if (member.getRoles().contains(AN.getRoleById(EVIL_PING_ROLE_ID))) {
                pingMembersId.add(member.getIdLong());
            }
        }
//        System.out.println(pingMembersId);
        for (long pingMemberId : pingMembersId) {
            if (!distinctActiveMembersId.contains(pingMemberId)) {
                activeMembersId.add(pingMemberId);
            }
        }

        Collections.shuffle(activeMembersId);
//        System.out.println(activeMembersId);
        return AN.retrieveMemberById(activeMembersId.get(0)).complete();
    }


}