package com.example;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HomeController {

    @FXML private ImageView currentSongArt;
    @FXML private Label currentSongTitle;
    @FXML private Label currentSongArtist;
    @FXML private Slider progressSlider;
    @FXML private Button playButton;
    @FXML private ImageView playPauseIcon;
    @FXML private javafx.scene.layout.GridPane recentlyPlayedContainer;
    @FXML private VBox playlistContainer;
    @FXML private Button createPlaylistButton;
    @FXML private TextField searchField;

    private MediaPlayer mediaPlayer;
    private List<Song> playlist = new ArrayList<>();
    private final List<Song> allSongs = new ArrayList<>();
    private final List<Playlist> playlists = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    private Image playImage;
    private Image pauseImage;
    private Image backImage;
    private Image skipImage;
    private Image defaultAlbumArt;

    private final List<Song> likedSongs = new ArrayList<>();
    private Playlist likedSongsPlaylist;

    @FXML
    public void initialize() {
        try {
            playImage = new Image(getClass().getResource("play.png").toString());
            pauseImage = new Image(getClass().getResource("pause.png").toString());
            backImage = new Image(getClass().getResource("back.png").toString());
            skipImage = new Image(getClass().getResource("skip.png").toString());
            defaultAlbumArt = new Image(getClass().getResource("weeknd.jpg").toString());

            setupPlayerControls();
            initializePlaylist();
            populatePlaylists();

            // --- Add this for search ---
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldText, newText) -> {
                    filterSongList(newText);
                });
            }
            // ---------------------------

            // Add Liked Songs playlist
            likedSongsPlaylist = new Playlist("Liked Songs", likedSongs);
            playlists.add(likedSongsPlaylist);
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
    URL song1Url = getClass().getResource("SaveyourTears.mp3");
    URL song2Url = getClass().getResource("Tetoris.mp3");
    URL song3Url = getClass().getResource("BlindingLights.mp3");
    URL song4Url = getClass().getResource("ShapeOfYou.mp3");
    URL song5Url = getClass().getResource("UptownFunk.mp3");
    URL song6Url = getClass().getResource("RollingInTheDeep.mp3");
    URL song7Url = getClass().getResource("Happy.mp3");
    URL song8Url = getClass().getResource("CantStopTheFeeling.mp3");
    URL song9Url = getClass().getResource("CountingStars.mp3");
    URL song10Url = getClass().getResource("DanceMonkey.mp3");

    Image weekndArt = new Image(getClass().getResource("weeknd.jpg").toString());
    Image tetoArt = new Image(getClass().getResource("Tetoris.jpg").toString());
    Image genericArt = new Image(getClass().getResource("generic.jpg").toString()); // create a generic image or reuse one

    if (song1Url != null) {
        allSongs.add(new Song("Save Your Tears", "The Weeknd", weekndArt, song1Url.toString()));
    }

    if (song2Url != null) {
        allSongs.add(new Song("Tetoris", "Kasane Teto", tetoArt, song2Url.toString()));
    }

    if (song3Url != null) {
        allSongs.add(new Song("Blinding Lights", "The Weeknd", weekndArt, song3Url.toString()));
    }

    if (song4Url != null) {
        allSongs.add(new Song("Shape of You", "Ed Sheeran", genericArt, song4Url.toString()));
    }

    if (song5Url != null) {
        allSongs.add(new Song("Uptown Funk", "Mark Ronson ft. Bruno Mars", genericArt, song5Url.toString()));
    }

    if (song6Url != null) {
        allSongs.add(new Song("Rolling in the Deep", "Adele", genericArt, song6Url.toString()));
    }

    if (song7Url != null) {
        allSongs.add(new Song("Happy", "Pharrell Williams", genericArt, song7Url.toString()));
    }

    if (song8Url != null) {
        allSongs.add(new Song("Can't Stop the Feeling!", "Justin Timberlake", genericArt, song8Url.toString()));
    }

    if (song9Url != null) {
        allSongs.add(new Song("Counting Stars", "OneRepublic", genericArt, song9Url.toString()));
    }

    if (song10Url != null) {
        allSongs.add(new Song("Dance Monkey", "Tones and I", genericArt, song10Url.toString()));
    }

    Playlist defaultPlaylist = new Playlist("All Songs", new ArrayList<>(allSongs));
    playlists.add(defaultPlaylist);
    setActivePlaylist(defaultPlaylist);
}


    private void setActivePlaylist(Playlist selected) {
        this.playlist = selected.getSongs();
        currentTrackIndex = 0;

        if (!playlist.isEmpty()) {
            updateNowPlayingUI(playlist.get(0));
        } else {
            currentSongArt.setImage(null);
            currentSongTitle.setText("No song");
            currentSongArtist.setText("");
        }

        populateSongList();
    }

    private void populateSongList() {
        recentlyPlayedContainer.getChildren().clear();
        int col = 0;
        int row = 0;
        for (int i = 0; i < playlist.size(); i++) {
            Song song = playlist.get(i);
            VBox songBox = createSongBox(song, i); // your method to create a song card

            recentlyPlayedContainer.add(songBox, col, row);

            col++;
            if (col == 3) { // 3 songs per row
                col = 0;
                row++;
            }
        }
    }

    private VBox createSongBox(Song song, int index) {
        VBox songBox = new VBox(5);
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

        // Like button
        Button likeButton = new Button(likedSongs.contains(song) ? "♥" : "♡");
        likeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-text-fill: #1DB954;");
        likeButton.setOnAction(e -> {
            if (likedSongs.contains(song)) {
                likedSongs.remove(song);
                likeButton.setText("♡");
            } else {
                likedSongs.add(song);
                likeButton.setText("♥");
            }
            populatePlaylists(); // Refresh playlists to update liked songs
        });

        songBox.getChildren().addAll(albumArt, title, artist, likeButton);
        songBox.setOnMouseClicked(event -> playSong(index));

        return songBox;
    }

    private void populatePlaylists() {
        playlistContainer.getChildren().clear();

        for (Playlist pl : playlists) {
            VBox playlistBox = new VBox(5);
            playlistBox.setStyle("-fx-background-color: #1DB954; -fx-background-radius: 10; -fx-padding: 5;");

            Label playlistLabel = new Label(pl.getName());
            playlistLabel.setTextFill(Color.WHITE);
            playlistLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 10; -fx-cursor: hand;");

            VBox songListBox = new VBox(5);
            songListBox.setVisible(false);

            for (int i = 0; i < pl.getSongs().size(); i++) {
                final int songIndex = i;
                Song song = pl.getSongs().get(i);

                Label songLabel = new Label("\u266A " + song.getTitle() + " - " + song.getArtist());
                songLabel.setTextFill(Color.WHITE);
                songLabel.setStyle("-fx-padding: 5 15;");
                songLabel.setCursor(Cursor.HAND);

                songLabel.setOnMouseClicked(e -> {
                    this.playlist = pl.getSongs();
                    currentTrackIndex = songIndex;
                    playSong(songIndex);
                });

                songListBox.getChildren().add(songLabel);
            }

            playlistLabel.setOnMouseClicked(event -> {
                setActivePlaylist(pl);
                songListBox.setVisible(!songListBox.isVisible());
            });

            playlistBox.getChildren().addAll(playlistLabel, songListBox);
            playlistContainer.getChildren().add(playlistBox);
        }
    }

    private void updateNowPlayingUI(Song song) {
        currentSongArt.setImage(song.getAlbumArt());
        currentSongTitle.setText(song.getTitle());
        currentSongArtist.setText(song.getArtist());
    }

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

            currentTrackIndex = index;
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

    @FXML
    private void createNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog("My Playlist");
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("Enter the name of your new playlist:");
        dialog.setContentText("Playlist name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) return;

            List<Song> selectedSongs = SongSelectionDialog.show(allSongs);
            if (selectedSongs == null || selectedSongs.isEmpty()) return;

            Playlist newPlaylist = new Playlist(name.trim(), selectedSongs);
            playlists.add(newPlaylist);
            populatePlaylists();
            setActivePlaylist(newPlaylist);
        });
    }

    private void filterSongList(String query) {
        recentlyPlayedContainer.getChildren().clear();
        List<Song> filtered = new ArrayList<>();
        for (Song song : playlist) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                song.getArtist().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(song);
            }
        }
        for (int i = 0; i < filtered.size(); i++) {
            final int index = i;
            Song song = filtered.get(i);

            VBox songBox = new VBox(5);
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
            songBox.setOnMouseClicked(event -> playSong(playlist.indexOf(song)));

            recentlyPlayedContainer.getChildren().add(songBox);
        }
    }

    public static class Song {
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

        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public Image getAlbumArt() { return albumArt; }
        public String getMediaUrl() { return mediaUrl; }
    }

    public static class Playlist {
        private final String name;
        private final List<Song> songs;

        public Playlist(String name, List<Song> songs) {
            this.name = name;
            this.songs = songs;
        }

        public String getName() { return name; }
        public List<Song> getSongs() { return songs; }
    }

    public static class SongSelectionDialog {
        public static List<Song> show(List<Song> allSongs) {
            Dialog<List<Song>> dialog = new Dialog<>();
            dialog.setTitle("Select Songs");
            dialog.setHeaderText("Choose songs to include in your playlist:");

            VBox content = new VBox(10);
            content.setPrefWidth(400);
            content.setStyle("-fx-padding: 10;");

            List<CheckBox> checkBoxes = new ArrayList<>();

            for (Song song : allSongs) {
                CheckBox cb = new CheckBox(song.getTitle() + " - " + song.getArtist());
                cb.setTextFill(Color.BLACK);
                cb.setSelected(true);
                checkBoxes.add(cb);
                content.getChildren().add(cb);
            }

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    List<Song> selected = new ArrayList<>();
                    for (int i = 0; i < allSongs.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selected.add(allSongs.get(i));
                        }
                    }
                    return selected;
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);
        }
    }
}
