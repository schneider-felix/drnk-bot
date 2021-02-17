package de.d_rnk.bot.commands.music;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.d_rnk.bot.AudioController;
import de.d_rnk.bot.GuildAudioManager;

import java.util.logging.Logger;

import static de.d_rnk.bot.Bot.logCommand;
import static de.d_rnk.bot.Bot.playerManager;

@CommandInfo(
        name = "Play",
        description = "Plays a song from the specified source."
)
@Author("Felix Schneider")
public class PlayCommand extends Command {

    private AudioController ac;

    public PlayCommand(AudioController audioController){
        this.name = "play";
        this.help = "Plays a song from the specified source.";
        this.guildOnly = true;
        ac = audioController;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        logCommand(commandEvent);

        if (commandEvent.getGuild().getAudioManager().isConnected()) {
            String song = commandEvent.getArgs();
            GuildAudioManager am = ac.getGuildAudioManager(commandEvent.getGuild());
            playerManager.loadItemOrdered(am, song, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    ac.play(commandEvent.getGuild(), track);

                    commandEvent.replySuccess("Adding to queue " + track.getInfo().title);
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    ac.playPlaylist(commandEvent.getGuild(), audioPlaylist);

                    commandEvent.replySuccess("Playing playlist " + audioPlaylist.getName());
                }

                @Override
                public void noMatches() {
                    commandEvent.replyError("Unable to find that song :(");
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    commandEvent.replyError("Failed to load song :(");
                }
            });
        }else {
            commandEvent.replyError("The Bot has to connect to a channel first.");
        }
    }

}
