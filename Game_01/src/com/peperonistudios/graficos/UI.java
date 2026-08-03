package com.peperonistudios.graficos;

import java.awt.BasicStroke;
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
   
    private final static int offsetUI = 3;

	public static BufferedImage minimap = null;
    public static boolean showMinimap = false;
    public static int[] minimapPixels;

    private final int widthBox = Game.WIDTH - (offsetUI*2), heightBox = 40;
    private final int xBox = offsetUI, yBox = Game.HEIGHT - offsetUI - heightBox;
    private final int xMsg = xBox + 7, yMsg = yBox + 16;
    public static String currentDialogue = "";
    private static String[] currentLine;
    public static int curIndex = 0, lineIndex = 0, time = 0, maxTime = 1;

	public static boolean showMessageGameOver = false;
	public static int framesMessageGameOver = 0;

    private static Lightmap lightmap = null;

    public void createLightmap(String path) {
        lightmap = new Lightmap(path);
    }

    public void createMinimap() {
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        minimapPixels = ((DataBufferInt) minimap.getRaster().getDataBuffer()).getData();
        showMinimap = true;
    }

    public void tick() {
        switch (Game.gameState) {
            case "Cutscene":
                
                break;
        
            case "Dialogue":
                if (curIndex < currentLine[lineIndex].length()) {
                    time++;
                    if (time >= maxTime) {
                        time = 0;
                        curIndex++;
                        // Som de digitação
                    }
                } else {
                    if (lineIndex < currentLine.length-1) {
                        time++;
                        if (time >= maxTime) {
                            time = 0;
                            curIndex = 0;
                            lineIndex++;
                        }
                    }
                }
                break;
            
            case "Normal":
                break;

            case "GameOver":
                framesMessageGameOver++;
                if (framesMessageGameOver == 20) {
                    framesMessageGameOver = 0;
                    if (showMessageGameOver) {
                        showMessageGameOver = false;
                    } else { showMessageGameOver = true; }
                }
                break;
        }
    }

    public static void newDialogue(String text) {
        currentDialogue = text;
        currentLine = text.split("\n");
        curIndex = 0;
        lineIndex = 0;
        time = 0;
    }

    public static void endDialogue() {
        currentDialogue = "";
        currentLine = null;
        curIndex = 0;
        lineIndex = 0;
        time = 0;
    }

    public void drawDialogueScreen(Graphics2D g2d, String text) {
        if (text == null) return;

        // Dialogue box
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(xBox, yBox,
                          widthBox, heightBox, 15, 15);
        /*g2d.setColor(new Color(255, 255, 255, 255));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(xBox+2, yBox+2, widthBox-4, heightBox-5, 13, 13);*/

        // Dialogue text
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < currentLine.length; i++) {
            if (i < lineIndex) {
                g2d.drawString(currentLine[i], xMsg, yMsg + (i * 12));
            } 
            else if (i == lineIndex) {
                // Isso é uma trava de segurança para nuncar travar a substring
                int safeIndex = Math.min(curIndex, currentLine[i].length());
                g2d.drawString(currentLine[i].substring(0, safeIndex), xMsg, yMsg + (i * 16));
            }
        }
    }

    public void drawPlayerHUD (Graphics2D g2d) {
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
            g2d.fillRect(Game.WIDTH - minimap.getWidth() - 10, 0,
                         minimap.getWidth() + 10, minimap.getHeight() + 10);

            World.renderMinimap();
            g2d.drawImage(minimap, Game.WIDTH - minimap.getWidth() - 5, 5, null);
        }
    }

    public void render(Graphics2D g2d) {
        switch (Game.gameState) {
            case "Menu":
                drawPlayerHUD(g2d);
                break;
            
            case "Cutscene":
                
                break;
        
            case "Dialogue":
                drawPlayerHUD(g2d);
                drawDialogueScreen(g2d, currentDialogue);
                break;
            
            case "Normal":
                drawPlayerHUD(g2d);
                break;

            case "GameOver":
                g2d.setColor(new Color(0,0,0,150));
                g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
                g2d.setFont(Game.kiwi);
                g2d.setColor(Color.RED);
                g2d.drawString("GAME OVER!", 80, 110);
                g2d.setColor(Color.WHITE);
                g2d.setFont(Game.hearts);
                if (showMessageGameOver)
                    g2d.drawString("> Pressione enter para reiniciar <", 17, 127);
                break;
        }
    }
}
