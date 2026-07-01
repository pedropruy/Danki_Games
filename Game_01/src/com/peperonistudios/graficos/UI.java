package com.peperonistudios.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.peperonistudios.entities.Player;
import com.peperonistudios.main.Game;

public class UI {

	private static BufferedImage playerHealthy = Game.spritesheet.getSprite(128, 32, 16, 16);
	private static BufferedImage playerDamaged = Game.spritesheet.getSprite(144, 32, 16, 16);
    
    public void render(Graphics g) {
        // Renderizando Barra de Vida
        for (int i = 0; i < Player.max_life; i++) {
            if (i >= Player.life) g.drawImage(playerDamaged, 5 + (16*i), 5, null);
            else g.drawImage(playerHealthy, 5 + (16*i), 5, null);
        }
        /*g.setColor(Color.GRAY);
        g.fillRect(10, 5, 50, 10);

        if (Player.life/Player.max_life > 0.5) g.setColor(Color.GREEN);
        else if (Player.life/Player.max_life > 0.25) g.setColor(Color.YELLOW);
        else g.setColor(Color.RED);
        g.fillRect(10, 5, (int)((Player.life/Player.max_life)*50), 10);*/
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("arial", Font.BOLD,8));
        g.drawString((int)Player.life+"/"+(int)Player.max_life,60,15);

        // Renderizando Barra de Mana
        g.setColor(Color.GRAY);
        g.fillRect(9, 26, 40, 5);

        g.setColor(Color.BLUE);
        g.fillRect(9, 26, (int)((Player.mana/Player.max_mana)*40), 5);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("arial", Font.BOLD,8));
        g.drawString((int)Player.mana+"/"+(int)Player.max_mana,17,31);
    }
}
