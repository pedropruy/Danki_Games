package com.peperonistudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
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
	private final int SCALE = 3;
	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Collectable> collectables;
	public static List<Projectile> projectiles;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;

	public static Random rand;

	public UI ui;
	
	public Game() {
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
		world = new World("/map.png");
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
		//new Thread(game).start();
	}
	
	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).tick();
		}

		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).tick();
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
		
		
		/*Renderização do jogo
		Graphics2D g2 = (Graphics2D) g;*/
		world.render(g2d);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g2d);
		}

		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).render(g2d);
		}
		ui.render(g2d);
		/***/
		g2d.dispose();
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		// Caso eu queira que a fonte nn use o scale
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
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
			e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_C) {
			player.isCasting = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_V) {
			player.nextSpell = true;
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
			player.isCasting = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_V) {
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
