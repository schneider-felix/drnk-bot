package de.d_rnk.bot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.d_rnk.bot.AudioController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.logging.Logger;

import static de.d_rnk.bot.Bot.logCommand;
import static de.d_rnk.bot.Bot.playerManager;

@CommandInfo(
        name = "Queue",
        description = "Modify the current song queue."
)
@Author("Felix Schneider")
public class QueueCommand extends Command {

    private AudioController ac;

    public QueueCommand(AudioController audioController){
        this.name = "queue";
        this.help = "Modify the current song queue.";
        this.guildOnly = true;

        ac = audioController;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        logCommand(commandEvent);

        GuildAudioManager am = ac.getGuildAudioManager(commandEvent.getGuild());
        if(commandEvent.getArgs().trim().length() == 0 || commandEvent.getArgs() == null){
            outputQueue(commandEvent.getGuild(), commandEvent.getTextChannel());
        }
    }

    private void outputQueue(Guild g, TextChannel channel){
        String[] queue = ac.getGuildAudioManager(g).scheduler.getQueueInfo();
        if(queue.length != 0){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setTitle("Queue");
            AudioTrackInfo currentTrack = ac.getGuildAudioManager(g).scheduler.getCurrentTrack().getInfo();

            String description = "Current song: " +currentTrack.title +" - " +currentTrack.author +"\n\n";
            for(int i = 0; i < queue.length; i++){
                description = description +(i + 1) +". " +queue[i] +"\n";
            }
            if(description.length() > 2047){
                description = description.substring(0, 2044);
                description = description + "...";
            }
            eb.appendDescription(description);
            channel.sendMessage(eb.build()).queue();
        }
    }
}
