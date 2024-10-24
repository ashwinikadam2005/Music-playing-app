// File: SongAdapter.java
package com.example.musicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    // Interface for click handling
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Song> songList;
    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    // Constructor
    public SongAdapter(List<Song> songList, OnItemClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    // ViewHolder class
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public ImageView albumArt;
        public TextView title;
        public TextView artist;

        public SongViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.item_album_art);
            title = itemView.findViewById(R.id.item_song_title);
            artist = itemView.findViewById(R.id.item_artist_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songList.get(position);
        holder.albumArt.setImageResource(currentSong.getAlbumArtResource());
        holder.title.setText(currentSong.getTitle());
        holder.artist.setText(currentSong.getArtist());

        // Highlight the selected item
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.selected_item));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Method to set selected position
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }
}
