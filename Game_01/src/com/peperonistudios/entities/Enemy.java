package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Enemy extends Entity{

	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = down_dir;
	private double speed = 1;

	// Máscara de colisão
	private int maskx = 2, masky = 3, maskw = 12, maskh = 11;
	
	private int frames = 0, maxFrames = 30, index = 0, maxIndex = 1;
	private boolean moved = false;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	private BufferedImage[] upEnemy;
	private BufferedImage[] downEnemy;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);

		rightEnemy = new BufferedImage[2];
		leftEnemy = new BufferedImage[2];
		upEnemy = new BufferedImage[2];
		downEnemy = new BufferedImage[2];

		for(int i = 0; i < 2; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(96+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite(64+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			upEnemy[i] = Game.spritesheet.getSprite(32+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			downEnemy[i] = Game.spritesheet.getSprite(0+(i*16), 32, 16, 16);
		}
	}

	public void tick () {
		if (Game.rand.nextInt(100) < 30) {
			if ((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
				&& !isColliding((int)(x+speed), this.getY())) {
				moved = true;
				dir = right_dir;
				x += speed;
			}
			else if ((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColliding((int)(x-speed), this.getY())) {
				moved = true;
				dir = left_dir;
				x -= speed;
			}
			else if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColliding(this.getX(), (int)(y+speed))) {
				moved = true;
				dir = down_dir;
				y += speed;
			}
			else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColliding(this.getX(), (int)(y-speed))) {
				moved = true;
				dir = up_dir;
				y -= speed;
			}
		}

		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
	}

	public boolean isColliding (int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) continue;
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}

	public void render(Graphics g) {
		if(dir == right_dir) {
			g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else if(dir == left_dir) {
			g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else if(dir == up_dir) {
			g.drawImage(upEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else if(dir == down_dir) {
			g.drawImage(downEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}
}
