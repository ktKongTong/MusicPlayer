package com.example.musicplayer;

import android.media.MediaPlayer;

import com.example.musicplayer.bean.Music;

import java.io.IOException;

public class PlayUtil {
    MediaPlayer mediaPlayer;
    Music music;
    public PlayUtil(final MediaPlayer mediaPlayer){
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mPlayer, int i, int i1) {
                mPlayer.release();
                mediaPlayer.reset();
                return false;
            }
        });
        this.mediaPlayer = mediaPlayer;
    }
//  播放新曲目
    public void play(Music music) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(music.getFilePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
//  暂停
    public void stop(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
//  继续播放
    public void continuePlay(){
        mediaPlayer.start();
    }
//  拖动进度条
    public void dragProcess(int msec){
        mediaPlayer.seekTo(msec);

    }

}
