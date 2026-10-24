package com.example.screenpet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PetWindow extends JWindow {
    private final int SPRITE_SIZE = 120;
    private final PetBrain brain;
    private final Timer gameLoop;

    private Point dragOffset = null;
    private boolean isDragging = false;
    private int velocityY = 0;
    private Rectangle screenBounds;

    public PetWindow() {
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        setSize(SPRITE_SIZE, SPRITE_SIZE);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        screenBounds = gd.getDefaultConfiguration().getBounds();

        setLocation(screenBounds.width / 2, screenBounds.height - SPRITE_SIZE - 60);

        brain = new PetBrain();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                brain.draw(g2, SPRITE_SIZE, SPRITE_SIZE, getX(), getY());
            }
        };
        panel.setOpaque(false);
        setContentPane(panel);

        setupMouseControls(panel);
        setupMenu();

        gameLoop = new Timer(16, e -> {
            updatePhysics();
            panel.repaint();
        });
        gameLoop.start();
    }

    public void start() {
        setVisible(true);
    }

    private void updatePhysics() {
        if (isDragging) return;

        int x = getX();
        int y = getY();
        int floorY = screenBounds.height - SPRITE_SIZE - 40;

        if (x < 0) x = 0;
        if (x > screenBounds.width - SPRITE_SIZE) x = screenBounds.width - SPRITE_SIZE;

        if (y < floorY) {
            velocityY += 1;
            y += velocityY;

            if (x <= 5 && brain.getState() != PetState.FALL) {
                x = -25;
                brain.setClimbDirection(false);
                brain.setState(PetState.CLIMB);

                if (y <= 0) {
                    y = 0;
                    brain.setState(PetState.FALL);
                    velocityY = 2;
                    x = 10;
                } else {
                    y -= 2;
                }
                velocityY = 0;

            } else if (x >= screenBounds.width - SPRITE_SIZE - 5 && brain.getState() != PetState.FALL) {
                x = screenBounds.width - SPRITE_SIZE + 25;
                brain.setClimbDirection(true);
                brain.setState(PetState.CLIMB);

                if (y <= 0) {
                    y = 0;
                    brain.setState(PetState.FALL);
                    velocityY = 2;
                    x = screenBounds.width - SPRITE_SIZE - 10;
                } else {
                    y -= 2;
                }
                velocityY = 0;

            } else if (brain.getState() != PetState.CLIMB && brain.getState() != PetState.FALL) {
                brain.setState(PetState.FALL);
            }

            if (y >= floorY) {
                y = floorY;
                velocityY = 0;
                brain.setState(PetState.IDLE);
            }
        } else {
            if (brain.getState() == PetState.IDLE) {
                if (brain.shouldWalk()) {
                    brain.setState(PetState.WALK);
                    x += brain.getWalkDirection() * 2;

                    if (x <= 0) {
                        x = 0;
                        brain.turnAround();
                    } else if (x >= screenBounds.width - SPRITE_SIZE) {
                        x = screenBounds.width - SPRITE_SIZE;
                        brain.turnAround();
                    }
                }

                if (brain.shouldJumpRandomly()) {
                    brain.triggerJump();
                    velocityY = -12;
                }
            } else if (brain.getState() == PetState.WALK) {
                x += brain.getWalkDirection() * 2;
                if (x <= 0) {
                    x = 0;
                    brain.turnAround();
                } else if (x >= screenBounds.width - SPRITE_SIZE) {
                    x = screenBounds.width - SPRITE_SIZE;
                    brain.turnAround();
                }

                if (!brain.shouldWalk()) {
                    brain.setState(PetState.IDLE);
                }
            } else if (brain.getState() == PetState.JUMP) {
                if (brain.isJumpFinished()) {
                    brain.setState(PetState.IDLE);
                }
            }
        }

        setLocation(x, y);
    }

    private void setupMouseControls(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragOffset = e.getPoint();
                    isDragging = true;
                    velocityY = 0;
                    brain.setDragging(true);
                    brain.setState(PetState.JUMP);

                    // УВЕЛИЧИВАЕМ ОКНО при захвате (например, до 200x200)
                    setSize(200, 200);
                    // Сдвигаем окно, чтобы оно не прыгнуло в сторону
                    setLocation(getX() - 40, getY() - 40);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isDragging = false;
                    dragOffset = null;
                    velocityY = 0;
                    brain.setDragging(false);

                    // ВОЗВРАЩАЕМ ОКНО обратно к 120x120
                    setSize(SPRITE_SIZE, SPRITE_SIZE);
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && dragOffset != null) {
                    Point p = e.getLocationOnScreen();
                    setLocation(p.x - 40, p.y - 10);
                }
            }
        });
    }

    private void setupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);

        getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
}