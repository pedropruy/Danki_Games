package com.peperonistudios.main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    private Clip clip;

    public static final Sound musicBackground = new Sound("music.wav");
    public static final Sound hurtEffect = new Sound("hitHurt.wav");
    public static final Sound jumpEffect = new Sound("isJumping.wav");

    private Sound(String name) {
        try {
            InputStream is = Sound.class.getResourceAsStream(name);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) return;
        try {
            new Thread() {
                public void run() {
                    clip.setFramePosition(0);
                    clip.start();
                }
            }.start();
        } catch (Throwable e) {}
    }

    public void loop() {
        if (clip == null) return;
        try {
            new Thread() {
                public void run() {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }.start();
        } catch (Throwable e) {}
    }
    
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}