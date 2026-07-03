package com.peperonistudios.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.peperonistudios.main.Game;
import com.peperonistudios.world.Camera;
import com.peperonistudios.world.World;

public class Projectile extends Entity{
    
    public int damage = 0;

    private double dx;
    private double dy;
    private double speed = 3;

    private int duration = 0, max_duration = 20;

    private BufferedImage[] projectileSprite;
    private double rotation_angle = 0;
	private int index = 0;

    public Projectile(int x, int y, int width, int height, String magicName,
                      int maskx, int masky, int maskw, int maskh,
                      double dx, double dy, int max_duration, double angle, int damage) {
		super(x, y, width, height, null, maskx, masky, maskw, maskh);

        this.damage = damage;

        this.dx = dx;
        this.dy = dy;
        this.max_duration = max_duration;

        projectileSprite = new BufferedImage[2];
        switch (magicName) {
            case "basic":
                projectileSprite = Entity.BASIC_ATTACK_EN;
            break;
            
            case "fire":
                projectileSprite = Entity.FIRE_BALL_EN;
            break;

            case "ice":
                projectileSprite = Entity.ICE_CRYSTAL_EN;
            break;

            default:
                projectileSprite = Entity.BASIC_ATTACK_EN;
            break;
        }
    
        this.rotation_angle = angle;
	}

    public void tick() {
        x += dx * speed;
        y += dy * speed;

        if(!World.isFreeProjectile((int)x, (int)y)) {
            Game.projectiles.remove(this);
        }

        duration++;
        if (duration == max_duration) {
            Game.projectiles.remove(this);
            return;
        }
        if (duration >= 10) index = 0;
        else index = 1;
    }

    public void render(Graphics2D g2d) {
        // Cria cópia para não alterar original
        Graphics2D g2d_aux = (Graphics2D) g2d.create();
    
        // Pega o centro da imagem
        int centerX = this.getX() - Camera.x + (projectileSprite[index].getWidth() / 2);
        int centerY = this.getY() - Camera.y + (projectileSprite[index].getHeight() / 2);

        g2d_aux.translate(centerX, centerY);
        g2d_aux.rotate(Math.toRadians(rotation_angle));
        g2d_aux.translate(-centerX, -centerY);

        // Hitbox
        /*g2d.setColor(Color.BLUE);
        g2d.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y,
                     maskw, maskh);*/

        g2d_aux.drawImage(projectileSprite[index], this.getX() - Camera.x,
                          this.getY() - Camera.y, null);

        g2d_aux.dispose();
    }
}
