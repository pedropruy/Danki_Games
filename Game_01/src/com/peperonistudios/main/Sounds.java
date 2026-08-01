package com.peperonistudios.main;

import java.io.*;
import javax.sound.sampled.*;

import com.peperonistudios.main.Sounds.Clips;

public class Sounds {
    
    public static class Clips {
        public Clip[] clips;
        private int p;
        private int count;

        public Clips(byte[] buffer, int count) throws LineUnavailableException,
                     IOException, UnsupportedAudioFileException {
            if (buffer == null) return;
            
            clips = new Clip[count];
            this.count = count;

            for (int i = 0; i < count; i++) {
                clips[i] = AudioSystem.getClip();
                clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));

            }

        }

        public void play() {
            if (clips == null) return;
            clips[p].stop();
            clips[p].setFramePosition(0);
            clips[p].start();
            p++;
            if (p >= count) p = 0;
        }

        public void loop() {
            if (clips == null) return;
            clips[p].stop();
            clips[p].setFramePosition(0);
            clips[p].loop(Clip.LOOP_CONTINUOUSLY);
        }

        public void stop() {
            if (clips == null) return;
            if (clips[p].isRunning()) clips[p].stop();
        }
    }

    // Todos os soms q eu fiz tão baixos!
    public static final Clips musicBackground = load ("/Sounds/MainTheme.wav", 1);
    public static final Clips hurtEffect = load("/Sounds/hitHurt.wav", 1);
    public static final Clips jumpEffect = load("/Sounds/JumpingUp.wav", 1);
    public static final Clips stairsEffect = load ("/Sounds/Stairs.wav", 1);
    public static final Clips gotBook = load("/Sounds/BookCollected.wav", 1);
    public static final Clips switchEffect = load("/Sounds/Click.wav", 1);
    public static final Clips deathEffect = load ("/Sounds/Death.wav", 1);
    public static final Clips doorEffect = load("/Sounds/DoorOpen.wav", 1);
    public static final Clips healEffect = load("/Sounds/HealUp.wav", 1);

    private static Clips load (String name, int count) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));

            byte[] buffer = new byte[1924];
            int read = 0;
            while((read = dis.read(buffer)) >= 0) {
                baos.write(buffer, 0, read);
            }
            dis.close();
            byte[] data = baos.toByteArray();
            return new Clips(data, count);
        } catch (Exception e) {
            try {
                return new Clips(null, 0);
            } catch (Exception ee) {
                return null;
            }
        }
    }


}
