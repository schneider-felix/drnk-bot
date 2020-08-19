package de.d_rnk.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.List;

import static de.d_rnk.bot.Bot.jda;
import static de.d_rnk.bot.Bot.playerManager;

public class AudioController {

    private HashMap<Long, GuildAudioManager> audioManagers;

    public AudioController(){
        audioManagers = new HashMap<>();
    }

    public GuildAudioManager getGuildAudioManager(Guild g){
        if(!audioManagers.containsKey(g.getIdLong())){
            audioManagers.put(g.getIdLong(), new GuildAudioManager(playerManager));
        }

        g.getAudioManager().setSendingHandler(audioManagers.get(g.getIdLong()).getSendHandler());

        return audioManagers.get(g.getIdLong());
    }

    public GuildAudioManager getGuildAudioManager(long guildId){
        if(!audioManagers.containsKey(guildId)){
            audioManagers.put(guildId, new GuildAudioManager(playerManager));
        }

        Guild g = jda.getGuildById(guildId);

        g.getAudioManager().setSendingHandler(audioManagers.get(g).getSendHandler());

        return audioManagers.get(guildId);
    }

    public void play(Guild g, AudioTrack track){
        GuildAudioManager gm = getGuildAudioManager(g);
        if(g.getAudioManager().isConnected()){
            gm.player.stopTrack();
            gm.player.playTrack(track);
        }
    }

    public void playPlaylist(Guild g, AudioPlaylist playlist){
        List<AudioTrack> audioTracks = playlist.getTracks();
        for(int i = (audioTracks.size() - 1); i >= 0; i--){
            getGuildAudioManager(g).scheduler.queueNext(audioTracks.get(i));
        }
    }

}
