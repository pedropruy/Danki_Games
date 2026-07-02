package com.peperonistudios.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.peperonistudios.entities.Collectable;
import com.peperonistudios.entities.Enemy;
import com.peperonistudios.entities.Entity;
import com.peperonistudios.entities.LifeElixir;
import com.peperonistudios.entities.ManaElixir;
import com.peperonistudios.entities.Player;
import com.peperonistudios.entities.Projectile;
import com.peperonistudios.entities.SpellBook;
import com.peperonistudios.graficos.Spritesheet;
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
            case 0xFFF76200:
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
                Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, Entity.PURP_SLIME_EN,
                                     2 , 3, 13, 11);
                Game.entities.add(en);
                Game.enemies.add(en);
                break;
            case 0xFF007F0E:
                // HEALTH ELIXIR
                LifeElixir le = new LifeElixir(xx * 16, yy * 16, 16, 16, Entity.LIFE_ELIXIR_EN,
                                               3, 3, 10, 13);
                Game.entities.add(le);
                Game.collectables.add(le);
                break;
            case 0xFF007F7F:
                // MANA ELIXIR
                ManaElixir me = new ManaElixir(xx * 16, yy * 16, 16, 16, Entity.MANA_ELIXIR_EN,
                                               3, 3, 10, 13);
                Game.entities.add(me);
                Game.collectables.add(me);
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

        return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
                 (tiles[x4 + (y4*World.WIDTH)] instanceof WallTile));
    }

    public static void restartGame (String level) {
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
		// Possivelmente um resetPlayer aqui!
        Player.life = Player.max_life; Player.mana = 0; 
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
	}

    public static void loadLevel (String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.collectables.clear();
		Game.projectiles.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.collectables = new ArrayList<Collectable>();
		Game.projectiles = new ArrayList<Projectile>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
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
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g2d);
			}
		}
	}
}
