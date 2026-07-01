package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;

public class Entity {

	public static BufferedImage LIFE_ELIXIR_EN = Game.spritesheet.getSprite(144, 16, 16, 16);
	public static BufferedImage MANA_ELIXIR_EN = Game.spritesheet.getSprite(144, 0, 16, 16);
	public static BufferedImage PURP_SLIME_EN = Game.spritesheet.getSprite(0,32, 16, 16);
	public static BufferedImage MAGIC_FOCUS_EN = Game.spritesheet.getSprite(128,48, 8, 8);
	public static BufferedImage BASIC_ATTACK_EN = Game.spritesheet.getSprite(0,48, 16, 16);
	public static BufferedImage FIRE_FOCUS_EN = Game.spritesheet.getSprite(136,48, 8, 8);
	public static BufferedImage FIRE_BOOK_EN = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage FIRE_BALL_EN = Game.spritesheet.getSprite(32,48, 16, 16);
	public static BufferedImage ICE_FOCUS_EN = Game.spritesheet.getSprite(128,56, 8, 8);
	public static BufferedImage ICE_BOOK_EN = Game.spritesheet.getSprite(128, 16, 16, 16);
	
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	protected BufferedImage sprite;

	protected int maskx, masky, maskw, maskh;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw, int maskh) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;

		this.maskx = maskx;
		this.masky = masky;
		this.maskw = maskw;
		this.maskh = maskh;
	}
	
	public void setMask(int maskx, int masky, int maskw, int maskh) {
		this.maskx = maskx;
		this.masky = masky;
		this.maskw = maskw;
		this.maskh = maskh;
	}

	public void setX(double newX) {
		this.x = newX;
	}
	
	public void setY(double newY) {
		this.y = newY;
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}

	public static boolean isColliding (Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.maskw, e1.maskh);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.maskw, e2.maskh);

		return e1Mask.intersects(e2Mask);
	}

	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		//g.setColor(Color.red);
		//g.fillRect(this.getX() - Camera.x + maskx, this.getY() - Camera.y + masky, maskw, maskh);
	}
}
