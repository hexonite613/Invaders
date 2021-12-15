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
            pathName = Audio.class.getResource("").getPath().replace("/engine/", "/") + name + ".wav";
            clip = AudioSystem.getClip();
            audioFile = new File(System.getProperty("user.dir") + "/sound/" + name + ".wav");
            System.out.println(System.getProperty("user.dir"));
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