package com.peperonistudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;

public class Tile {

	public static BufferedImage TILE_GRASS = Game.spritesheet.getSprite(0, 16, 16, 16);
	public static BufferedImage TILE_TREETOP = Game.spritesheet.getSprite(32, 16, 16, 16);
	public static BufferedImage TILE_SOLOTREE = Game.spritesheet.getSprite(16, 16, 16, 16);
	public static BufferedImage TILE_RIVER = Game.spritesheet.getSprite(80, 16, 16, 16);
	public static BufferedImage TILE_BRIDGEV = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage TILE_BRIDGEH = Game.spritesheet.getSprite(112, 16, 16, 16);
	public static BufferedImage TILE_FENCE = Game.spritesheet.getSprite(48, 16, 16, 16);
	public static BufferedImage TILE_FLOWER = Game.spritesheet.getSprite(64, 16, 16, 16);
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite,x - Camera.x,y - Camera.y,null);
	}
}

