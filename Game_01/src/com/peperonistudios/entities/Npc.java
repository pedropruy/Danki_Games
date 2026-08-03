package com.peperonistudios.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.peperonistudios.graficos.UI;
import com.peperonistudios.main.Game;
import com.peperonistudios.world.World;

public class Npc extends Creature {

    public int movingDuration = 0, movingDurationMax = 60;
    public int movedCooldown = 110, movedCooldownMax = 120;
    public int movementDir = Game.rand.nextInt(4);

    public Npc(int x, int y, int width, int height, int xsprite, int ysprite, int maskx, int masky, int maskw,
            int maskh) {
        super(x, y, width, height, xsprite, ysprite, maskx, masky, maskw, maskh);

        this.offsetShadow = 2;
        this.speed = 0.4;

        setDialogue();
    }

    public void tick() {
        if (!isInteracting) {
            npcMovement_basic();
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

    public void setAction() {
        boolean validAction = false;
        while (!validAction) {
            int i = Game.rand.nextInt(100)+1; // Número aleatório de 1 a 100
            if (i <= 25) {
                movementDir = 0;
                if (!isColliding((int)(x+speed), this.getY()))
                    validAction = true; 
            } else if (i > 25 && i <= 50) {
                movementDir = 1;
                if (!isColliding((int)(x-speed), this.getY()))
                    validAction = true;
            } else if (i > 50 && i <= 75) {
                movementDir = 2;
                if (!isColliding(this.getX(), (int)(y+speed)))
                    validAction = true;
            } else if (i > 75 && i <= 100) {
                movementDir = 3;
                if (!isColliding(this.getX(), (int)(y-speed)))
                    validAction = true;
            }
        }
        movingDurationMax = Game.rand.nextInt(30, 120);
        movedCooldownMax = Game.rand.nextInt(60, 180);
    }

    public void setDialogue() {
        dialogue[0] = "Elimine todas as gosmas e você será muito \nbem recompensado!";
        dialogue[1] = "Olá! Seja muito bem-vindo ao jogo!";
        dialogue[2] = "Cuidado com as gosmas roxas.";
    }

    public void speak() {
        UI.newDialogue(dialogue[dialogueIndex]);
        Game.gameState = "Dialogue";

        dialogueIndex++;
        if(dialogue[dialogueIndex] == null) dialogueIndex = 0;

        moved = false;
        movedCooldown = 0;
        movingDuration = 0;

        switch (Game.player.dir) {
            case 0:
                // Player olhando pela Direita
                this.dir = 1;
                break;
            case 1:
                // Player olhando pela Esquerda
                this.dir = 0;
                break;
            case 2:
                // Player olhando pela Cima
                this.dir = 3;
                break;
            case 3:
                // Player olhando pela Baixo
                this.dir = 2;
                break;
        }
    }

    private void npcMovement_basic() {    
        if (moved) {
            if (movementDir == 0) {
                if (!World.isFreeCreature((int)(x+speed), this.getY(), 0))
                    movingDuration = movingDurationMax;
                else if (isColliding((int)(x+speed), this.getY()))
                    movingDuration = movingDurationMax;
                else {
                    moved = true;
                    dir = right_dir;
                    x += speed;
                }
            } else if (movementDir == 1) {
                if (!World.isFreeCreature((int)(x-speed), this.getY(), 0))
                    movingDuration = movingDurationMax;
                else if (isColliding((int)(x-speed), this.getY()))
                    movingDuration = movingDurationMax;
                else {
                    moved = true;
                    dir = left_dir;
                    x -= speed;
                }
            } else if (movementDir == 2) {
                if (!World.isFreeCreature(this.getX(), (int)(y+speed), 0))
                    movingDuration = movingDurationMax;
                else if (isColliding(this.getX(), (int)(y+speed)))
                    movingDuration = movingDurationMax;
                else {
                    moved = true;
                    dir = down_dir;
                    y += speed;
                }
            } else if (movementDir == 3) {
                if (!World.isFreeCreature(this.getX(), (int)(y-speed), 0))
                    movingDuration = movingDurationMax;
                else if (isColliding(this.getX(), (int)(y-speed)))
                    movingDuration = movingDurationMax; 
                else {
                    moved = true;
                    dir = up_dir;
                    y -= speed;
                }
            } else movingDuration = movingDurationMax;
            
            movingDuration++;
            if (movingDuration >= movingDurationMax) {
                movingDuration = 0;
                moved = false;
            }
        } else {
            movedCooldown++;
            if (movedCooldown >= movedCooldownMax) {
                movedCooldown = 0;
                moved = true;
                setAction();
            }
        }
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
