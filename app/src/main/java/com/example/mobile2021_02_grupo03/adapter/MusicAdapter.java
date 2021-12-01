package com.example.mobile2021_02_grupo03.adapter;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.view.MusicListActivity;
import com.example.mobile2021_02_grupo03.view.PlayerActivity;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

    //private int selectedLayout = -1;
    private MusicAdapterListener listener;
    private ArrayList<Song> songs;

    public MusicAdapter(ArrayList<Song> songs){
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(MusicListActivity.selectedLayout == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recent_musics, parent, false);
        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*if(selectedLayout != MusicListActivity.selectedLayout){
            setAnimation(holder.itemView, position);
        }*/
        if(MusicListActivity.selectedLayout != 0){
            setAnimation(holder.itemView, position);
        }


        /*if(position == songs.size()-1){
            selectedLayout = MusicListActivity.selectedLayout;
        }*/
        Song song = songs.get(position);
        TextView songName = holder.itemView.findViewById(R.id.txtSongName);

        songName.setText(song.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        if(MusicListActivity.selectedLayout == 2){
            holder.itemView.findViewById(R.id.background).setBackgroundResource(R.drawable.list_bg_recent_musics);
            holder.itemView.findViewById(R.id.txtSongName).setSelected(false);
        } else{
            if(MusicListActivity.selectedName.equals(songs.get(position).getTitle())){
                holder.itemView.findViewById(R.id.background).setBackgroundResource(R.drawable.list_bg_pressed);
                holder.itemView.findViewById(R.id.txtSongName).setSelected(true);
            } else{
                holder.itemView.findViewById(R.id.background).setBackgroundResource(R.drawable.list_bg);
                holder.itemView.findViewById(R.id.txtSongName).setSelected(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setListener(MusicAdapterListener listener) {
        this.listener = listener;
    }

    public interface MusicAdapterListener{
        void onItemClick(int position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }
}
