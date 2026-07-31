package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.peperonistudios.graficos.UI;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.World;

public class Npc extends Creature {

    public String[] frases = new String[3];
    public int fraseIndex = 0;
    public boolean wasInteracted = false;

    public int movingDuration = 0, movingDurationMax = 60;
    public int movedCooldown = 0, movedCooldownMax = 120;
    public int movementDir = Game.rand.nextInt(4);

    public Npc(int x, int y, int width, int height, int xsprite, int ysprite, int maskx, int masky, int maskw,
            int maskh) {
        super(x, y, width, height, xsprite, ysprite, maskx, masky, maskw, maskh);

        this.offsetShadow = 2;
        this.speed = 0.5;
        
        frases[0] = "Olá! Seja muito bem-vindo ao jogo!";
        frases[1] = "sejrverj jr cwje cjew rwje cwer wejr wcj wcejrc wr wjcrh wejwh wjchw rjw df";
        frases[2] = "sejrverj jr cwje cjew rwje cwer wejr wcj wcejrc wr wjcrh wejwh wjchw rjw df";
    }

    public void tick() {
        if (!isInteracting) {
            if (moved) {
                npcMovement_basic();
                movingDuration++;
                if (movingDuration == movingDurationMax) {
                    movingDuration = 0;
                    moved = false;
                }
            } else {
                movedCooldown++;
                if (movedCooldown == movedCooldownMax) {
                    movedCooldown = 0;
                    moved = true;
                    movementDir = Game.rand.nextInt(4);
                }
            }
        } else {
            // Mudar a direção que o NPC olha qnd é interagido e impedir player de atravessar NPC
            if (!UI.showMessage) {
                isInteracting = false;
                Game.player.isInteracting = false;
                UI.curIndex = 0;
            }
        }

        if (wasInteracted) {
            wasInteracted = false;
            isInteracting = true;
            UI.message = frases[fraseIndex];
            UI.showMessage = true;

            if (fraseIndex < frases.length - 1) fraseIndex++;
            else fraseIndex = 0;

            moved = false;
            movedCooldown = 0;
            movingDuration = 0;
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
    }

    private void npcMovement_basic() {
        if (this.isCollidingWithPlayer() == false) {
			if (movementDir == 0 && World.isFreeCreature((int)(x+speed), this.getY(), 0)) {
                if (!isColliding((int)(x+speed), this.getY())) {
				    moved = true;
				    dir = right_dir;
				    x += speed;
                } else moved = false;
			}
			else if (movementDir == 1 && World.isFreeCreature((int)(x-speed), this.getY(), 0)) {
				if (!isColliding((int)(x-speed), this.getY())) {
		    		moved = true;
    		    	dir = left_dir;
				    x -= speed;
                } else moved = false;
			}
			else if (movementDir == 2 && World.isFreeCreature(this.getX(), (int)(y+speed), 0)) {
				if (!isColliding(this.getX(), (int)(y+speed))) {
    				moved = true;
	    			dir = down_dir;
		    		y += speed;
                } else moved = false;
			}
			else if (movementDir == 3 && World.isFreeCreature(this.getX(), (int)(y-speed), 0)) {
				if (!isColliding(this.getX(), (int)(y-speed))) {
	    			moved = true;
		    		dir = up_dir;
			    	y -= speed;
                } else moved = false;
			}	
		} else {}
    }

    public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		return enemyCurrent.intersects(player);
	}

    public void render(Graphics2D g2d) {
		super.render(g2d);
    }    
}
