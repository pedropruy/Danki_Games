package com.peperonistudios.main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    private Clip clip;

    public static final Sound musicBackground = new Sound("/Sounds/MainTheme.wav");
    public static final Sound hurtEffect = new Sound("/Sounds/hitHurt.wav");
    public static final Sound jumpEffect = new Sound("/Sounds/isJumping.wav");

    private Sound(String path) {
        try {
            URL url = getClass().getResource(path);
            
            if (url == null) {
                System.err.println("Som não encontrado no caminho: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) return;
        try {
            new Thread(() -> {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (clip == null) return;
        try {
            new Thread(() -> {
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}