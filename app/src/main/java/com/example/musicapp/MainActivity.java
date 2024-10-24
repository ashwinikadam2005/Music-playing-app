// File: MainActivity.java
package com.example.musicapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101; // Define your request code here

    MediaPlayer mediaPlayer;
    Button playPauseButton, nextButton, prevButton;
    ImageView albumArt;
    TextView songTitle, artistName;
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable runnable;

    RecyclerView recyclerView;
    SongAdapter songAdapter;
    List<Song> songList;

    int currentSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request permissions
        checkPermissions();

        // Initialize views
        playPauseButton = findViewById(R.id.btn_play_pause);
        nextButton = findViewById(R.id.btn_next);
        prevButton = findViewById(R.id.btn_previous);
        albumArt = findViewById(R.id.album_art);
        songTitle = findViewById(R.id.song_title);
        artistName = findViewById(R.id.artist_name);
        seekBar = findViewById(R.id.seekBar);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize song list
        initializeSongList();

        // Set up RecyclerView
        setUpRecyclerView();

        // Initialize MediaPlayer with the first song
        initializeMediaPlayer();

        // Play or pause music on button click
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseButton.setText("Play");
                }
                else {
                    mediaPlayer.start();
                    playPauseButton.setText("Pause");
                    updateSeekBar();
                }
            }
        });

        // Next button click
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playNextSong();
            }
        });

        // Previous button click
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playPreviousSong();
            }
        });

        // SeekBar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(fromUser && mediaPlayer != null){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                // Optional: Pause updating the SeekBar
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                // Resume updating the SeekBar
                updateSeekBar();
            }
        });
    }

    private void initializeSongList(){
        songList = new ArrayList<>();
        songList.add(new Song(R.raw.song1, "Phir Aur Kya Chahiye", "Arijit Singh", R.drawable.album1));
        songList.add(new Song(R.raw.song2, "Makhna", "Arijit Singh", R.drawable.album2));
        songList.add(new Song(R.raw.song3, "Dilbar", "Neha Kakkar", R.drawable.album3));
        songList.add(new Song(R.raw.song4, "O Saki Saki", "Neha Kakkar & Tulsi Kumar", R.drawable.album4));
        songList.add(new Song(R.raw.song5, "Hasi Ban Gaye", "Ami Mishra", R.drawable.album5));
        songList.add(new Song(R.raw.song6, "Dil Ko Karar Aaya", "Neha Kakkar & Yasser Desai", R.drawable.album6));
        songList.add(new Song(R.raw.song7, "Janam Janam", "Arjit Singh", R.drawable.album7));
        songList.add(new Song(R.raw.song8, "Tum Kya Mile", "Arjit Singh & Shreya Ghoshal", R.drawable.album8));
        songList.add(new Song(R.raw.song10, "Baatein Ye Kabhi Na", "Arjit Singh & Jeet Ganhuly", R.drawable.album10));
        songList.add(new Song(R.raw.song11, "Raanjhanaa", "A.R.Rahman", R.drawable.album11));




        // Add more songs as needed
    }

    private void setUpRecyclerView(){
        songAdapter = new SongAdapter(songList, new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playSelectedSong(position);
            }
        });
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void playSelectedSong(int position){
        if(position >= 0 && position < songList.size()){
            currentSongIndex = position;
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            initializeMediaPlayer();
            mediaPlayer.start();
            playPauseButton.setText("Pause");
            recyclerView.scrollToPosition(currentSongIndex);
            songAdapter.setSelectedPosition(currentSongIndex); // Highlight the selected song
            Toast.makeText(this, "Now Playing: " + songList.get(currentSongIndex).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeMediaPlayer(){
        Song currentSong = songList.get(currentSongIndex);
        mediaPlayer = MediaPlayer.create(this, currentSong.getSongResource());
        mediaPlayer.setLooping(false);
        updateSongDetails();

        // Set SeekBar max to song duration
        seekBar.setMax(mediaPlayer.getDuration());

        // Start updating the SeekBar
        updateSeekBar();

        // Handle song completion
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp){
                playNextSong();
            }
        });
    }

    private void updateSongDetails(){
        Song currentSong = songList.get(currentSongIndex);
        songTitle.setText(currentSong.getTitle());
        artistName.setText(currentSong.getArtist());
        albumArt.setImageResource(currentSong.getAlbumArtResource());
    }

    private void playNextSong(){
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        initializeMediaPlayer();
        mediaPlayer.start();
        playPauseButton.setText("Pause");
        recyclerView.scrollToPosition(currentSongIndex);
        songAdapter.setSelectedPosition(currentSongIndex); // Highlight the selected song
        Toast.makeText(this, "Now Playing: " + songList.get(currentSongIndex).getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void playPreviousSong(){
        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        initializeMediaPlayer();
        mediaPlayer.start();
        playPauseButton.setText("Pause");
        recyclerView.scrollToPosition(currentSongIndex);
        songAdapter.setSelectedPosition(currentSongIndex); // Highlight the selected song
        Toast.makeText(this, "Now Playing: " + songList.get(currentSongIndex).getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void updateSeekBar(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // Optionally, initialize the playlist or other features that require permission
            }
            else{
                Toast.makeText(this, "Permission Denied. App cannot function without audio permissions.", Toast.LENGTH_LONG).show();
                // Optionally, disable playback features or close the app
            }
        }
    }
}
