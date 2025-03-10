package org.oriontransfer.snails;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.game.TiledLayer;

/**
 *
 * @author samuel
 */
public class GameMonster extends GameEntity {
    private Random brain;
    private static int SEED = 1;

    public GameMonster (String name) throws IOException {
        super(name);

        brain = new Random();
        
        brain.setSeed(System.currentTimeMillis() + SEED);
        SEED++;
    }

    public boolean tick (TiledLayer collisionMap) {
        boolean result = super.tick(collisionMap);

        boolean lX = (getX() % 40) == 0;
        boolean lY = (getY() % 40) == 0;

        //System.err.println("x = " + getX() + ", y = " + getY());
        //System.err.println("lX = " + lX + ", lY = " + lY);

        // Every square, reconsider which direction we are going in
        if (lX && lY) {
            int cX = getX() / 40;
            int cY = getY() / 40;
            boolean cP = false;
            int cD = getCurrentDirection();

            //System.err.println("cX = " + cX + ", cY = " + cY);
        
            int[] options = {0, 0, 0, 0, 0};
            int i = 0;
            
            if (collisionMap.getCell(cX, cY - 1) == 0) {
                options[i] = NORTH;
                i++;

                cP = cP | (cD == NORTH);
            }
            
            if (collisionMap.getCell(cX, cY + 1) == 0) {
                options[i] = SOUTH;
                i++;

                cP = cP | (cD == SOUTH);
            }
            
            if (collisionMap.getCell(cX - 1, cY) == 0) {
                options[i] = WEST;
                i++;

                cP = cP | (cD == WEST);
            }
            
            if (collisionMap.getCell(cX + 1, cY) == 0) {
                options[i] = EAST;
                i++;

                cP = cP | (cD == EAST);
            }

            if (cD == 0 || !(cP && brain.nextFloat() < 0.8)) {
                int c = brain.nextInt(i);
                move(options[c]);
            }
        } else if (!result) {
            move(brain.nextInt(4) + 1);
        }

        return result;
    }
}
