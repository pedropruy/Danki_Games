package com.peperonistudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;

public class Entity {

	public static BufferedImage LIFE_ELIXIR_EN = Game.spritesheet.getSprite(144, 16, 16, 16);
	public static BufferedImage MANA_ELIXIR_EN = Game.spritesheet.getSprite(144, 0, 16, 16);
	public static BufferedImage PURP_SLIME_EN = Game.spritesheet.getSprite(0,32, 16, 16);
	public static BufferedImage FIRE_BALL_EN = Game.spritesheet.getSprite(0,48, 16, 16);
	public static BufferedImage FIRE_BOOK_EN = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage ICE_BOOK_EN = Game.spritesheet.getSprite(128, 16, 16, 16);
	
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	private BufferedImage sprite;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
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
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
