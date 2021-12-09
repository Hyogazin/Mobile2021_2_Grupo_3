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
import com.example.mobile2021_02_grupo03.databinding.RowAllSongsBinding;
import com.example.mobile2021_02_grupo03.databinding.RowRecentSongsBinding;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import java.util.ArrayList;
import java.util.Collection;

public class SongsAdapterDataBinding extends RecyclerView.Adapter<SongsAdapterDataBinding.ViewHolder> implements Filterable {

    private SongListPresenter songListPresenter;
    private ArrayList<Song> songs;

    public SongsAdapterDataBinding(SongListPresenter songListPresenter, ArrayList<Song> songs){
        this.songListPresenter = songListPresenter;
        this.songs = new ArrayList<>(songs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(songListPresenter.selectedList == songListPresenter.songs){
            RowAllSongsBinding layoutAllSongs = RowAllSongsBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutAllSongs);
        } else{
            RowRecentSongsBinding layoutRecentSongs = RowRecentSongsBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutRecentSongs);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(songListPresenter.selectedLayout != 0){
            setAnimation(holder.itemView);
        }
        Song song = songListPresenter.selectedList.get(holder.getAdapterPosition());
        if(songListPresenter.selectedList == songListPresenter.songs){
            holder.layoutAllSongs.setSong(song);
            holder.layoutAllSongs.executePendingBindings();

            if(holder.layoutAllSongs.getSong().getTitle().equals(songListPresenter.selectedName)){
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
                songListPresenter.onItemClick(holder.getAdapterPosition());
                songListPresenter.songs.clear();
                songListPresenter.songs.addAll(songs);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songListPresenter.selectedList.size();
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
            songListPresenter.songs.clear();
            songListPresenter.songs.addAll((Collection<? extends Song>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowAllSongsBinding layoutAllSongs;
        RowRecentSongsBinding layoutRecentSongs;

        public ViewHolder(@NonNull RowAllSongsBinding layoutAllSongs) {
            super(layoutAllSongs.getRoot());
            this.layoutAllSongs = layoutAllSongs;
        }

        public ViewHolder(@NonNull RowRecentSongsBinding layoutRecentSongs) {
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
