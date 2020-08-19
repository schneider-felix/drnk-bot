package de.d_rnk.bot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import de.d_rnk.bot.AudioController;
import net.dv8tion.jda.api.managers.AudioManager;

@CommandInfo(
        name = "Leave",
        description = "Leave the current voice channel."
)
@Author("Felix Schneider")
public class LeaveCommand extends Command {

    private AudioController ac;

    public LeaveCommand(AudioController audioController){
        this.name = "leave";
        this.help = "Leave the current voice channel.";
        this.guildOnly = true;

        ac = audioController;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        ac.getGuildAudioManager(commandEvent.getGuild()).player.stopTrack();

        AudioManager am = commandEvent.getGuild().getAudioManager();
        if(am.isConnected()){
            am.closeAudioConnection();
        }

    }
}
