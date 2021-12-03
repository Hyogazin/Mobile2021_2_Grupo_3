package com.example.mobile2021_02_grupo03.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import com.example.mobile2021_02_grupo03.view.MusicListActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder>{

    private MusicAdapterListener listener;
    private ArrayList<Song> songs;
    public SongListPresenter songListPresenter;

    public SongsAdapter(ArrayList<Song> songs, SongListPresenter songListPresenter){
        this.songs = songs;
        this.songListPresenter = songListPresenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(songListPresenter.selectedLayout == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recent_musics, parent, false);
        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(songListPresenter.selectedLayout != 0){
            setAnimation(holder.itemView, holder.getAdapterPosition());
        }

        Song song = songs.get(holder.getAdapterPosition());
        TextView songName = holder.itemView.findViewById(R.id.txtSongName);

        songName.setText(song.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holder.getAdapterPosition());
                songListPresenter.selectedLayout = 0;
                notifyDataSetChanged();
            }
        });
        if(songListPresenter.selectedLayout == 2){
            holder.itemView.findViewById(R.id.background).setBackgroundResource(R.drawable.list_bg_recent_musics);
            holder.itemView.findViewById(R.id.txtSongName).setSelected(false);
        } else{
            if(songListPresenter.selectedName.equals(songs.get(position).getTitle())){
                songListPresenter.selectedPosition = holder.getAdapterPosition();
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
