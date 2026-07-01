package com.peperonistudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;

public class Projectile extends Entity{
    
    private int dx;
    private int dy;
    private double speed = 3;

    private int duration = 0, max_duration = 20;

    public Projectile(int x, int y, int width, int height, BufferedImage sprite,
                      int maskx, int masky, int maskw, int maskh, int dx, int dy, int max_duration) {
		super(x, y, width, height, sprite, maskx, masky, maskw, maskh);

        this.dx = dx;
        this.dy = dy;
        this.max_duration = max_duration;
	}

    public void tick() {
        x += dx * speed;
        y += dy * speed;
        duration++;
        if (duration == max_duration) {
            Game.projectiles.remove(this);
            return;
        }
    }

    public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
