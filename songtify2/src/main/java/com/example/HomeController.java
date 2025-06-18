package com.example;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HomeController {
    // UI Elements
    @FXML private ImageView currentSongArt;
    @FXML private Label currentSongTitle;
    @FXML private Label currentSongArtist;
    @FXML private Slider progressSlider;
    @FXML private Button playButton;
    @FXML private ImageView playPauseIcon;
    @FXML private HBox recentlyPlayedContainer;  // <-- Added for song list container
    
    // Media Player
    private MediaPlayer mediaPlayer;
    private List<Song> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean isPlaying = false;
    
    // Images
    private Image playImage;
    private Image pauseImage;
    private Image backImage;
    private Image skipImage;
    private Image defaultAlbumArt;

    @FXML
    public void initialize() {
        try {
            // Load images
            playImage = new Image(getClass().getResource("play.png").toString());
            pauseImage = new Image(getClass().getResource("pause.png").toString());
            backImage = new Image(getClass().getResource("back.png").toString());
            skipImage = new Image(getClass().getResource("skip.png").toString());
            defaultAlbumArt = new Image(getClass().getResource("weeknd.jpg").toString());
            
            // Setup player controls and playlist
            setupPlayerControls();
            initializePlaylist();
            
            // Populate the clickable song list UI
            populateSongList();
        } catch (Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupPlayerControls() {
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (progressSlider.isValueChanging() && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
            }
        });
    }

    private void initializePlaylist() {
    URL song1Url = getClass().getResource("wsong1.mp3");
    URL song2Url = getClass().getResource("wsong2.mp3");

    Image weekndArt = new Image(getClass().getResource("weeknd.jpg").toString());
    Image tetoArt = new Image(getClass().getResource("Tetoris.jpg").toString());

    if (song1Url != null) {
        playlist.add(new Song("Save Your Tears", "The Weeknd", weekndArt, song1Url.toString()));
    } else {
        System.err.println("Could not find wsong1.mp3");
    }

    if (song2Url != null) {
        playlist.add(new Song("Tetoris", "Kasane Teto", tetoArt, song2Url.toString()));
    } else {
        System.err.println("Could not find wsong2.mp3");
    }

    if (!playlist.isEmpty()) {
        updateNowPlayingUI(playlist.get(0));
    } else {
        System.err.println("Playlist is empty!");
    }
}


    // New method to populate the clickable song list
    private void populateSongList() {
        recentlyPlayedContainer.getChildren().clear();

        for (int i = 0; i < playlist.size(); i++) {
            final int index = i;
            Song song = playlist.get(i);

            VBox songBox = new VBox();
            songBox.setSpacing(5);
            songBox.setStyle("-fx-background-color: #282828; -fx-padding: 10; -fx-background-radius: 10;");
            songBox.setPrefWidth(150);
            songBox.setCursor(Cursor.HAND);

            ImageView albumArt = new ImageView(song.getAlbumArt());
            albumArt.setFitWidth(140);
            albumArt.setFitHeight(140);
            albumArt.setPreserveRatio(true);

            Label title = new Label(song.getTitle());
            title.setTextFill(Color.WHITE);
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            Label artist = new Label(song.getArtist());
            artist.setTextFill(Color.LIGHTGRAY);
            artist.setStyle("-fx-font-size: 12;");

            songBox.getChildren().addAll(albumArt, title, artist);

            // Play the selected song on click
            songBox.setOnMouseClicked(event -> playSong(index));

            recentlyPlayedContainer.getChildren().add(songBox);
        }
    }

    private void updateNowPlayingUI(Song song) {
        currentSongArt.setImage(song.getAlbumArt());
        currentSongTitle.setText(song.getTitle());
        currentSongArtist.setText(song.getArtist());
    }

    // Player Actions
    @FXML
    private void togglePlay() {
        if (playlist.isEmpty()) return;
        
        if (mediaPlayer == null) {
            playSong(currentTrackIndex);
            return;
        }
        
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseIcon.setImage(playImage);
        } else {
            mediaPlayer.play();
            playPauseIcon.setImage(pauseImage);
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void nextTrack() {
        if (playlist.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
        playSong(currentTrackIndex);
    }

    @FXML
    private void previousTrack() {
        if (playlist.isEmpty()) return;
        currentTrackIndex = (currentTrackIndex - 1 + playlist.size()) % playlist.size();
        playSong(currentTrackIndex);
    }

    private void playSong(int index) {
        try {
            Song song = playlist.get(index);
            updateNowPlayingUI(song);
            
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            
            Media media = new Media(song.getMediaUrl());
            mediaPlayer = new MediaPlayer(media);
            setupMediaPlayerListeners();
            mediaPlayer.play();
            isPlaying = true;
            playPauseIcon.setImage(pauseImage);

            currentTrackIndex = index; // Update current track index!
        } catch (Exception e) {
            System.err.println("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupMediaPlayerListeners() {
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressSlider.isValueChanging()) {
                progressSlider.setValue(newTime.toSeconds());
            }
        });
        
        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        });
        
        mediaPlayer.setOnEndOfMedia(this::nextTrack);
    }

    // Song data class
    private static class Song {
        private final String title;
        private final String artist;
        private final Image albumArt;
        private final String mediaUrl;

        public Song(String title, String artist, Image albumArt, String mediaUrl) {
            this.title = title;
            this.artist = artist;
            this.albumArt = albumArt;
            this.mediaUrl = mediaUrl;
        }

        // Getters
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public Image getAlbumArt() { return albumArt; }
        public String getMediaUrl() { return mediaUrl; }
    }
}
