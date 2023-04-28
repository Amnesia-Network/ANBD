package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ICommand(name = "allavatars", category = CommandCategory.INFO, description = "Get a compressed file of all server's avatars")
public class AllAvatarsCommand extends Command {

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        event.getGuild().loadMembers().onSuccess((members -> {
            UUID uuid = UUID.randomUUID();
            File folder = new File("./" + uuid);
            File zipFile = new File("./" + uuid + ".zip");
            folder.mkdir();
            for (Member member : members) {
                if (member.getUser().getAvatarUrl() != null) {
                    InputStream in = null;
                    try {
                        in = new URL(member.getEffectiveAvatarUrl()).openStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    File avatar = new File(folder.getAbsolutePath() + "/" + member.getUser().getAsTag().replace('#', '_') + ".png");
                    try {
                        avatar.createNewFile();
                        Files.copy(in, avatar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(zipFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            try {
                zipFile(folder, folder.getName(), zipOut);
                zipOut.close();
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            event.getHook().editOriginalAttachments(FileUpload.fromData(zipFile).setName("avatars.zip")).queue();

            // DELETE FILES
            try {
                File[] files = folder.listFiles();

                // delete each file from the directory
                for (File file : files) {
                    file.delete();
                }

                // delete the directory
                folder.delete();

                // delete the zip file
                zipFile.delete();

            } catch (Exception e) {
                e.getStackTrace();
            }
        }));
        return Outcome.SUCCESS;
    }
}
