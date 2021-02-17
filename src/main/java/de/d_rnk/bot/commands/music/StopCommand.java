package de.d_rnk.bot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import de.d_rnk.bot.AudioController;

import java.util.logging.Logger;

import static de.d_rnk.bot.Bot.logCommand;

@CommandInfo(
        name = "Stop",
        description = "Stop the current song entirely."
)
@Author("Felix Schneider")
public class StopCommand extends Command {

    private AudioController ac;

    public StopCommand(AudioController audioController){
        this.name = "stop";
        this.help = "Stop the current song entirely.";
        this.guildOnly = true;

        ac = audioController;
    }

    @Override
    protected void execute(CommandEvent event) {
        logCommand(event);

        ac.getGuildAudioManager(event.getGuild()).player.stopTrack();
    }
}
