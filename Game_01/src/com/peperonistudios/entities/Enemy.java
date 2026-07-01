package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.main.Sound;
import com.peperonistudios.world.World;

public class Enemy extends Creature {

	public double max_life = 3, life = max_life;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw, int maskh) {
		super(x, y, width, height, null, maskx, masky, maskw, maskh);
		
		// Tem que trocar isto caso for fazer outro inimigo
		this.setMask(2, 3, 13, 11);

		rightCreature = new BufferedImage[2];
		leftCreature = new BufferedImage[2];
		upCreature = new BufferedImage[2];
		downCreature = new BufferedImage[2];

		for(int i = 0; i < 2; i++) {
			rightCreature[i] = Game.spritesheet.getSprite(96+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			leftCreature[i] = Game.spritesheet.getSprite(64+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			upCreature[i] = Game.spritesheet.getSprite(32+(i*16), 32, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			downCreature[i] = Game.spritesheet.getSprite(0+(i*16), 32, 16, 16);
		}
	}

	public void tick () {
		if (this.life > 0) {
		if (this.isCollidingWithPlayer() == false) {
		if (Game.rand.nextInt(100) < 30 && !this.isDamaged) {
			if ((int)x < Game.player.getX() && World.isFreeCreature((int)(x+speed), this.getY())
				&& !isColliding((int)(x+speed), this.getY())) {
				moved = true;
				dir = right_dir;
				x += speed;
			}
			else if ((int)x > Game.player.getX() && World.isFreeCreature((int)(x-speed), this.getY())
					&& !isColliding((int)(x-speed), this.getY())) {
				moved = true;
				dir = left_dir;
				x -= speed;
			}
			else if ((int)y < Game.player.getY() && World.isFreeCreature(this.getX(), (int)(y+speed))
					&& !isColliding(this.getX(), (int)(y+speed))) {
				moved = true;
				dir = down_dir;
				y += speed;
			}
			else if ((int)y > Game.player.getY() && World.isFreeCreature(this.getX(), (int)(y-speed))
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
					Sound.hurtEffect.play();
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
			// Animação de morte simples
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
				Sound.hurtEffect.play();
				life -= e.damage;
				this.isDamaged = true;
				Game.projectiles.remove(e);
			}
		}

		return false;
	}

	protected void destroySelf () {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void render(Graphics2D g2d) {
		super.render(g2d);
	}
}
