package com.peperonistudios.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.peperonistudios.main.Game;

public class Lightmap {

	public BufferedImage lightmap;
	public int[] lightmapPixels;
    
    Lightmap(String path) {
        try {
			lightmap = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) { e.printStackTrace(); }
		lightmapPixels = new int[(lightmap.getWidth() * lightmap.getHeight())];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(),lightmapPixels, 0, lightmap.getWidth());
    }

    public void applyLight() {
		for (int xx = 0; xx < Game.WIDTH; xx++) {
			for (int yy = 0; yy < Game.HEIGHT; yy++) {
				if (lightmapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff) {
					Game.screenPixels[xx + (yy * Game.WIDTH)] = 0;
				}
			}
		}
	}
}
