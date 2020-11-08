package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayer.bean.Music;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MusicAdapter.OnItemClickListener {
    Music currentSong;
    AssetManager am;
    Typeface iconfont;
    TextView name,preButton,playButton,nextButton;
    ImageView coverImage;
    View bottomPlayer;
    RecyclerView recyclerView;
    ArrayList<Music> musicList;
    int index = 0;
    public static final String CTL_ACTION =
            "org.xr.action.CTL_ACTION";
    public static final String UPDATE_ACTION =
            "org.xr.action.UPDATE_ACTION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
//        请求存储权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        try {
            initRecyclerView();
            //注册广播启动服务
            PlayBroadcastReceiver playBroadcastReceiver = new PlayBroadcastReceiver();
            // 创建IntentFilter
            IntentFilter filter = new IntentFilter();
            // 指定BroadcastReceiver监听的Action
            filter.addAction(UPDATE_ACTION);
            // 注册BroadcastReceiver
            registerReceiver(playBroadcastReceiver, filter);
            Intent intent = new Intent(this, MusicService.class);
            Bundle args = new Bundle();
            args.putParcelableArrayList("musicList", musicList);
            intent.putExtra("bundle",args);
            intent.putExtra("index",index);
            // 启动后台Service
            startService(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initRecyclerView() throws IOException {
        am = getAssets();
        musicList=new ArrayList<>();

        recyclerView=(RecyclerView)findViewById(R.id.music_list);
        coverImage = (ImageView) findViewById(R.id.music_cover);
        name = (TextView)  findViewById(R.id.music_name);
        preButton = (TextView) findViewById(R.id.music_pre);
        preButton.setOnClickListener(this);
        playButton = (TextView) findViewById(R.id.music_play);
        playButton.setOnClickListener(this);
        nextButton = (TextView) findViewById(R.id.music_next);
        nextButton.setOnClickListener(this);
        bottomPlayer = findViewById(R.id.bottom_player);
        bottomPlayer.setOnClickListener(this);
//加载音乐list
        preButton.setTypeface(iconfont);
        preButton.setText("\ue6ac");
        playButton.setTypeface(iconfont);
        playButton.setText("\ue6ae");
        nextButton.setTypeface(iconfont);
        nextButton.setText("\ue6a9");
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        MusicAdapter musicAdapter = new MusicAdapter(musicList,MainActivity.this);
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(this);

//        扫描文件
        File files = new File(Environment.getExternalStorageDirectory().getPath()+"/Music/");
        for (String str:files.list()){
            if(str.endsWith(".mp3")||str.endsWith(".flac")){
                Music music = new Music(Environment.getExternalStorageDirectory().getPath()+"/Music/",str);
                musicList.add(music);
            }
        }


        currentSong = musicList.get(index);
        if (currentSong.getCoverImage() != null){
            coverImage.setImageBitmap(currentSong.getCoverImage());
            coverImage.setAdjustViewBounds(true);
        }else {
            Log.d("msg"," null");
        }
        name.setText(currentSong.getName());
    }

//    bottomPlayer点击事件
    @Override
    public void onClick(View view) {
        Intent intent = new Intent("org.xr.action.CTL_ACTION");
        switch (view.getId()){
            case R.id.music_pre:
                if(index>0){
                    index--;
                }else{
                    index = musicList.size()-1;
                }
                intent.putExtra("index", index);
                intent.putExtra("control", 3);
                break;
            case R.id.music_next:
                if(index+1<musicList.size()){
                    index++;
                }else{
                    index=0;
                }
                intent.putExtra("index", index);
                intent.putExtra("control", 3);
                break;
            case R.id.music_play:
                intent.putExtra("control", 1);
                intent.putExtra("index", index);
                break;
            case R.id.bottom_player:
                Intent intentBottom = new Intent(view.getContext(), PlayActivity.class);
                view.getContext().startActivity(intentBottom);
                break;
        }
        sendBroadcast(intent);
    }



// recyclerView长按事件
    @Override
    public void onItemLongClick(View view, int pos) {
        Toast.makeText(MainActivity.this,String.valueOf(pos)+":ItemLongClick",Toast.LENGTH_SHORT).show();
    }
// recyclerView单击事件
    @Override
    public void onItemClick(View view, int pos) {
        Toast.makeText(MainActivity.this,String.valueOf(pos)+":ItemClick",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(view.getContext(), PlayActivity.class);
//        view.getContext().startActivity(intent);
        Intent broadIntent = new Intent("org.xr.action.CTL_ACTION");
//      播放新曲目
        broadIntent.putExtra("control",3);
        broadIntent.putExtra("index",pos);
        sendBroadcast(broadIntent);
    }

//    广播
    class PlayBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            // 获取Intent中的update消息，update代表播放状态
            int status = intent.getIntExtra("playStatus", -1);
            // 获取Intent中的current消息，current代表当前正在播放的歌曲
            index = intent.getIntExtra("index",index);
            currentSong = musicList.get(index);
            if (currentSong!=null){
                name.setText(currentSong.getName());
                coverImage.setImageBitmap(currentSong.getCoverImage());
            }
            switch (status){
                //未播放状态
                case 0x11:
                    //若为播放状态，改为停止
                    if(playButton.getText() == IconFont.PLAY.getValue()){
                        playButton.setText(IconFont.STOP.getValue());
                    }
                    status = 0x11;
                    break;
                // 控制系统进入播放状态
                case 0x12:
                    // 播放状态下设置使用暂停图标
                    playButton.setText(IconFont.PLAY.getValue());
                    // 设置当前状态
                    status = 0x12;
                    break;
                // 控制系统进入暂停状态
                case 0x13:
                    // 暂停状态下设置使用播放图标
                    playButton.setText(IconFont.STOP.getValue());
                    // 设置当前状态
                    status = 0x13;
                    break;
            }
        }
    }
}