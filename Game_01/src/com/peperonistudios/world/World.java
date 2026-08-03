package com.peperonistudios.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.peperonistudios.entities.Collectable;
import com.peperonistudios.entities.Creature;
import com.peperonistudios.entities.Enemy;
import com.peperonistudios.entities.Entity;
import com.peperonistudios.entities.Npc;
import com.peperonistudios.entities.Elixir;
import com.peperonistudios.entities.Player;
import com.peperonistudios.entities.Projectile;
import com.peperonistudios.entities.SpellBook;
import com.peperonistudios.graficos.Spritesheet;
import com.peperonistudios.graficos.UI;
import com.peperonistudios.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
    public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			tiles = new Tile[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			map.getRGB(0, 0,map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int xx = 0; xx < map.getWidth(); xx++) {
    for(int yy = 0; yy < map.getHeight(); yy++) {
        int pixelAtual = pixels[xx + (yy * map.getWidth())];

    	// Por padrão, sempre criamos o tile do chão
        tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_GRASS);

        switch(pixelAtual) {
            case 0xFF00FF21:
                tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_GRASS);
                break;
            case 0xFF7F3300:
                tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_SOLOTREE);
                break;
            case 0xFF00AD00:
                tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_TREETOP);
                break;
            case 0xFF00FFFF:
                tiles[xx + (yy * WIDTH)] = new ObstacleTile(xx * 16, yy * 16, Tile.TILE_RIVER);
                break;
            case 0xFFFF9854:
                tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_BRIDGEV);
                break;
            case 0xFFD37C45:
                tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_BRIDGEH);
                break;
            case 0xFFAD7B00:
                tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_FENCE);
                break;
            case 0xFFFF006E:
                tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOWER);
                break;
            case 0xFF57007F:
                // PLAYER
                Game.player.setX(xx * 16);
                Game.player.setY(yy * 16);
                break;
            case 0xFFB200FF:
                // PURPLE SLIME
                Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, 0,
                                     32, 0.5, 2 , 3,
                                     13, 11);
                Game.entities.add(en);
                Game.enemies.add(en);
                break;
            case 0xFF7F0000:
                // HUNTER
                Npc h  = new Npc(xx * 16, yy * 16, 16, 16, 0, 80,
                                     0, 0, 16, 16);
                Game.entities.add(h);
                Game.npcs.add(h);
                break;
            case 0xFF007F0E:
                // HEALTH ELIXIR
                Elixir le = new Elixir(xx * 16, yy * 16, 16, 16, Entity.LIFE_ELIXIR_EN,
                                               3, 3, 10, 13, 13);
                Game.entities.add(le);
                Game.collectables.add(le);
                break;
            case 0xFF003F06:
                // BIG HEALTH ELIXIR
                Elixir ble = new Elixir(xx * 16, yy * 16, 16, 16, Entity.BIGGER_LIFE_ELIXIR_EN,
                                               2, 0, 12, 16, 20);
                Game.entities.add(ble);
                Game.collectables.add(ble);
                break;
            case 0xFF007F7F:
                // MANA ELIXIR
                Elixir me = new Elixir(xx * 16, yy * 16, 16, 16, Entity.MANA_ELIXIR_EN,
                                               3, 3, 10, 13, 13);
                Game.entities.add(me);
                Game.collectables.add(me);
                break;
            case 0xFF003A3A:
                // BIG MANA ELIXIR
                Elixir bme = new Elixir(xx * 16, yy * 16, 16, 16, Entity.BIGGER_MANA_ELIXIR_EN,
                                               2, 0, 12, 16, 20);
                Game.entities.add(bme);
                Game.collectables.add(bme);
                break;
            case 0xFFFF0000:
                // FIRE MAGIC BOOK
                SpellBook fire = new SpellBook(xx * 16, yy * 16, 16, 16, Entity.FIRE_BOOK_EN,
                                     1, 2, 15, 13);
                Game.entities.add(fire);
                Game.collectables.add(fire);
                break;

            case 0xFF0026FF:
                // ICE MAGIC BOOK
                SpellBook ice = new SpellBook(xx * 16, yy * 16, 16, 16, Entity.ICE_BOOK_EN,
                                     1, 2, 15, 13);
                Game.entities.add(ice);
                Game.collectables.add(ice);
                break;

            default:
        		// Por padrão, sempre criamos o tile do chão
        		tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_GRASS);
                break;
        }
    }
}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    // Geração de mapa "automática"
    public World() {
        Game.player.setX(25*16);
        Game.player.setY(25*16);
        WIDTH = 50;
        HEIGHT = 50;
        tiles = new Tile[WIDTH*HEIGHT];
        for (int xx = 0; xx < WIDTH; xx++) {
            for (int yy = 0; yy < HEIGHT; yy++) {
                tiles[xx + yy*WIDTH] = new WallTile(xx*16, yy*16, Tile.TILE_SOLOTREE);
            }
        }

        int dir = 0;
        int xx = 25, yy = 25;
        for (int i = 0; i < 400; i++) {

            // Direita
            if (dir == 0) {
                if (xx < WIDTH) xx++;

            // Esquerda
            } else if (dir == 1) {
                if (xx > 0) xx--;

            // Baixo
            } else if (dir == 2) {
                if (yy < HEIGHT) yy++;

            // Cima
            } else if (dir == 3) {
                if (yy > 0) yy--;
            }

            if (Game.rand.nextInt(100) < 30) {
                dir = Game.rand.nextInt(4);
            }

            tiles[xx + yy*WIDTH] = new FloorTile(xx*16, yy*16, Tile.TILE_GRASS);
        }
    }

    public static boolean isFreeCreature(int xnext, int ynext, int zplayer) {
        int pxl_a_menos = 2;
        // Cantos superiores
        int x1 = (xnext+pxl_a_menos) / TILE_SIZE;
        int y1 = (ynext+pxl_a_menos) / TILE_SIZE;

        int x2 = (xnext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;
        int y2 = (ynext+pxl_a_menos) / TILE_SIZE;

        // Cantos inferiores
        int x3 = (xnext+pxl_a_menos) / TILE_SIZE;
        int y3 = (ynext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;

        int x4 = (xnext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;
        int y4 = (ynext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;

        if (   !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x4 + (y4*World.WIDTH)] instanceof WallTile)) &&
               !((tiles[x1 + (y1*World.WIDTH)] instanceof ObstacleTile) ||
                 (tiles[x2 + (y2*World.WIDTH)] instanceof ObstacleTile) ||
                 (tiles[x3 + (y3*World.WIDTH)] instanceof ObstacleTile) ||
                 (tiles[x4 + (y4*World.WIDTH)] instanceof ObstacleTile)))
            return true;
        
        if (zplayer > 0) return true;
        return false;
    }

    public static boolean isFreeProjectile(int xnext, int ynext) {
        int pxl_a_menos = 2;
        // Cantos superiores
        int x1 = (xnext+pxl_a_menos) / TILE_SIZE;
        int y1 = (ynext+pxl_a_menos) / TILE_SIZE;

        int x2 = (xnext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;
        int y2 = (ynext+pxl_a_menos) / TILE_SIZE;

        // Cantos inferiores
        int x3 = (xnext+pxl_a_menos) / TILE_SIZE;
        int y3 = (ynext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;

        int x4 = (xnext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;
        int y4 = (ynext+TILE_SIZE-1-pxl_a_menos) / TILE_SIZE;

        return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile
             && !(tiles[x1 + (y1*World.WIDTH)] instanceof ObstacleTile)) ||
                 (tiles[x2 + (y2*World.WIDTH)] instanceof WallTile
             && !(tiles[x2 + (y2*World.WIDTH)] instanceof ObstacleTile)) ||
                 (tiles[x3 + (y3*World.WIDTH)] instanceof WallTile
             && !(tiles[x3 + (y3*World.WIDTH)] instanceof ObstacleTile)) ||
                 (tiles[x4 + (y4*World.WIDTH)] instanceof WallTile
             && !(tiles[x4 + (y4*World.WIDTH)] instanceof ObstacleTile)));
    }

    public static void restartGame (String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.npcs.clear();
		Game.collectables.clear();
		Game.projectiles.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.npcs = new ArrayList<Npc>();
        Game.collectables = new ArrayList<Collectable>();
		Game.projectiles = new ArrayList<Projectile>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0,0,16,16,0,0, 0, 0, 16, 16);
        Player.life = Player.max_life; Player.mana = 0; 
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
        //Game.world = new World();
	}

    public static void loadLevel (String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.npcs.clear();
		Game.collectables.clear();
		Game.projectiles.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.npcs = new ArrayList<Npc>();
		Game.collectables = new ArrayList<Collectable>();
		Game.projectiles = new ArrayList<Projectile>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		Game.ui.createMinimap();
        //Game.world = new World();
	}

    public static double calculateDistance (int x1, int y1, int x2, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));		
	}

	public void render(Graphics2D g2d) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;

		int xfinal = xstart + (Game.WIDTH >> 4) + 1;
		int yfinal = ystart + (Game.HEIGHT >> 4) + 1;

		/*if (xstart < 0) xstart = 0;
   		if (ystart < 0) ystart = 0;

    	// Impede que passemos do tamanho máximo do mapa (neste caso, usando as variáveis do próprio mapa)
    	if (xfinal >= WIDTH) xfinal = WIDTH - 1;
    	if (yfinal >= HEIGHT) yfinal = HEIGHT - 1;*/

		for (int xx = xstart; xx <= xfinal; xx++) {
			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
                if (calculateDistance(Camera.x + Game.WIDTH/2, Camera.y + Game.HEIGHT/2,
                    xx*16, yy*16) < Game.SIMULATION_DISTANCE + 32) {
				    Tile tile = tiles[xx + (yy*WIDTH)];
				    tile.render(g2d);
                }
			}
		}
	}

    public static void renderMinimap () {
        for (int i = 0; i < UI.minimapPixels.length; i++) {
            UI.minimapPixels[i] = 0;
        }

        for (int xx = 0; xx < WIDTH; xx++) {
            for (int yy = 0; yy < HEIGHT; yy++) {
                /*if (tiles[xx + (yy*WIDTH)] instanceof FloorTile) {
                    UI.minimapPixels[xx + (yy*WIDTH)] = 0xc0000000;
                }*/
                if (tiles[xx + (yy*WIDTH)] instanceof WallTile) {
                    UI.minimapPixels[xx + (yy*WIDTH)] = 0xff00ff00;
                }

                if (tiles[xx + (yy*WIDTH)] instanceof ObstacleTile) {
                    UI.minimapPixels[xx + (yy*WIDTH)] = 0xff0000ff;
                }
            }
        }

        int xPlayer = (Game.player.getX()+8) / 16;
        int yPlayer = (Game.player.getY()+8) / 16;
        UI.minimapPixels[xPlayer + (yPlayer*WIDTH)] = 0xffffffff;

        int xEnemy = 0, yEnemy = 0;
        for (int i = 0; i < Game.enemies.size(); i++) {
            xEnemy = (Game.enemies.get(i).getX()+8) / 16;
            yEnemy = (Game.enemies.get(i).getY()+8) / 16;
            UI.minimapPixels[xEnemy + (yEnemy*WIDTH)] = 0xffff0000;
        }

        int xCollect = 0, yCollect = 0;
        for (int i = 0; i < Game.collectables.size(); i++) {
            Collectable c = Game.collectables.get(i);
            xCollect = (c.getX()+8) / 16;
            yCollect = (c.getY()+8) / 16;
            if (c instanceof SpellBook)
                UI.minimapPixels[xCollect + (yCollect*WIDTH)] = 0xffff006E;
            else
                UI.minimapPixels[xCollect + (yCollect*WIDTH)] = 0xffffD800;
        }
        
    }

}
