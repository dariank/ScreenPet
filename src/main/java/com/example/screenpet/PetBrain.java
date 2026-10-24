package com.example.screenpet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class PetBrain {
    private PetState state = PetState.IDLE;
    private final Random random = new Random();

    private int walkTimer = 0;
    private int jumpTimer = 0;
    private int walkDirection = 1;

    private boolean isClimbingRight = true;
    private boolean isDragging = false;
    private int currentFrame = 0;
    private int animTick = 0;
    private final int ANIM_SPEED = 15;
    private boolean isReversing = false;

    private BufferedImage imgIdle, imgJump, imgFall, imgUp;
    private BufferedImage[] walkRightFrames;
    private BufferedImage[] walkLeftFrames;
    private BufferedImage[] climbRightFrames;
    private BufferedImage[] climbLeftFrames;

    public PetBrain() {
        loadSprites();
    }

    private void loadSprites() {
        imgIdle = loadImage("idle.png");
        imgJump = loadImage("jump.png");
        imgFall = loadImage("fall.png");
        imgUp = loadImage("up.png");

        walkRightFrames = new BufferedImage[]{
                loadImage("rightd1.png"),
                loadImage("rightd2.png"),
                loadImage("rightd3.png"),
                loadImage("rightd2.png"),
                loadImage("rightd1.png")
        };

        walkLeftFrames = new BufferedImage[]{
                loadImage("leftd1.png"),
                loadImage("leftd2.png"),
                loadImage("leftd3.png"),
                loadImage("leftd2.png"),
                loadImage("leftd1.png")
        };


        climbLeftFrames = new BufferedImage[]{
                loadImage("climbone.png"),
                loadImage("climbtwo.png"),
        };

        climbRightFrames = new BufferedImage[]{
                loadImage("climboneright.png"),
                loadImage("climbtworight.png"),
        };
    }

    private BufferedImage loadImage(String name) {
        try (InputStream is = getClass().getResourceAsStream("/sprites/" + name)) {
            if (is != null) return ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("Не найдена картинка: " + name);
        }
        return null;
    }

    public void setClimbDirection(boolean right) {
        this.isClimbingRight = right;
    }

    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }
    public void update() {
        if (walkTimer > 0) walkTimer--;

        if (state == PetState.JUMP) {
            jumpTimer++;
            if (jumpTimer > 40) {
                state = PetState.IDLE;
                jumpTimer = 0;
            }
        }

        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;

            if (state == PetState.WALK) {
                if (!isReversing) {
                    currentFrame++;
                    if (currentFrame >= walkRightFrames.length - 1) {
                        isReversing = true;
                    }
                } else {
                    currentFrame--;
                    if (currentFrame <= 0) {
                        isReversing = false;
                    }
                }
            } else if (state == PetState.CLIMB) {

                currentFrame = (currentFrame + 1) % 2;
            }
        }
    }

    public void draw(Graphics2D g, int width, int height, int windowX, int windowY) {
        update();

        BufferedImage currentImage = imgIdle;
        int offsetX = 0;
        int offsetY = 0;

        double scale = 1.0;
        double scaleX = 1.0;

        if (isDragging && imgUp != null) {

            int newWidth = (int) (width * 1.25 );

            double koef = ((double) width / imgUp.getWidth());
            int newHeight = (int) (imgUp.getHeight() * koef);

            int drawX = (width - newWidth) / 2;
            int drawY = (height - newHeight) / 2 - 5;

            g.drawImage(imgUp, drawX, -20, newWidth, newHeight, null);
            return;
        }
        if (state == PetState.WALK) {
            scaleX = 0.85;

            if (walkDirection > 0) {
                currentImage = walkRightFrames[currentFrame];
                scale = 1.08;
                offsetY = 0;

                if (currentFrame == 0 || currentFrame == 4) {
                    scale = 1.15;
                    offsetY = -2;
                }
                if (currentFrame == 3) {
                    scale = 1.08;
                    offsetY = 0;
                }

                if (currentFrame == 0 || currentFrame == 4) { offsetX = 2; }
                if (currentFrame == 1 || currentFrame == 3) { offsetX = 4; }
                if (currentFrame == 2) { offsetX = 6; }

            } else {
                currentImage = walkLeftFrames[currentFrame];
                scale = 1.12;
                offsetY = -2;

                if (currentFrame == 0 || currentFrame == 4) {
                    scale = 1.12;
                    offsetY = -4;
                }
                if (currentFrame == 3) {
                    scale = 1.10;
                    offsetY = -2;
                }

                if (currentFrame == 0 || currentFrame == 4) { offsetX = -2; }
                if (currentFrame == 1 || currentFrame == 3) { offsetX = -4; }
                if (currentFrame == 2) { offsetX = -6; }
            }
        } else if (state == PetState.CLIMB) {
            if (isClimbingRight) {
                currentImage = climbRightFrames[currentFrame];
            } else {
                currentImage = climbLeftFrames[currentFrame];
            }

            if (currentFrame == 0) {

                scale = 0.95;
                offsetY = 0;
            } else if (currentFrame == 1) {
                scale = 0.95;
                offsetY = 0;
            }
        }
        else if (state == PetState.JUMP) {
            currentImage = imgJump;
            scale = 1.18;
            offsetY = 5;
        } else if (state == PetState.FALL) {
            currentImage = imgFall;
            scale = 1.08;
        } else if (state == PetState.IDLE) {
            currentImage = imgIdle;
            scale = 1.12;
            offsetY = -2;
        }

        if (currentImage != null) {
            int newWidth = (int) (width * scale * scaleX);
            int newHeight = (int) (height * scale);

            int drawX = (width - newWidth) / 2 + offsetX;
            int drawY = (height - newHeight) / 2 + offsetY;

            g.drawImage(currentImage, drawX, drawY, newWidth, newHeight, null);
        } else {
            g.setColor(new Color(255, 105, 180));
            g.fillOval(width/4, height/4, width/2, height/2);
            g.setColor(Color.BLACK);
            g.drawString(state.name(), 10, 20);
        }
    }

    public boolean shouldWalk() {
        if (walkTimer > 0) return true;
        if (state == PetState.IDLE && random.nextInt(100) < 0.5) {
            walkTimer = 100 + random.nextInt(200);
            return true;
        }
        return false;
    }

    public boolean shouldJumpRandomly() {
        return state == PetState.IDLE && random.nextInt(100) < 1;
    }

    public boolean isJumpFinished() {
        return state == PetState.JUMP && jumpTimer >= 40;
    }

    public int getWalkDirection() { return walkDirection; }

    public void turnAround() {
        walkDirection *= -1;
        currentFrame = 0;
        isReversing = false;
    }

    public void triggerJump() {
        if (state != PetState.JUMP && state != PetState.FALL) {
            state = PetState.JUMP;
            jumpTimer = 0;
        }
    }

    public void setState(PetState newState) {
        if (this.state != newState) {
            this.state = newState;
            if (newState == PetState.IDLE) {
                jumpTimer = 0;
                currentFrame = 0;
                isReversing = false;
            }
            if (newState == PetState.CLIMB) {
                currentFrame = 0;
            }
        }
    }
    public PetState getState() { return state; }
}