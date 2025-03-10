package org.oriontransfer.snails;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;
import java.util.Vector;
import javax.microedition.lcdui.Font;

/**
 *
 * @author samuel
 */
public class GameWorld extends GameCanvas implements Runnable {
    final int W = 16;
    final int H = 16;

    private TiledLayer terrainLayer;
    private TiledLayer waterLayer;
    private TiledLayer gemsLayer;
    private TiledLayer leafLayer;

    private int totalGems;
    private int collectedGems;
    
    private LayerManager layerManager;

    private volatile boolean running = true;

    private GameEntity player;
    private Vector monsters;

    public GameWorld() {
        super(true);
    }

    public void start() throws IOException {
        Image grassTileSet = Image.createImage("tileset/grass.png");
        Image waterTileSet = Image.createImage("tileset/water.png");
        Image gemsTileSet = Image.createImage("tileset/gems.png");
        Image leafTileSet = Image.createImage("tileset/leaf.png");

        terrainLayer = new TiledLayer(W, H, grassTileSet, 40, 40);
        waterLayer = new TiledLayer(W, H, waterTileSet, 40, 40);
        gemsLayer = new TiledLayer(W, H, gemsTileSet, 40, 40);
        leafLayer = new TiledLayer(W, H, leafTileSet, 40, 40);

        collectedGems = 0;

        int[] terrainData = {
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,1,2,2,2,2,3,4,4,4,4,4,
            4,4,4,4,4,6,7,7,7,7,8,4,4,4,4,4,
            4,4,4,4,4,6,7,7,7,7,8,4,4,4,4,4,
            4,4,4,4,4,11,12,12,12,12,13,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
            4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4
        };

        int[] waterData = {
            21,25,25,25,25,29,25,25,25,25,29,25,25,25,25,18,
            22,0,0,0,0,22,0,0,0,0,22,0,0,0,0,22,
            22,0,31,0,17,23,0,17,27,0,24,27,0,19,0,22,
            22,0,0,0,0,0,0,0,0,0,0,0,0,22,0,22,
            16,25,27,0,31,0,17,25,25,27,0,31,0,24,25,20,
            22,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22,
            22,0,19,0,19,0,0,0,0,0,0,19,0,19,0,22,
            22,0,22,0,22,0,0,0,0,0,0,22,0,22,0,22,
            22,0,22,0,22,0,0,0,0,0,0,22,0,22,0,22,
            22,0,30,0,24,29,25,25,27,0,21,23,0,30,0,22,
            22,0,0,0,0,22,0,0,0,0,22,0,0,0,0,22,
            16,25,25,25,25,20,0,17,25,25,26,25,25,27,0,22,
            22,0,0,0,0,22,0,0,0,0,30,0,0,0,0,22,
            22,0,17,25,25,23,0,17,27,0,0,0,17,27,0,22,
            22,0,0,0,0,0,0,0,0,0,19,0,0,0,0,22,
            24,25,25,25,25,25,25,25,25,25,28,25,25,25,25,23
        };

        int[] gemsData = {
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,32,32,32,33,0,32,32,32,32,0,32,32,32,32,0,
            0,32,0,32,0,0,32,0,0,32,0,0,32,0,32,0,
            0,32,32,32,32,32,32,32,32,32,32,32,32,0,34,0,
            0,0,0,32,0,32,0,0,0,0,32,0,32,0,0,0,
            0,32,32,32,32,0,0,0,0,0,0,32,32,32,32,0,
            0,32,0,32,0,0,0,0,0,0,0,0,32,0,32,0,
            0,32,0,32,0,0,0,0,0,0,0,0,32,0,32,0,
            0,32,0,32,0,0,0,0,0,0,0,0,32,0,32,0,
            0,32,0,32,0,0,0,0,0,32,0,0,32,0,32,0,
            0,32,32,32,33,0,32,32,32,32,0,33,32,32,32,0,
            0,0,0,0,0,0,32,0,0,0,0,0,0,0,32,0,
            0,32,32,32,34,0,32,32,32,32,0,32,32,32,32,0,
            0,32,0,0,0,0,32,0,0,32,32,32,0,0,32,0,
            0,32,32,32,32,32,32,32,32,32,0,32,32,32,32,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
        };

        int[] leafData = {
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,35,36,37,38,39,40,0,0,0,0,0,
            0,0,0,0,0,41,42,43,44,45,46,0,0,0,0,0,
            0,0,0,0,0,47,48,49,50,51,52,0,0,0,0,0,
            0,0,0,0,0,53,54,55,56,57,58,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
        };

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int offset = x + (y * W);

                if (terrainData[offset] != 0) {
                    terrainLayer.setCell(x, y, terrainData[offset]);
                }

                if (waterData[offset] != 0) {
                    waterLayer.setCell(x, y, waterData[offset] - 15);
                }

                if (gemsData[offset] != 0) {
                    gemsLayer.setCell(x, y, gemsData[offset] - 31);
                    
                    totalGems++;
                }

                if (leafData[offset] != 0) {
                    leafLayer.setCell(x, y, leafData[offset] - 34);
                }
            }
        }

        layerManager = new LayerManager();
        layerManager.append(leafLayer);

        player = new GameEntity("yoshi");
        player.setPosition(40, 40);
        player.setSpeed(3);
        layerManager.append(player);

        monsters = new Vector();

        for (int i = 0; i < 6; i++) {
            GameMonster monster = new GameMonster("snail");
            monster.setPosition(320, 280);
            monster.setSpeed(1);

            monsters.addElement(monster);
            layerManager.append(monster);
        }

        layerManager.append(gemsLayer);
        layerManager.append(waterLayer);
        layerManager.append(terrainLayer);

        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop() {
        running = false;
    }

    public void run() {
        Graphics g = getGraphics();

        while (running) {
            gameUpdate();
            gameDraw(g);

            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                running = false;
            }
        }
    }

    private void playerDied () {
        player.setPosition(40, 40);

        for (int i = 0; i < monsters.size(); i++) {
            GameMonster monster = (GameMonster)(monsters.elementAt(i));
            monster.setPosition(320, 280);
        }
    }

    private void gameUpdate() {
        int keyState = getKeyStates();

        if ((keyState & UP_PRESSED)!= 0) {
            player.move(GameEntity.NORTH);
        } else if ((keyState & DOWN_PRESSED) != 0) {
            player.move(GameEntity.SOUTH);
        } else if ((keyState & LEFT_PRESSED) != 0) {
            player.move(GameEntity.WEST);
        } else if ((keyState & RIGHT_PRESSED) != 0) {
            player.move(GameEntity.EAST);
        } else {
            player.move(0);
        }

        player.tick(waterLayer);

        if (player.collidesWith(gemsLayer, true)) {
            int x, y;

            x = (player.getX() + 20) / 40;
            y = (player.getY() + 20) / 40;

            gemsLayer.setCell(x, y, 0);
            collectedGems++;
        }

        for (int i = 0; i < monsters.size(); i++) {
            GameMonster monster = (GameMonster)(monsters.elementAt(i));
            
            monster.tick(waterLayer);

            if (monster.collidesWith(player, true)) {
                playerDied();
                return;
            }
        }

    }

    private void gameDraw(Graphics g) {
        int w = getWidth(), h = getHeight();

        g.setColor(0xcccccc);
        g.fillRect(0, 0, w, h);

        layerManager.setViewWindow((int)(player.getX() - w / 2.0), (int)(player.getY() - h / 2.0), w, h);
        layerManager.paint(g, 0, 0);

        String s = "Score: " + Integer.toString(collectedGems);

        Font font = g.getFont();
        int sw = font.stringWidth(s) + 2;
        int sh = font.getHeight();

        // Draw the render capacity.
        g.setColor(0xffffff);
        g.fillRect(w - sw, h - sh, sw, sh);
        g.setColor(0x000000);
        g.drawRect(w - sw, h - sh, sw, sh);
        g.drawString(s, w, h, Graphics.RIGHT | Graphics.BOTTOM);
        
        flushGraphics();
    }
}
