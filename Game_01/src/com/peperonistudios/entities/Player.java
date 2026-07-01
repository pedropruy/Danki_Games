package com.peperonistudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.peperonistudios.graficos.Spritesheet;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Player extends Entity{
	
	public boolean right, up, down, left;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = down_dir;
	public double spd = 1;

	private int frames = 0, maxFrames = 15, index = 0, maxIndex = 1;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;

	public boolean isDamaged = false;
	private int isDamagedFrames = 0;
	// 0 = Normal, 1 = Branco, 2 = Transparente
	private int damageMode = 0;

	private int useSpell = 0;
	public boolean isCasting = false;
	private boolean gotFireBook = false, gotIceBook = false;

	public static double max_mana = 40, mana = 0;
	public static double max_life = 3, life = max_life;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite, int maskx, int masky, int maskw, int maskh) {
		super(x, y, width, height, sprite, maskx, masky, maskw, maskh);
		
		rightPlayer = new BufferedImage[2];
		leftPlayer = new BufferedImage[2];
		upPlayer = new BufferedImage[2];
		downPlayer = new BufferedImage[2];
		
		for(int i = 0; i < 2; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(96+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(64+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 2; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(0+(i*16), 0, 16, 16);
		}
	}

	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x + spd),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=spd;
		}
		else if (left && World.isFree((int)(x - spd),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=spd;
		}
		
		else if(up && World.isFree(this.getX(),(int)(y - spd))) {
			moved = true;
			dir = up_dir;
			y-=spd;
		}
		else if (down && World.isFree(this.getX(),(int)(y + spd))) {
			moved = true;
			dir = down_dir;
			y+=spd;
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

		if (isCasting) {
			isCasting = false;
			if (!isDamaged) {
				int dx = 0, dy = 0;
				int px = this.getX(), py = this.getY();
				if (dir == right_dir) {
					dx = 1;
					px += 8;
				} else if (dir == left_dir) {
					dx = -1;
					px -= 8;
				}

				if (dir == down_dir) {
					dy = 1;
					py += 8;
				} else if (dir == up_dir) {
					dy = -1;
					py -= 8;
				}
				
				Projectile spell = new Projectile(px, py, 8, 8, Entity.BASIC_ATTACK_EN,
								     		      0, 0, 8, 8, dx, dy, 20);
				if (gotFireBook && mana > 0) {
					spell = new Projectile(px, py, 8, 8, Entity.FIRE_BALL_EN,
								     	   0, 0, 8, 8, dx, dy, 30);					
					mana--;
				}
				Game.projectiles.add(spell);
			}			
		}

		// Game over simples!
		if (life <= 0) {
			Game.entities.clear();
			Game.enemies.clear();
			Game.itens.clear();
			Game.projectiles.clear();
			Game.entities = new ArrayList<Entity>();
			Game.enemies = new ArrayList<Enemy>();
			Game.itens = new ArrayList<Entity>();
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

	public void checkCollisionItems () {
		for (int i = 0; i < Game.itens.size(); i++) {
			Entity atual = Game.itens.get(i);
			if (atual instanceof LifeElixir) {
				if (Entity.isColliding(this, atual)) {
					life += 2;
					if (life > max_life) life = max_life;
					Game.itens.remove(i);
					Game.entities.remove(atual);
				}
			}

			if (atual instanceof ManaElixir) {
				if (Entity.isColliding(this, atual)) {
					mana += 10;
					if (mana > max_mana) mana = max_mana;
					Game.itens.remove(i);
					Game.entities.remove(atual);
				}
			}

			if (atual instanceof Spell) {
				if (Entity.isColliding(this, atual)) {
					this.gotFireBook = true;
					this.useSpell = 1;
					Game.itens.remove(i);
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void render(Graphics g) {
    	// 1. Descobre qual é a sprite atual com base na direção
    	BufferedImage spriteAtual = null;
		int xFocus = 0, yFocus = 0;
    	if (dir == right_dir) {
			spriteAtual = rightPlayer[index];
			xFocus = 13; yFocus = 7;
		} else if (dir == left_dir) {
			spriteAtual = leftPlayer[index];
			xFocus = -5; yFocus = 7;
		} else if (dir == up_dir) {
			spriteAtual = upPlayer[index];
			xFocus = 4; yFocus = -2;
		} else if (dir == down_dir) {
			spriteAtual = downPlayer[index];
			xFocus = 4; yFocus = 9;
		}

    	if (spriteAtual == null) return;

    	// 2. Aplica o efeito visual com base no damageMode
	    if (this.isDamaged) {
    	    if (this.damageMode == 1) {
        	    // Desenha a versão totalmente branca
            	spriteAtual = gersarSpriteBranca(spriteAtual);
	            g.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
    	    } else if (this.damageMode == 2) {
        	    // Modo Transparente: simplesmente NÃO damos drawImage (o player fica invisível por esse frame)
        	} else {
            	// Modo Normal dentro do período de dano
            	g.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
        	}
	    } else {
    	    // Se NÃO estiver danificado, desenha o player normalmente
        	g.drawImage(spriteAtual, this.getX() - Camera.x, this.getY() - Camera.y, null);
    	}

		// Tenha pegado Magia de Fogo
		if (dir != up_dir) {
			switch (useSpell) {
				case 0:
					g.drawImage(Entity.MAGIC_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;

				case 1:
					g.drawImage(Entity.FIRE_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;
			
				default:
					g.drawImage(Entity.MAGIC_FOCUS_EN, this.getX() - Camera.x + xFocus, this.getY() - Camera.y + yFocus, null);
					break;
			}
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
