package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import java.util.List;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.Vector2i;
import com.peperonistudios.world.Node;

public class Creature extends Entity{

	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = down_dir;

	protected List<Node> path;
	public double speed = 1;

    protected int frames = 0, maxFrames = 15, index = 0, maxIndex = 1;
	protected boolean moved = false;
	protected BufferedImage[] sideCreature;
	protected BufferedImage[] upCreature;
	protected BufferedImage[] downCreature;
    protected int offsetShadow = 0;

    public boolean isDamaged = false;
	protected int isDamagedFrames = 0;
	// 0 = Normal, 1 = Branco, 2 = Transparente
	protected int damageMode = 0;

	public boolean isInteracting = false;
    public String[] dialogue = new String[20];
    public int dialogueIndex = 0;

    public Creature(int x, int y, int width, int height, int xsprite, int ysprite, int maskx, int masky, int maskw, int maskh) {
		super(x, y, width, height, null, 0, maskx, masky, maskw, maskh);

		sideCreature = new BufferedImage[2];
		upCreature = new BufferedImage[2];
		downCreature = new BufferedImage[2];

		for(int i = 0; i < 2; i++) {
			sideCreature[i] = Game.spritesheet.getSprite(xsprite+64+(i*16), ysprite, 16, 16);
			upCreature[i] = Game.spritesheet.getSprite(xsprite+32+(i*16), ysprite, 16, 16);
			downCreature[i] = Game.spritesheet.getSprite(xsprite+(i*16), ysprite, 16, 16);
		}
    }

	public void setAction() {}
	public void setDialogue() {}
	public void speak() {}

	public boolean isColliding (int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) continue;
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}

	public static double calculateDistance (Entity e1, Entity e2) {
		int x1 = e1.getX(), y1 = e1.getY();
		int x2 = e2.getX(), y2 = e2.getY();
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));		
	}

	public static double calculateDistance (int x1, int y1, Entity e2) {
		int x2 = e2.getX(), y2 = e2.getY();
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));		
	}

	protected void destroySelf () {
	}

	public void followPath(List<Node> path) {
    	if (path != null && path.size() > 0) {
        	Vector2i target = path.get(path.size() - 1).tile;

	        if (x < target.x*16) {
    	        if (!isColliding((int)(x + speed), this.getY())) {
        	        x += speed;
            	    dir = right_dir;
                	moved = true;
            	} else path.clear();
	        } else if (x > target.x*16) {
    	        if (!isColliding((int)(x - speed), this.getY())) {
        	        x -= speed;
            	    dir = left_dir;
                	moved = true;
	            } else path.clear();
    	    } else if (y < target.y*16) {
        	    if (!isColliding(this.getX(), (int)(y + speed))) {
            	    y += speed;
                	dir = down_dir;
	                moved = true;
    	        } else path.clear();
        	} else if (y > target.y*16) {
            	if (!isColliding(this.getX(), (int)(y - speed))) {
                	y -= speed;
            	    dir = up_dir;
        	        moved = true;
    	        } else path.clear();
	        }

			// Arredonda a coordenada para retirar a parte decimal
    	    if (Math.abs(x - target.x*16) < speed && Math.abs(y - target.y*16) < speed) {
        	    x = target.x*16; 
            	y = target.y*16;
            	path.remove(path.size() - 1);
        	}
    	}
	}
    
    public void render(Graphics2D g2d) {
		// Hitbox
        /*g2d.setColor(Color.BLUE);
        g2d.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y,
                     maskw, maskh);*/

        g2d.drawImage(GROUND_SHADOW_EN, this.getX() - Camera.x, this.getY() - Camera.y + offsetShadow, null);


		// Descobre qual é a sprite atual com base na direção
    	BufferedImage spriteAtual = null;
    	if (dir == right_dir) {
			// Forma de inverter a imagem horizontalmente
			spriteAtual = sideCreature[index];
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    		tx.translate(-spriteAtual.getWidth(), 0);
    
    		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			spriteAtual = op.filter(spriteAtual, null);
		} else if (dir == left_dir) {
			spriteAtual = sideCreature[index];
		}else if (dir == up_dir) {
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
	            g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
    	    } else if (this.damageMode == 2) {
        	    // Não desenha nada (totalmente transparente)
        	} else {
            	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
        	}
	    } else {
    	    // Caso não tiver levado dano, desenha normalmente
        	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
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
