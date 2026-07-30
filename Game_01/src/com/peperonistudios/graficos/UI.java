package com.peperonistudios.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.peperonistudios.entities.Player;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.World;

public class UI {

	private static BufferedImage playerHealthy = Game.spritesheet.getSprite(128, 32, 16, 16);
	private static BufferedImage playerDamaged = Game.spritesheet.getSprite(144, 32, 16, 16);

	private static BufferedImage iconBasic = Game.spritesheet.getSprite(0, 64, 16, 16);
	private static BufferedImage iconFire = Game.spritesheet.getSprite(32, 64, 16, 16);
	private static BufferedImage iconIce = Game.spritesheet.getSprite(64, 64, 16, 16);
   
	public static BufferedImage minimap = null;
    public static boolean showMinimap = false;
    public static int[] minimapPixels;

    private Lightmap lightmap = null;

    public void createLightmap(String path) {
        lightmap = new Lightmap(path);
    }

    public void createMinimap() {
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        minimapPixels = ((DataBufferInt) minimap.getRaster().getDataBuffer()).getData();
        showMinimap = true;
    }

    public void render(Graphics2D g2d) {
        if (lightmap != null) lightmap.applyLight();
        
        // Renderizando o preto abaixo dos icones
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, 66, 26);

        // Renderizando Barra de Vida
        for (int i = 0; i < Player.max_life; i++) {
            if (i >= Player.life) g2d.drawImage(playerDamaged, 3 + (16*i), 3, null);
            else g2d.drawImage(playerHealthy, 3 + (16*i), 3, null);
        }

        // Renderizando Barra de Mana
        g2d.setColor(Color.BLACK);
        g2d.fillRect(3, 17, 45, 6);

        g2d.setColor(new Color(0, 10, 68));
        g2d.fillRect(4, 18, 43, 4);

        g2d.setColor(Color.BLUE);
        g2d.fillRect(4, 18, (int)(((double)Player.mana/(double)Player.max_mana)*43), 4);
        
        //g2d.setColor(Color.WHITE);
        //g2d.setFont(Game.hearts);
        //g2d.drawString(Player.mana+"/"+Player.max_mana,10,24);

        // Renderizando Magia
        switch (Player.knowSpell.get(Game.player.useSpell)) {
			case "basic":
                g2d.drawImage(iconBasic, 52, 4, null);
			break;
			    
			case "fire":
                g2d.drawImage(iconFire, 51, 4, null);
			break;
					
			case "ice":
                g2d.drawImage(iconIce, 51, 5, null);
			break;
        }
        
        // Renderizando Minimapa
        if (showMinimap) {
            g2d.setColor(new Color(0, 0, 0, 175));
            g2d.fillRect(Game.HEIGHT - 40, 0, 40, 40);

            World.renderMinimap();
            g2d.drawImage(minimap, Game.WIDTH - minimap.getWidth() - 5, 5, null);
        }

    }
}
