package de.d_rnk.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static de.d_rnk.bot.Bot.playerManager;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final Deque<AudioTrack> queue;
    private final GuildAudioManager am;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, GuildAudioManager am) {
        this.player = player;
        this.am = am;
        this.queue = new ArrayDeque<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void queueNext(AudioTrack track){
        if(!player.startTrack(track, true)){
            queue.offerFirst(track);
        }
    }

    public void queueAdd(AudioTrack track){
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    public void queueAdd(AudioPlaylist playlist){
        queue.addAll(playlist.getTracks());
        if(player.startTrack(queue.getFirst(), true)){
            queue.poll();
        }
    }

    public void queueNext(AudioPlaylist playlist){
        List<AudioTrack> tracks = playlist.getTracks();
        for(int i = tracks.size() - 1; i >= 0; i--){
            queue.addFirst(tracks.get(i));
        }
        if(player.startTrack(queue.getFirst(), true)){
            queue.poll();
        }
    }

    public void clearQueue(){
        queue.clear();
    }

    public int getQueueLength(){
        return queue.size();
    }

    public AudioTrack getCurrentTrack(){
        return player.getPlayingTrack();
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    public String[] getQueueInfo(){
        Iterator<AudioTrack> it = queue.iterator();
        String[] result = new String[queue.size()];
        int index = 0;
        while(it.hasNext()){
            AudioTrack track = it.next();
            result[index] = track.getInfo().title +" - " +track.getInfo().author;
            index++;

        }
        return result;
    }

    /*
    Queue type 0: queue last
    Queue type 1: queue next
     */

    public void loadAudioTracks(String input, int queueType, TextChannel messageChannel){

        playerManager.loadItem(input, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if(queueType == 0){
                    queueAdd(audioTrack);
                }else if (queueType == 1){
                    queueNext(audioTrack);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if(queueType == 0){
                    queueAdd(audioPlaylist);
                }else if (queueType == 1){
                    queueNext(audioPlaylist);
                }
            }

            @Override
            public void noMatches() {
                messageChannel.sendMessage("Unable to find that song :(");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                messageChannel.sendMessage("Failed to load song :(");
            }

        });
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
