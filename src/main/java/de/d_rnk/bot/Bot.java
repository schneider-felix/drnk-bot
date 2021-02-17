package de.d_rnk.bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import de.d_rnk.bot.commands.music.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.*;

public class Bot {

    public static JDA jda;
    public static AudioPlayerManager playerManager;
    private static Logger cmdLogger;

    public static void main(String[] args){
        cmdLogger = setupLogger();
        if(cmdLogger == null){
            System.out.println("Failed to setup logger. This is probably a permission problem.");
            System.exit(0);
        }

        try {
            File key = new File("bot-key.txt");
            BufferedReader br = new BufferedReader(new FileReader(key));
            String botKey = br.readLine();
            System.out.println(botKey);
            JDABuilder builder = JDABuilder.createDefault(botKey);
            //builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS);

            //Compression sollte vermutlich wieder angeschalten werden
            builder.setCompression(Compression.NONE);
            try {
                jda = builder.build();

                playerManager = new DefaultAudioPlayerManager();
                AudioSourceManagers.registerRemoteSources(playerManager);
                AudioSourceManagers.registerLocalSource(playerManager);

                AudioController ac = new AudioController();

                CommandClientBuilder cmdBuilder = new CommandClientBuilder();
                cmdBuilder.setPrefix("+");
                cmdBuilder.setOwnerId("137546039631544320");
                cmdBuilder.addCommand(new JoinCommand());
                cmdBuilder.addCommand(new LeaveCommand(ac));
                cmdBuilder.addCommand(new PlayCommand(ac));
                cmdBuilder.addCommand(new SkipCommand(ac));
                cmdBuilder.addCommand(new StopCommand(ac));
                cmdBuilder.addCommand(new QueueCommand(ac));
                CommandClient cmdClient = cmdBuilder.build();

                jda.addEventListener(cmdClient);
                jda.addEventListener(new AutoDisconnect());
            } catch (LoginException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Logger setupLogger(){
        try {
            Logger commandLogger = Logger.getLogger("Commands");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_AAAAAAAA");
            LocalDateTime now = LocalDateTime.now();
            Handler fileHandler = new FileHandler("command-log_" +dtf.format(now) +".log");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new Formatter() {
                private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                @Override
                public String format(LogRecord record) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[").append(df.format(new Date(record.getMillis()))).append("] ");
                    builder.append("[Command] ");
                    builder.append(formatMessage(record));
                    builder.append("\n");
                    return builder.toString();
                }
            });
            commandLogger.addHandler(fileHandler);

            return commandLogger;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logCommand(CommandEvent e){
        cmdLogger.info(e.getGuild().getName() +"(" +e.getGuild().getId() +") > " +e.getAuthor().getAsTag() +" >>> " +e.getMessage().getContentRaw());
    }

}
