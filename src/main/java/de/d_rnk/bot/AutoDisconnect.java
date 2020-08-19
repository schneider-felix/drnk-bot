package de.d_rnk.bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.d_rnk.bot.Bot.jda;

public class AutoDisconnect extends ListenerAdapter {

    public AutoDisconnect(){
        System.out.println("Auto disconnect enabled.");
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e){
        leaveChannel(e.getChannelLeft(), e.getMember(), e.getGuild());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e){
        leaveChannel(e.getChannelLeft(), e.getMember(), e.getGuild());
    }

    public void leaveChannel(VoiceChannel c, Member m, Guild g){
        if(c == g.getAudioManager().getConnectedChannel()){
            if (m != g.getSelfMember()){
                if(c.getMembers().size() == 1){
                    g.getAudioManager().closeAudioConnection();
                }
            }
        }
    }


}
