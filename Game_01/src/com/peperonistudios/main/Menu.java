package com.peperonistudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Menu {
    
    public String[] options = {"novo jogo", "carregar jogo", "sair"};
    public int currentOption = 0;
    public int maxOption = options.length - 1;

    public boolean up = false, down = false, optionSelected = false;

    public boolean pause = false;

    public void tick() {
        if (up) {
            up = false;
            currentOption--;
            if (currentOption < 0)
                currentOption = maxOption;
        }

        if (down) {
            down = false;
            currentOption++;
            if (currentOption > maxOption)
                currentOption = 0;
        }

        if (optionSelected) {
            optionSelected = false;
            if (options[currentOption] == "novo jogo") {
                Game.gameState = "Normal";
                pause = false;
            } else if (options[currentOption] == "carregar jogo") {
                // Boa sorte!
            } else if (options[currentOption] == "sair") {
                System.exit(1);
            }
        }
    }

    public void render(Graphics2D g2d) {
	    if (pause == false) g2d.setColor(Color.BLACK);
        else g2d.setColor(new Color(0, 0, 0, 150));
		g2d.fillRect(0, 0,Game.WIDTH,Game.HEIGHT);

        g2d.setColor(Color.YELLOW);
		g2d.setFont(new Font("arial", Font.BOLD, 15));
		g2d.drawString("The Legend of Karma", 27, 45);

        g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("arial", Font.PLAIN, 10));
		if (pause == false) g2d.drawString("Novo jogo", 90, 115);
        else g2d.drawString("Continuar", 90, 115);
        g2d.drawString("Carregar jogo", 80, 135);
        g2d.drawString("Sair",105, 155);

        if (options[currentOption] == "novo jogo") {
            g2d.drawString(">", 70, 115);
            g2d.drawString("<", 151, 115);
        } else if (options[currentOption] == "carregar jogo") {
            g2d.drawString(">", 60, 135);
            g2d.drawString("<", 161, 135);
        } else if (options[currentOption] == "sair") {
            g2d.drawString(">", 85, 155);
            g2d.drawString("<", 138, 155);
        }
    }
}
