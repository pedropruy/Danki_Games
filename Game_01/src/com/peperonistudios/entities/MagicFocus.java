package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;

public class MagicFocus extends Entity {
    
    private int frames = 0, maxFrames = 10, index = 0, maxIndex = 4, offset = 0;
    Player playerOnwer;
    int xFocus = 0, yFocus = 0;

    public MagicFocus(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw,
            int maskh, Player player) {
        super(x, y, width, height, Entity.MAGIC_FOCUS_EN, maskx, masky, maskw, maskh);
        this.playerOnwer = player;
    }

    public void tick() {
        if (playerOnwer.dir == playerOnwer.right_dir) {
			xFocus = 13; yFocus = 7;
		} else if (playerOnwer.dir == playerOnwer.left_dir) {
			xFocus = -5; yFocus = 7;
		} else if (playerOnwer.dir == playerOnwer.up_dir) {
			xFocus = 4; yFocus = -2;
		} else if (playerOnwer.dir == playerOnwer.down_dir) {
			xFocus = 4; yFocus = 10;
		}

        setX(Game.player.x + xFocus);
        setY(Game.player.y + yFocus - playerOnwer.z - offset);

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
		if (playerOnwer.dir != playerOnwer.up_dir) {
            BufferedImage spriteAtual = null;

			switch (Player.knowSpell.get(playerOnwer.useSpell)) {
				case "basic":
                    spriteAtual = Entity.MAGIC_FOCUS_EN;
					break;

				case "fire":
					spriteAtual = Entity.FIRE_FOCUS_EN;
					break;
				
				case "ice":
					spriteAtual = Entity.ICE_FOCUS_EN;
				break;
			}

            g2d.drawImage(spriteAtual, this.getX() - Camera.x,
                          this.getY() - Camera.y, null);
		}

        //g2d.setColor(Color.red);
		//g2d.fillRect(this.getX() - Camera.x + maskx, this.getY() - Camera.y + masky, maskw, maskh);
    }
}
