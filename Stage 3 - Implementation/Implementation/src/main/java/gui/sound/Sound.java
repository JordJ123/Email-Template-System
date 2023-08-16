package gui.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**
 * Represents a sound to play on the window.
 * @author Jordan Jones
 */
public class Sound {

    //Attributes
    private String name;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    /**
     * Creates a sound object.
     * @param filePath File path of the sound object
     */
    public Sound(String filePath) {
        setName(filePath);
        setMediaPlayer(new MediaPlayer(new Media(
            new File(filePath).toURI().toString())));
        setIsPlaying(false);
        setVolume(1);
    }

    /**
     * Sets the name of the sound.
     * @param filePath File path of the sound
     */
    private void setName(String filePath) {
        this.name = new File(filePath).getName().split("\\.")[0];
    }

    /**
     * Sets the media player that plays the sound.
     * @param mediaPlayer Media player that plays the sound
     */
    private void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    /**
     * Sets if the sound is currently playing or not.
     * @param isPlaying True if playing.
     */
    private void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * Sets the volume of the sound.
     * @param volume Volume of the sound
     */
    public void setVolume(double volume) {
        getMediaPlayer().setVolume(volume);
        System.out.println(getVolume());
    }

    /**
     * Sets what code runs when the sound is finished.
     * @param runnable Code that runs when the sound is finished
     */
    public void setOnEnd(Runnable runnable) {
        getMediaPlayer().setOnEndOfMedia(runnable);
    }

    /**
     * Sets the name of the sound.
     * @return Name of the sound
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the media player that plays the sound.
     * @return Media player that plays the sound
     */
    private MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Gets if the sound is playing or not.
     * @return True if the sound is playing or not
     */
    private boolean getIsPlaying() {
        return isPlaying;
    }

    /**
     * Gets the current volume of the sound.
     * @return Current volume of the sound
     */
    public double getVolume() {
        return getMediaPlayer().getVolume();
    }

    /**
     * Toggles if the song is playing or not.
     * @return True if the song is playing
     */
    public boolean toggle() {
        if (getIsPlaying()) {
            getMediaPlayer().pause();
        } else {
            getMediaPlayer().play();
        }
        setIsPlaying(!getIsPlaying());
        return getIsPlaying();
    }

}
