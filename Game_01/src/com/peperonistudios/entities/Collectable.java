package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.peperonistudios.world.Camera;

public class Collectable extends Entity {
    
	public Collectable(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw,
            int maskh) {
        super(x, y, width, height, sprite, maskx, masky, maskw, maskh);
    }

    private int frames = 0, maxFrames = 15, index = 0, maxIndex = 4, offset = 0;

    public void tick() {
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index >= maxIndex) {
				index = 0;
			}
		}
		
		switch (index) {
			case 0:
				offset = 0;
				break;
		
			case 1:
				offset = 1;
				break;

			case 2:
				offset = 2;
				break;
			
			case 3:
				offset = 1;
				break;
		}
	}

    public void render(Graphics2D g2d) {
		g2d.drawImage(Entity.GROUND_SHADOW_EN, this.getX() - Camera.x, this.getY() - Camera.y + 2, null);
		g2d.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y - offset, null);
	
		//g2d.setColor(Color.red);
		//g2d.fillRect(this.getX() - Camera.x + maskx, this.getY() - Camera.y + masky, maskw, maskh);
    }
}
