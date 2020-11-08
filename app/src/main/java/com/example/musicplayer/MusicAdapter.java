package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.bean.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<Music> musicList = new ArrayList<>();
    private Context context;
    private OnItemClickListener onItemClickListener;
    public MusicAdapter(List<Music> musicList,Context context){
        this.musicList=musicList;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemLongClick(View view, int pos);
        void onItemClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Music music = this.musicList.get(position);
        holder.textView.setText(music.getName());
        holder.tabView.setText("text");
        holder.imageView.setImageBitmap(music.getCoverImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClickListener.onItemLongClick(view,position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.musicList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView tabView;
         public ViewHolder(@NonNull View itemView) {
             super(itemView);
             imageView = itemView.findViewById(R.id.music_item_cover);
             textView = itemView.findViewById(R.id.music_item_name);
             tabView = itemView.findViewById(R.id.music_item_tab);
         }
     }
}
