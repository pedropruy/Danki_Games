package com.peperonistudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.peperonistudios.entities.Entity;
import com.peperonistudios.entities.Npc;
import com.peperonistudios.entities.Collectable;
import com.peperonistudios.entities.Creature;
import com.peperonistudios.entities.Enemy;
import com.peperonistudios.entities.Player;
import com.peperonistudios.entities.Projectile;
import com.peperonistudios.graficos.Spritesheet;
import com.peperonistudios.graficos.UI;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

	/* jar --create --file=out/Game.jar --main-class=com.peperonistudios.main.Game -C bin . */

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private  Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 240;
	public static final int SCALE = 3;
	// ERROR: a simulação deve estar de acordo com a camera e não a distância do player
	public static final int SIMULATION_DISTANCE = 10*16;
	public static int CURRENT_LEVEL = 1, MAX_LEVEL = 2;
	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Npc> npcs;
	public static List<Collectable> collectables;
	public static List<Projectile> projectiles;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	public static boolean playerAction = true;

	public static Random rand;

	public UI ui;
	public Sound sound;
	public InputStream k_stream = ClassLoader.getSystemClassLoader().getResourceAsStream("KiwiSoda.ttf");
	public static Font kiwi;
	public InputStream h_stream = ClassLoader.getSystemClassLoader().getResourceAsStream("rainyhearts.ttf");
	public static Font hearts;

	public static int[] screenPixels;

	public Menu menu;
	public Cutscene cutscene;

	// Menu, Normal, GameOver, Cutscene, Dialogue
	public static String gameState = "Menu";
	private static boolean restartGame = false;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		screenPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
				
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		npcs = new ArrayList<Npc>();
		collectables = new ArrayList<Collectable>();
		projectiles = new ArrayList<Projectile>();

		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,0,0, 0, 0, 16, 16);
		entities.add(player);

		try {
			kiwi = Font.createFont(Font.TRUETYPE_FONT, k_stream).deriveFont(16f);
			hearts = Font.createFont(Font.TRUETYPE_FONT, h_stream).deriveFont(16f);
		} catch (FontFormatException e) {
		} catch (IOException e) {}

		world = new World("/level1.png");
		//world = new World();

		ui = new UI();
		//ui.createLightmap("/lightmap.png");
		ui.createMinimap();
    
		Sounds.musicBackground.loop();

		menu = new Menu();
		cutscene = new Cutscene();
	}
	
	public void initFrame() {
		frame = new JFrame("The Legend of Karma");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();

		// Icone de Janela
		Image image = null;
		try {
			image = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) { e.printStackTrace(); }
		frame.setIconImage(image);

		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		image = toolkit.getImage(getClass().getResource("/cursor.png"));
		// Point(0, 0) é a origem do cursor, ou seja, a ponta do cursor será em (0, 0) 
		Cursor cursor = toolkit.createCustomCursor(image, new Point(22, 22), "img");
		frame.setCursor(cursor);

		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
    public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
    public synchronized void stop() {
    	isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
    
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if (gameState == "Menu") {
			menu.tick();
		} else if (gameState == "Cutscene") {
			cutscene.tick();
		} else if (gameState == "Dialogue") {
			ui.tick();

			for (int i = 0; i < collectables.size(); i++) {
				if (Creature.calculateDistance(Camera.x + WIDTH/2, Camera.y + HEIGHT/2,
					collectables.get(i)) < SIMULATION_DISTANCE) {
						collectables.get(i).tick();
					}
			}

		} else if (gameState == "Normal") {
			ui.tick();

			// Rodando o tick apenas das entidades dentro da distância de simulação
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i) instanceof Player ||
					Creature.calculateDistance(Camera.x + WIDTH/2, Camera.y + HEIGHT/2,
					entities.get(i)) < SIMULATION_DISTANCE) {
						entities.get(i).tick();
					}
			}

			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).tick();
			}

			if (enemies.size() <= 0) {
				CURRENT_LEVEL++;
				if (CURRENT_LEVEL > MAX_LEVEL) {
					CURRENT_LEVEL = 1;
				}
				String newWorld = "level" + CURRENT_LEVEL + ".png";
				World.loadLevel(newWorld);
			}
		} else if (gameState == "GameOver") {
			ui.tick();

			if (restartGame) {
				restartGame = false;
				String newWorld = "level" + CURRENT_LEVEL + ".png";
				World.restartGame(newWorld);
				gameState = "Normal";
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0,WIDTH,HEIGHT);

		if (gameState == "Menu" && !Menu.pause) {
			menu.render(g2d);

			g2d.dispose();
			g2d = (Graphics2D) bs.getDrawGraphics();
			g2d.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		} else {
			world.render(g2d);

			Collections.sort(entities,Entity.entitySorter);
			// Renderizando apenas o que está dentro da distância de simulação
			for(int i = 0; i < entities.size(); i++) {
				if (entities.get(i) instanceof Player ||
					Creature.calculateDistance(Camera.x + WIDTH/2, Camera.y + HEIGHT/2,
					entities.get(i)) < SIMULATION_DISTANCE)
					entities.get(i).render(g2d);
			}

			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).render(g2d);
			}

			ui.render(g2d);

			if(gameState == "Menu" && Menu.pause) menu.render(g2d);
			
			g2d.dispose();
			g2d = (Graphics2D) bs.getDrawGraphics();
			g2d.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		}
		bs.show();
	}
	
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000/amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime)/ ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer+=1000;
			}	
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (gameState == "Normal") {
				Menu.pause = true;
				Menu.options = Menu.optionsPause;
				menu.maxOption = Menu.options.length - 1;
				gameState = "Menu";
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
			e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
			if (gameState == "Menu") menu.up = true;
			else player.up = true;

		}else if (e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			if (gameState == "Menu") menu.down = true;
			else player.down = true;
		}

		if (playerAction) {
			if (e.getKeyCode() == KeyEvent.VK_C) {
				playerAction = false;
				player.isCasting = true;
			}

			if (e.getKeyCode() == KeyEvent.VK_V) {
				playerAction = false;
				player.nextSpell = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (gameState == "Menu") menu.optionSelected = true;
			else if (gameState == "GameOver") restartGame = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
			e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
			
		if(e.getKeyCode() == KeyEvent.VK_UP ||
			e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameState == "Normal") player.jumped = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_C) {
			playerAction = true;
			player.isCasting = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_V) {
			playerAction = true;
			player.nextSpell = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (gameState == "Normal") {
				//if (UI.showMessage) UI.showMessage = false;
				player.checkInteraction = true;
			}
			if (gameState == "Dialogue") {
				gameState = "Normal";
				UI.endDialogue();
			}
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void mousePressed(MouseEvent e) {
		player.isCastingMouse = true;
		player.mx = (e.getX() / SCALE);
		player.my = (e.getY() / SCALE);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		player.isCastingMouse = false;
		player.mx = 0;
		player.my = 0;
	}
	
}
