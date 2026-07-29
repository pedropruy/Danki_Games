package com.peperonistudios.graficos;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.peperonistudios.entities.Player;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.World;

public class UI {

	private static BufferedImage playerHealthy = Game.spritesheet.getSprite(128, 32, 16, 16);
	private static BufferedImage playerDamaged = Game.spritesheet.getSprite(144, 32, 16, 16);

	private static BufferedImage speelBook = Game.spritesheet.getSprite(0, 64, 32, 16);
	private static BufferedImage iconFire = Game.spritesheet.getSprite(32, 64, 16, 16);
   
	public static BufferedImage minimap = null;
    public static int[] minimapPixels;

    private Lightmap lightmap = null;

    public void createLightmap(String path) {
        lightmap = new Lightmap(path);
    }

    public void createMinimap() {
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
        minimapPixels = ((DataBufferInt) minimap.getRaster().getDataBuffer()).getData();
    }

    public void render(Graphics2D g2d) {
        // Renderizando Barra de Vida
        for (int i = 0; i < Player.max_life; i++) {
            if (i >= Player.life) g2d.drawImage(playerDamaged, 5 + (16*i), 5, null);
            else g2d.drawImage(playerHealthy, 5 + (16*i), 5, null);
        }

        // Renderizando Barra de Mana
        g2d.setColor(Color.GRAY);
        g2d.fillRect(9, 26, 40, 5);

        g2d.setColor(Color.BLUE);
        g2d.fillRect(9, 26, (int)(((double)Player.mana/(double)Player.max_mana)*40), 5);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(Game.hearts);
        g2d.drawString(Player.mana+"/"+Player.max_mana,13,33);

        // Renderizando Minimapa
        if (minimap != null) {
            World.renderMinimap();
            g2d.drawImage(minimap, Game.WIDTH - minimap.getWidth() - 5, 5, null);
        }

        // Renderizando Magia
        g2d.drawImage(speelBook, 64, 8, null);
        g2d.drawImage(iconFire, 72, 3, null);


        if (lightmap != null) lightmap.applyLight();
    }
}
