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
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.presenter.SongListPresenter;
import java.util.ArrayList;
import java.util.Collection;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> implements Filterable {

    private SongListPresenter songListPresenter;

    public SongsAdapter(SongListPresenter songListPresenter){
        this.songListPresenter = songListPresenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(!SongData.selectedLayout.equals("recent")){
            RowAllSongsBinding layoutAllSongs = RowAllSongsBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutAllSongs);
        } else{
            RowRecentSongsBinding layoutRecentSongs = RowRecentSongsBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(layoutRecentSongs);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!SongData.selectedLayout.equals("recent")){
            holder.layoutAllSongs.setSong(SongData.selectedSongs.get(holder.getAdapterPosition()));
            holder.layoutAllSongs.executePendingBindings();
            if(SongData.selectedSong.getTitle().equals(SongData.selectedSongs.get(position).getTitle())){
                holder.layoutAllSongs.rowBackground.setBackgroundResource(R.drawable.list_bg_pressed);
            } else{
                holder.layoutAllSongs.rowBackground.setBackgroundResource(R.drawable.list_bg);
            }
        } else{
            holder.layoutRecentSongs.setSong(SongData.selectedSongs.get(holder.getAdapterPosition()));
            holder.layoutRecentSongs.executePendingBindings();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songListPresenter.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return SongData.selectedSongs.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Song> filteredList = new ArrayList<>();
            ArrayList<Song> songs = new ArrayList<>();

            if(SongData.selectedLayout.equals("all")) {
                songs = SongData.allSongs;
            } else if(SongData.selectedLayout.equals("streaming")){
                songs = SongData.streamingSongs;
            } else if(SongData.selectedLayout.equals("favorite")){
                songs = SongData.favoriteSongs;
            }else if(SongData.selectedLayout.equals("recent")){
                songs = SongData.recentSongs;
            }

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
            SongData.selectedSongs.clear();
            SongData.selectedSongs.addAll((Collection<? extends Song>) filterResults.values);
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
}
