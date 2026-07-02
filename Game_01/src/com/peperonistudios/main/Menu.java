package com.peperonistudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.peperonistudios.entities.Player;
import com.peperonistudios.world.World;

public class Menu {
    
    public static String[] optionsMain = {"Novo Jogo", "Carregar Jogo", "Sair"};
    public static String[] optionsPause = {"Continuar", "Salvar Jogo", "Carregar Jogo", "Sair"};
    public static String[] options = optionsMain;
    public int currentOption = 0;
    public int maxOption = options.length - 1;

    public boolean up = false, down = false, optionSelected = false;

    public static boolean pause = false;
    public static boolean saveExists = false;
    public static boolean saveGame = false;

    public void tick() {
        File file = new File("save.txt");
        if (file.exists()) saveExists = true;
        else saveExists = false;

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
            if (options[currentOption].equals("Novo Jogo")) {
                Game.gameState = "Normal";
                pause = false;
                file = new File("save.txt");
                file.delete();

            } else if (options[currentOption].equals("Continuar")) {
                Game.gameState = "Normal";
                pause = false;

            } else if (options[currentOption].equals("Salvar Jogo")) {
				String[] opt1 = {"level","vida","mana","spell1","spell2"};
				int[] opt2 = {Game.CURRENT_LEVEL,Player.life,Player.mana,
                              Player.gotFireBook ? 1 : 0,
                              Player.gotIceBook ? 1 : 0};
				Menu.saveGame(opt1, opt2, 10);

            } else if (options[currentOption].equals("Carregar Jogo")) {
                file = new File("save.txt");
                if (file.exists()) {
                    String saver = loadGame(10);
                    applySave(saver);
                }
                
            } else if (options[currentOption].equals("Sair")) {
                System.exit(1);
            }
        }
    }

    public static void applySave (String str) {
        String[] spl = str.split("/");
        for (int i = 0; i < spl.length; i++) {
            String[] spl2 = spl[i].split("\\|");
            switch (spl2[0]) {
                case "level":
                    World.loadLevel("level" + spl2[1] + ".png");
                    Game.gameState = "Normal";
                    pause = false;
                break;

                case "vida":
                    Player.life = Integer.parseInt(spl2[1]);
                break;

                case "mane":
                    Player.mana = Integer.parseInt(spl2[1]);
                break;

                case "spell1":
                    Player.gotFireBook = spl2[1].equals("1");
                break;

                case "spell2":
                    Player.gotIceBook = spl2[1].equals("1");
                break;
            }
        }
    }

    public static String loadGame (int encode) {
        String line = "";
        File file = new File("save.txt");
        if (file.exists()) {
            try {
                String singleLine = null;
                BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                try {
                    while ((singleLine = reader.readLine()) != null) {
                        String[] trans = singleLine.split("\\|");
                        char[] val = trans[1].toCharArray();
                        trans[1] = "";
                        for (int i = 0; i < val.length; i++) {
                            val[i] -= encode;
                            trans[1] += val[i];
                        }
                        line += trans[0] + "|" + trans[1] + "/";
                    }
                } catch (IOException e) {}
            } catch (FileNotFoundException e) {}
        }

        return line;
    }

    public static void saveGame(String[] val1, int[] val2, int encode) {
        BufferedWriter write = null;
        try {
            write = new BufferedWriter(new FileWriter("save.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < val1.length; i++) {
            String current = val1[i];
            current += "|";
            char[] value = Integer.toString(val2[i]).toCharArray();
            for (int j = 0; j < value.length; j++) {
                value[j] += encode;
                current += value[j];
            }

            try {
                write.write(current);
                if (i < val1.length - 1) write.newLine();  
            } catch (IOException e) {}
        }

        try {
            write.flush();
            write.close();
        } catch (IOException e) {}
    }

    public void render(Graphics2D g2d) {
    if (!pause) g2d.setColor(Color.BLACK);
    else g2d.setColor(new Color(0, 0, 0, 150));
    g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

    // Desenhando título
    g2d.setColor(Color.YELLOW);
    Font titleFont = new Font("arial", Font.BOLD, 15);
    g2d.setFont(titleFont);
    drawCenteredString(g2d, "The Legend of Karma", titleFont, 45);

    // Desenhando as opções
    g2d.setColor(Color.WHITE);
    Font menuFont = new Font("arial", Font.PLAIN, 10);
    g2d.setFont(menuFont);

    int y = 115;
    for (int i = 0; i < options.length; i++) {
        if (options[currentOption].equals(options[i])) 
            drawCenteredString(g2d, ">  " + options[i] + "  <", menuFont, y+(20*i));
        else drawCenteredString(g2d, options[i], menuFont, y+(20*i));
    }
}

    public void drawCenteredString(Graphics2D g2d, String text, Font font, int y) {
        FontMetrics fm = g2d.getFontMetrics(font);
        int x = (Game.WIDTH - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

}
