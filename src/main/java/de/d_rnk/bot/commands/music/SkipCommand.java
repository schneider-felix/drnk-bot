package de.d_rnk.bot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import de.d_rnk.bot.AudioController;

@CommandInfo(
        name = "Skip",
        description = "Skip the current song."
)
@Author("Felix Schneider")
public class SkipCommand extends Command {

    private AudioController ac;

    public SkipCommand(AudioController audioController){
        this.name = "skip";
        this.help = "Skip the current song.";
        this.guildOnly = true;

        ac = audioController;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        ac.getGuildAudioManager(commandEvent.getGuild()).scheduler.nextTrack();
    }
}
