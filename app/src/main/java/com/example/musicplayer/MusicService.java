package com.example.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.example.musicplayer.bean.Music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    MyReceiver serviceReceiver;
    ArrayList<Music> musicList;
    MediaPlayer mPlayer;
    // 当前的状态，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    int status = 0x11;
    // 记录当前正在播放的音乐
    Music currentSong;
    int index;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle args= intent.getBundleExtra("bundle");
        index = intent.getIntExtra("index",0);
        musicList = args.getParcelableArrayList("musicList");
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        for(Music music:musicList){
            metadataRetriever.setDataSource(music.getFilePath());
            music.setCoverImage(metadataRetriever.getEmbeddedPicture());
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate(){
        super.onCreate();
        serviceReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.CTL_ACTION);
        registerReceiver(serviceReceiver, filter);
        mPlayer = new MediaPlayer();
//        播放完成
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                index++;
                if (index+1 > musicList.size()){
                    index = 0;
                }
                currentSong = musicList.get(index);
                //发送广播通知Activity更改文本框
                Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
                sendIntent.putExtra("index", index);
                // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                sendBroadcast(sendIntent);
                // 准备并播放音乐
                play(musicList.get(index));
            }
        });
    }


    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            int control = intent.getIntExtra("control", -1);
            index = intent.getIntExtra("index",index);
            status = intent.getIntExtra("playStatus",status);
            Log.d("msg",String.valueOf(index));
            Log.d("msg",String.valueOf(status));
            switch (control){
                // 播放或暂停
                case 1:
                    // 原来处于没有播放状态
                    if (status == 0x11){
                        // 准备并播放音乐
                        play(musicList.get(index));
                        status = 0x12;
                    }
                    // 原来处于播放状态
                    else if (status == 0x12){
                        // 暂停
                        mPlayer.pause();
                        // 改变为暂停状态
                        status = 0x13;
                    }
                    // 原来处于暂停状态
                    else if (status == 0x13){
                        // 播放
                        mPlayer.start();
                        // 改变状态
                        status = 0x12;
                    }
                    break;
                // 停止声音
                case 2:
                    // 如果原来正在播放或暂停
                    if (status == 0x12 || status == 0x13){
                        // 停止播放
                        mPlayer.stop();
                        status = 0x11;
                    }
                //上一首，下一首
                case 3:
                    play(musicList.get(index));
                    status = 0x12;
            }
            // 广播通知Activity更改图标、文本框
            Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
            sendIntent.putExtra("playStatus", status);
            sendIntent.putExtra("index", index);

            // 发送广播，将被Activity组件中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);
        }
    }
//   播放音乐
    void play(Music music){
        try
        {
            mPlayer.reset();
            Log.d("msg",music.getFilePath());
            mPlayer.setDataSource(music.getFilePath());
            // 使用MediaPlayer加载指定的声音文件。
            // 准备声音
            mPlayer.prepare();
            // 播放
            mPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
