package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Enemy extends Entity{

	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = down_dir;
	private double speed = 1;
	
	private int frames = 0, maxFrames = 30, index = 0, maxIndex = 1;
	private boolean moved = false;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	private BufferedImage[] upEnemy;
	private BufferedImage[] downEnemy;

	public boolean isDamaged = false;
	private int isDamagedFrames = 0;
	// 0 = Normal, 1 = Branco, 2 = Transparente
	private int damageMode = 0;

	public double max_life = 3, life = max_life;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw, int maskh) {
		super(x, y, width, height, null, maskx, masky, maskw, maskh);
		
		// Tem que trocar isto caso for fazer outro inimigo
		this.setMask(2, 3, 13, 11);

		rightEnemy = new BufferedImage[2];
		leftEnemy = new BufferedImage[2];
		upEnemy = new BufferedImage[2];
		downEnemy = new BufferedImage[2];

		for(int i = 0; i < 2; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(96+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite(64+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			upEnemy[i] = Game.spritesheet.getSprite(32+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			downEnemy[i] = Game.spritesheet.getSprite(0+(i*16), 32, 16, 16);
		}
	}

	public void tick () {
		if (this.life > 0) {
		if (this.isCollidingWithPlayer() == false) {
		if (Game.rand.nextInt(100) < 30 && !this.isDamaged) {
			if ((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
				&& !isColliding((int)(x+speed), this.getY())) {
				moved = true;
				dir = right_dir;
				x += speed;
			}
			else if ((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColliding((int)(x-speed), this.getY())) {
				moved = true;
				dir = left_dir;
				x -= speed;
			}
			else if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColliding(this.getX(), (int)(y+speed))) {
				moved = true;
				dir = down_dir;
				y += speed;
			}
			else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColliding(this.getX(), (int)(y-speed))) {
				moved = true;
				dir = up_dir;
				y -= speed;
			}	
		}
		} else {
			// Estamos colidindo com o player
			if(!Game.player.isDamaged) {
				if (Game.rand.nextInt(100) < 10) {
					Game.player.isDamaged = true;
					Player.life--;
				}
			}
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}

		// Controla a animação de dano
		if (this.isDamaged) {
    		this.isDamagedFrames++;
    
    		// Altera o estado a cada 4 frames (Sinta-se livre para mudar o 4 para alterar a velocidade do piscar)
    		if (this.isDamagedFrames % 12 < 4) {
    		    this.damageMode = 1; // Modo Branco
   		 	} else if (this.isDamagedFrames % 12 < 8) {
    		    this.damageMode = 2; // Modo Transparente (Invisível)
    		} else {
    		    this.damageMode = 0; // Modo Normal
    		}

    		// Se atingiu o tempo total do dano (30 frames)
   			if (this.isDamagedFrames >= 30) {
    		    this.isDamagedFrames = 0;
    		    this.isDamaged = false;
    		    this.damageMode = 0; // Garante que volta ao normal ao acabar o dano
    		}
		}

		isCollidingWithProjectiles();

		} else {
			this.isDamagedFrames++;
    
    		// Altera o estado a cada 4 frames (Sinta-se livre para mudar o 4 para alterar a velocidade do piscar)
    		if (this.isDamagedFrames % 12 < 4) {
    		    this.damageMode = 1; // Modo Branco
   		 	} else if (this.isDamagedFrames % 12 < 8) {
    		    this.damageMode = 2; // Modo Transparente (Invisível)
    		} else {
    		    this.damageMode = 0; // Modo Normal
    		}

    		// Se atingiu o tempo total do dano (30 frames)
   			if (this.isDamagedFrames >= 30) {
				destroySelf();
				return;
    		}
		}
	}

	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		return enemyCurrent.intersects(player);
	}

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

	public boolean isCollidingWithProjectiles() {
		for (int i = 0; i < Game.projectiles.size(); i++) {
			Projectile e = Game.projectiles.get(i);
			
			if (Entity.isColliding(this, e) && !isDamaged) {
				life -= e.damage;
				this.isDamaged = true;
				Game.projectiles.remove(e);
			}
		}

		return false;
	}

	public void destroySelf () {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void render(Graphics2D g2d) {
		g2d.drawImage(GROUND_SHADOW_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);

		// Descobre qual é a sprite atual com base na direção
    	BufferedImage spriteAtual = null;
    	if (dir == right_dir) {
			spriteAtual = rightEnemy[index];
		} else if (dir == left_dir) {
			spriteAtual = leftEnemy[index];
		} else if (dir == up_dir) {
			spriteAtual = upEnemy[index];
		} else if (dir == down_dir) {
			spriteAtual = downEnemy[index];
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
            	// Desenha normal
            	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
        	}
	    } else {
    	    // Caso não tiver levado dano, desenha normalmente
        	g2d.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
    	}
	}

	private BufferedImage gersarSpriteBranca(BufferedImage image) {
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
