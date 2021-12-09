package com.example.mobile2021_02_grupo03.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.databinding.RowAllSongsDatabindingBinding;
import com.example.mobile2021_02_grupo03.databinding.RowRecentSongsDatabindingBinding;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenterDataBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class SongsAdapterDataBinding extends RecyclerView.Adapter<SongsAdapterDataBinding.ViewHolder> implements Filterable {

    private SongListPresenterDataBinding songListPresenterDataBinding;
    private ArrayList<Song> songs;

    public SongsAdapterDataBinding(SongListPresenterDataBinding songListPresenterDataBinding, ArrayList<Song> songs){
        this.songListPresenterDataBinding = songListPresenterDataBinding;
        this.songs = new ArrayList<>(songs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(songListPresenterDataBinding.selectedList == songListPresenterDataBinding.songs){
            RowAllSongsDatabindingBinding layoutAllSongs = RowAllSongsDatabindingBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutAllSongs);
        } else{
            RowRecentSongsDatabindingBinding layoutRecentSongs = RowRecentSongsDatabindingBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutRecentSongs);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(songListPresenterDataBinding.selectedLayout != 0){
            setAnimation(holder.itemView);
        }
        Song song = songListPresenterDataBinding.selectedList.get(holder.getAdapterPosition());
        if(songListPresenterDataBinding.selectedList == songListPresenterDataBinding.songs){
            holder.layoutAllSongs.setSong(song);
            holder.layoutAllSongs.executePendingBindings();

            if(holder.layoutAllSongs.getSong().getTitle().equals(songListPresenterDataBinding.selectedName)){
                holder.layoutAllSongs.rowBackground.setBackgroundResource(R.drawable.list_bg_pressed);
                holder.layoutAllSongs.rowSongName.setSelected(true);
            } else{
                holder.layoutAllSongs.rowBackground.setBackgroundResource(R.drawable.list_bg);
            }
        } else{
            holder.layoutRecentSongs.setSong(song);
            holder.layoutRecentSongs.executePendingBindings();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songListPresenterDataBinding.onItemClick(holder.getAdapterPosition());
                songListPresenterDataBinding.songs.clear();
                songListPresenterDataBinding.songs.addAll(songs);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songListPresenterDataBinding.selectedList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Song> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(songs);
            } else{
                for(Song song: songs){
                    if(song.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(song);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            songListPresenterDataBinding.songs.clear();
            songListPresenterDataBinding.songs.addAll((Collection<? extends Song>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowAllSongsDatabindingBinding layoutAllSongs;
        RowRecentSongsDatabindingBinding layoutRecentSongs;

        public ViewHolder(@NonNull RowAllSongsDatabindingBinding layoutAllSongs) {
            super(layoutAllSongs.getRoot());
            this.layoutAllSongs = layoutAllSongs;
        }

        public ViewHolder(@NonNull RowRecentSongsDatabindingBinding layoutRecentSongs) {
            super(layoutRecentSongs.getRoot());
            this.layoutRecentSongs = layoutRecentSongs;
        }
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(500);
        viewToAnimate.startAnimation(animation);
    }
}
