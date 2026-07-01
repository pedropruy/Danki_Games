package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;

public class Creature extends Entity{

	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = down_dir;
	public double speed = 1;

    protected int frames = 0, maxFrames = 15, index = 0, maxIndex = 1;
	protected boolean moved = false;
	protected BufferedImage[] rightCreature;
	protected BufferedImage[] leftCreature;
	protected BufferedImage[] upCreature;
	protected BufferedImage[] downCreature;
    protected int offsetShadow = 0;

    public boolean isDamaged = false;
	protected int isDamagedFrames = 0;
	// 0 = Normal, 1 = Branco, 2 = Transparente
	protected int damageMode = 0;

    public Creature(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw,
            int maskh) {
        super(x, y, width, height, sprite, maskx, masky, maskw, maskh);
    }

	protected void destroySelf () {
	}
    
    public void render(Graphics2D g2d) {
        g2d.drawImage(GROUND_SHADOW_EN, this.getX() - Camera.x, this.getY() - Camera.y + offsetShadow, null);

		// Descobre qual é a sprite atual com base na direção
    	BufferedImage spriteAtual = null;
    	if (dir == right_dir) {
			spriteAtual = rightCreature[index];
		} else if (dir == left_dir) {
			spriteAtual = leftCreature[index];
		} else if (dir == up_dir) {
			spriteAtual = upCreature[index];
		} else if (dir == down_dir) {
			spriteAtual = downCreature[index];
		}

    	if (spriteAtual == null) return;

    	// Efeito visual com base no estado de dano
	    if (this.isDamaged) {
    	    if (this.damageMode == 1) {
        	    // Desenha a versão totalmente branca
            	spriteAtual = gersarSpriteBranca(spriteAtual);
	            g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
    	    } else if (this.damageMode == 2) {
        	    // Não desenha nada (totalmente transparente)
        	} else {
            	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
        	}
	    } else {
    	    // Caso não tiver levado dano, desenha normalmente
        	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
    	}
    }

	protected BufferedImage gersarSpriteBranca(BufferedImage image) {
    	// Cria uma nova imagem temporária com o mesmo tamanho e tipo da original
    	BufferedImage branca = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    
	    for (int x = 0; x < image.getWidth(); x++) {
    	    for (int y = 0; y < image.getHeight(); y++) {
        	    int pixel = image.getRGB(x, y);
            	int alpha = (pixel >> 24) & 0xff;

	            // Se o pixel não for totalmente transparente, transforma em branco
    	        if (alpha > 0) {
        	        // 0xFFFFFF é o código hexadecimal para a cor Branca
            	    int pixelBranco = (alpha << 24) | 0xFFFFFF;
                	branca.setRGB(x, y, pixelBranco);
            	}
        	}
    	}
    	return branca;
	}
}
