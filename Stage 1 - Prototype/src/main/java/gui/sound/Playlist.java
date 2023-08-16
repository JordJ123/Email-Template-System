package gui.sound;

import file.ExtendedFile;
import file.SoundFile;
import file.directory.Directory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Represents a set of songs.
 * @author Jordan Jones
 */
public class Playlist {

    //Attributes
    private Sound[] songs;
    private Sound currentSong;
    private int currentSongIndex = 0;

    /**
     * Creates a playlist using a directory of sounds.
     * @param directory Directory of sound files
     * @throws IOException Thrown if a file can't be found
     */
    public Playlist(@NotNull Directory directory) throws IOException {
        ExtendedFile[] directoryFiles = directory.readFiles();
        Sound[] songs = new Sound[directoryFiles.length];
        for (int i = 0; i < directoryFiles.length; i++) {
            songs[i] = ((SoundFile) directoryFiles[i]).toSound();
        }
        setSongs(songs);
        setCurrentSong(getSongs()[getCurrentSongIndex()]);
    }

    /**
     * Set the songs to play.
     * @param songs Songs to play
     */
    private void setSongs(Sound[] songs) {
        this.songs = songs;
        for (Sound song : songs) {
            song.setOnEnd(this::nextSong);
        }
    }

    /**
     * Sets the current song to play.
     * @param currentSong Current song to play.
     */
    private void setCurrentSong(Sound currentSong) {
        this.currentSong = currentSong;
    }

    /**
     * Sets the current song index.
     * @param currentSongIndex Current song index
     */
    private void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    /**
     * Sets the current song's volume.
     * @param volume Current songs volume
     */
    public void setVolume(double volume) {
        getCurrentSong().setVolume(volume);
    }

    /**
     * Gets the songs in the playlist.
     * @return Songs in the playlist
     */
    private Sound[] getSongs() {
        return songs;
    }

    /**
     * Gets the current playing song.
     * @return Get current playing song
     */
    public Sound getCurrentSong() {
        return currentSong;
    }

    /**
     * Gets the index of the current song.
     * @return Current song index
     */
    private int getCurrentSongIndex() {
        return currentSongIndex;
    }

    /**
     * Gets the volume of playlist.
     * @return Volume of the playlist
     */
    public double getVolume() {
        return getCurrentSong().getVolume();
    }

    /**
     * Toggles if the song is playing or pausing.
     * @return True if the song starts playing
     */
    public boolean toggle() {
       return getCurrentSong().toggle();
    }

    /**
     * Sets the current song to the next song.
     */
    public void nextSong() {
        double previousSongVolume = getCurrentSong().getVolume();
        if (!(getCurrentSongIndex() == getSongs().length - 1)) {
            setCurrentSongIndex(getCurrentSongIndex() + 1);
        } else {
            setCurrentSongIndex(0);
        }
        setCurrentSong(getSongs()[getCurrentSongIndex()]);
        getCurrentSong().setVolume(previousSongVolume);
        toggle();
    }

    /**
     * Sets the current song to the previous song.
     */
    public void previousSong() {
        double previousSongVolume = getCurrentSong().getVolume();
        if (!(getCurrentSongIndex() == 0)) {
            setCurrentSongIndex(getCurrentSongIndex() - 1);
        } else {
            setCurrentSongIndex(getSongs().length - 1);
        }
        setCurrentSong(getSongs()[getCurrentSongIndex()]);
        getCurrentSong().setVolume(previousSongVolume);
        toggle();
    }

}
