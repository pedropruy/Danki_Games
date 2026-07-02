package com.peperonistudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.peperonistudios.entities.Entity;
import com.peperonistudios.entities.Collectable;
import com.peperonistudios.entities.Enemy;
import com.peperonistudios.entities.Player;
import com.peperonistudios.entities.Projectile;
import com.peperonistudios.graficos.Spritesheet;
import com.peperonistudios.graficos.UI;
import com.peperonistudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private  Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 240;
	public static final int SCALE = 3;
	public static int CURRENT_LEVEL = 1, MAX_LEVEL = 2;
	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Collectable> collectables;
	public static List<Projectile> projectiles;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	public static boolean playerAction = true;

	public static Random rand;

	public UI ui;
	public Menu menu;

	public static String gameState = "Normal";
	private static boolean showMessageGameOver = false;
	private static int framesMessageGameOver = 0;
	private static boolean restartGame = false;

	
	public Game() {
		Sound.musicBackground.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		collectables = new ArrayList<Collectable>();
		projectiles = new ArrayList<Projectile>();

		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(0, 0, 16, 16), 0, 0, 16, 16);
		entities.add(player);
		ui = new UI();
		world = new World("/level1.png");

		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("The Legend of Karma");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		requestFocus();
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
		} else if (gameState == "Normal") {
			restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).tick();
			}

			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).tick();
			}

			if (enemies.size() == 0) {
				CURRENT_LEVEL++;
				if (CURRENT_LEVEL > MAX_LEVEL) {
					CURRENT_LEVEL = 1;
				}
				String newWorld = "level" + CURRENT_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GameOver") {
			framesMessageGameOver++;

			if (framesMessageGameOver == 25) {
				framesMessageGameOver = 0;
				if (showMessageGameOver) {
					showMessageGameOver = false;
				} else { showMessageGameOver = true; }
			}

			if (restartGame) {
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

		if (gameState == "Menu" && !menu.pause) {
			menu.render(g2d);

			g2d.dispose();
			g2d = (Graphics2D) bs.getDrawGraphics();
			g2d.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		} else {		
			world.render(g2d);
		
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.render(g2d);
			}

			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).render(g2d);
			}
			ui.render(g2d);

			if(menu.pause) menu.render(g2d);
			
			g2d.dispose();
			g2d = (Graphics2D) bs.getDrawGraphics();
			g2d.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
			// Caso eu queira que a fonte nn use o scale
		

			if (gameState == "GameOver") {
				g2d.setColor(new Color(0,0,0,150));
				g2d.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
				g2d.setFont(new Font("arial", Font.BOLD, 28));
				g2d.setColor(Color.WHITE);
				g2d.drawString("GAME OVER!", 85*SCALE, 115*SCALE);
				g2d.setFont(new Font("arial", Font.BOLD, 20));
				if (showMessageGameOver)
					g2d.drawString("> Pressione espaço para reiniciar <", 48*SCALE, 127*SCALE);
			}
		}
		bs.show();
	}
	
	public void run() {
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
			menu.pause = true;
			gameState = "Menu";
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
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameState == "GameOver") restartGame = true;
			else player.jumped = true;
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

		if (gameState == "Menu") {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				menu.optionSelected = true;
			}
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

		if (e.getKeyCode() == KeyEvent.VK_C) {
			playerAction = true;
			player.isCasting = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_V) {
			playerAction = true;
			player.nextSpell = false;
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
