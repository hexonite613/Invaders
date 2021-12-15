package engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Audio {
    private Clip clip;
    private File audioFile;
    private AudioInputStream audioInputStream;
    private String pathName;
    private boolean isLoop;

    public Audio(String name, boolean isLoop) {
        try {
            pathName = Audio.class.getResource("").getPath().replace("classes/java/main/engine/", "resources/main/audio/") + name + ".wav";
            System.out.println(pathName);
            clip = AudioSystem.getClip();
            audioFile = new File(pathName);
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            clip.open(audioInputStream);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        clip.setFramePosition(0);
        clip.start();
        if (isLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() { clip.stop(); }
}