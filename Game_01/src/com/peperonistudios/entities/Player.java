package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.peperonistudios.graficos.Spritesheet;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Player extends Creature {
	
	public boolean right, up, down, left;

	private int useSpell = 0, max_spell = 0;
	public boolean isCasting = false, nextSpell = false;
	private boolean gotFireBook = false, gotIceBook = false;

	public boolean isCastingMouse = false;
	public int mx = 0, my = 0;

	public static double max_mana = 40, mana = 0;
	public static double max_life = 3, life = max_life;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw, int maskh) {
		super(x, y, width, height, sprite, maskx, masky, maskw, maskh);
		
		rightCreature = new BufferedImage[2];
		leftCreature = new BufferedImage[2];
		upCreature = new BufferedImage[2];
		downCreature = new BufferedImage[2];
		
		for(int i = 0; i < 2; i++) {
			rightCreature[i] = Game.spritesheet.getSprite(96+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			leftCreature[i] = Game.spritesheet.getSprite(64+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			upCreature[i] = Game.spritesheet.getSprite(32+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			downCreature[i] = Game.spritesheet.getSprite(0+(i*16), 0, 16, 16);
		}
	}

	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x + speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if (left && World.isFree((int)(x - speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		
		else if(up && World.isFree(this.getX(),(int)(y - speed))) {
			moved = true;
			dir = up_dir;
			y-=speed;
		}
		else if (down && World.isFree(this.getX(),(int)(y + speed))) {
			moved = true;
			dir = down_dir;
			y+=speed;
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

		// Trocar de Magia
		if (this.nextSpell) {
			this.nextSpell = false;
			this.useSpell++;

			if (mana == 0) 
				this.useSpell = 0;

			if (this.useSpell > this.max_spell) 
				this.useSpell = 0;
		}

		castSpell();

		// Game over simples!
		if (life <= 0) {
			Game.entities.clear();
			Game.enemies.clear();
			Game.collectables.clear();
			Game.projectiles.clear();
			Game.entities = new ArrayList<Entity>();
			Game.enemies = new ArrayList<Enemy>();
			Game.collectables = new ArrayList<Collectable>();
			Game.projectiles = new ArrayList<Projectile>();
			Game.spritesheet = new Spritesheet("/spritesheet.png");
			Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(0, 0, 16, 16), 0, 0, 16, 16);
			Player.life = max_life; Player.mana = max_mana;
			Game.entities.add(Game.player);
			Game.world = new World("/map.png");
			return;
		}

		checkCollisionItems();

		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2) + (this.getWidth()/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2) + (this.getHeight()/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}

	public void castSpell() {
		double dx = 0, dy = 0;
		double angle = 0;
		int px = this.getX(), py = this.getY();
		if (isCasting) {
			if (!isDamaged) {
				if (dir == right_dir) {
					dx = 1;
					px += 8;
					angle = -90;
				} else if (dir == left_dir) {
					dx = -1;
					px -= 8;
					angle = 90;
				}

				if (dir == down_dir) {
					dy = 1;
					py += 8;
				} else if (dir == up_dir) {
					dy = -1;
					py -= 8;
					angle = 180;
				}
			}			
		} else if (isCastingMouse) {
			if (!isDamaged) {
				double mouse_angle = Math.atan2(this.my - (this.getY() + 8 - Camera.y),
					 						    this.mx - (this.getX() + 8  - Camera.x));
				dx = Math.cos(mouse_angle);
				dy = Math.sin(mouse_angle);
				px = this.getX();
				py = this.getY();

				angle = Math.toDegrees(mouse_angle) - 90;
			}			
		}

		if (isCasting || isCastingMouse) {
			this.isCasting = false;
			this.isCastingMouse = false;
			if (!isDamaged) {
				Projectile spell = new Projectile(px, py, 8, 8, Entity.BASIC_ATTACK1_EN, Entity.BASIC_ATTACK2_EN,
										     		      4, 4, 8, 8, dx, dy, 20, angle, 1);
				switch (useSpell) {
					case 0:
						// Nada porque já é definido acima
					break;
				    
					case 1:
						if (mana > 0 && this.gotFireBook) {
						spell = new Projectile(px, py, 8, 8, Entity.FIRE_BALL1_EN, Entity.FIRE_BALL2_EN,
										       3, 3, 10, 11, dx, dy, 30, angle, 5);					
						mana--;
						if (mana == 0) this.useSpell = 0;
						} else this.useSpell = 0;
					break;
					
					case 2:
						if (mana > 0 && this.gotIceBook) {
						spell = new Projectile(px, py, 8, 8, Entity.ICE_CRYSTAL1_EN, Entity.ICE_CRYSTAL2_EN,
										       4, 2, 8, 12, dx, dy, 40, angle, 10);					
						mana--;
						if (mana == 0) this.useSpell = 0;
						} else this.useSpell = 0;
					break;
				}
				Game.projectiles.add(spell);
			}
		}
	}

	public void checkCollisionItems () {
		for (int i = 0; i < Game.collectables.size(); i++) {
			Entity atual = Game.collectables.get(i);
			// Colisão com elixir da vida
			if (atual instanceof LifeElixir) {
				if (Entity.isColliding(this, atual)) {
					life += 2;
					if (life > max_life) life = max_life;
					Game.collectables.remove(i);
					Game.entities.remove(atual);
				}
			}
			// Colisão com elixir de mana
			if (atual instanceof ManaElixir) {
				if (Entity.isColliding(this, atual)) {
					mana += 10;
					if (mana > max_mana) mana = max_mana;
					Game.collectables.remove(i);
					Game.entities.remove(atual);
				}
			}
			// Colisão com livros de magia
			if (atual instanceof SpellBook) {
				if (Entity.isColliding(this, atual)) {
					if (atual.sprite == Entity.FIRE_BOOK_EN) {
						if (!this.gotFireBook) {
							this.gotFireBook = true;
							this.max_spell++;
							this.useSpell = 1;
						}
					} if (atual.sprite == Entity.ICE_BOOK_EN) {
						if (!this.gotIceBook) {
							this.gotIceBook = true;
							this.max_spell++;
							this.useSpell = 2;
						}
					}
					Game.collectables.remove(i);
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void render(Graphics2D g2d) {
		super.render(g2d);

		render_Magic_Focus(g2d);
	}

	private void render_Magic_Focus(Graphics2D g2d) {
		int xFocus = 0, yFocus = 0;
    	if (dir == right_dir) {
			xFocus = 13; yFocus = 7;
		} else if (dir == left_dir) {
			xFocus = -5; yFocus = 7;
		} else if (dir == up_dir) {
			xFocus = 4; yFocus = -2;
		} else if (dir == down_dir) {
			xFocus = 4; yFocus = 9;
		}

		if (dir != up_dir) {
			switch (useSpell) {
				case 0:
					g2d.drawImage(Entity.MAGIC_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;

				case 1:
					g2d.drawImage(Entity.FIRE_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;
				
				case 2:
					g2d.drawImage(Entity.ICE_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
				break;
			
				default:
					g2d.drawImage(Entity.MAGIC_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;
			}
		}
	}
}
