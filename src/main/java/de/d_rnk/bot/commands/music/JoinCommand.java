package de.d_rnk.bot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

@CommandInfo(
        name = "Join",
        description = "Joins the users current voice channel."
)
@Author("Felix Schneider")
public class JoinCommand extends Command {

    public JoinCommand(){
        this.name = "join";
        this.help = "Joins the users current voice channel.";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User author = commandEvent.getAuthor();
        Guild guild = commandEvent.getGuild();
        Member member = guild.getMember(author);
        if(member.getVoiceState().inVoiceChannel()){
            guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
        }else{
            commandEvent.reply("Connect to a voice channel first.");
        }
    }
}
