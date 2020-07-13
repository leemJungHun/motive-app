package kr.rowan.motive_app.util.video;

import android.media.MediaPlayer;
import android.widget.MediaController;

public class VideoPlayerControl implements MediaController.MediaPlayerControl {

    private MediaPlayer videoPlayer;

    public VideoPlayerControl(MediaPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    @Override
    public void start() {
        videoPlayer.start();
    }

    @Override
    public void pause() {
        videoPlayer.pause();
    }

    @Override
    public int getDuration() {
        return videoPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        try {
            return videoPlayer.getCurrentPosition();
        }catch (IllegalStateException e){
            videoPlayer.reset();
            return videoPlayer.getCurrentPosition();
        }
    }

    @Override
    public void seekTo(int pos) {
        videoPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return videoPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
