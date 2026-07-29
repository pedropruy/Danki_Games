package com.peperonistudios.entities;

import java.awt.image.BufferedImage;

public class Elixir extends Collectable{

	public Elixir(int x, int y, int width, int height, BufferedImage sprite,
				  int maskx, int masky, int maskw, int maskh, int framesPerIndex) {
		super(x, y, width, height, sprite, maskx, masky, maskw, maskh);
		this.maxFrames = framesPerIndex;
	}
	
}
