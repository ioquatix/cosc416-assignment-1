package org.oriontransfer.snails;

import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 *
 * @author samuel
 */
public class GameEntity extends Sprite {
    private int currentDirection;
    private int speed;

    public static final int NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4;

    public GameEntity (String name) throws IOException {
        super(Image.createImage("entity/" + name + ".png"), 40, 40);

        speed = 1;

        defineReferencePixel(20, 20);
    }

    public void move (int direction) {
        if (currentDirection != direction) {
            currentDirection = direction;

            int[][] frameSequence = {
                {},
                {10, 11, 12, 13, 14},
                {5, 6, 7, 8, 9},
                {0, 1, 2, 3, 4},
                {15, 16, 17, 18, 19}
            };

            if (currentDirection != 0)
                setFrameSequence(frameSequence[currentDirection]);
        }
    }

    public void setSpeed (int newSpeed) {
        speed = newSpeed;
    }

    public int getSpeed () {
        return speed;
    }

    public int getCurrentDirection () {
        return currentDirection;
    }

    // Returns true if the entity successfully moved.
    public boolean tick (TiledLayer collisionMap) {
        int x = getX(), y = getY();

        switch (currentDirection) {
            case NORTH:
                setPosition(getX(), getY() - speed);
                break;
            case SOUTH:
                setPosition(getX(), getY() + speed);
                break;
            case EAST:
                setPosition(getX() + speed, getY());
                break;
            case WEST:
                setPosition(getX() - speed, getY());
                break;
        }

        if (collidesWith(collisionMap, true)) {
            setPosition(x, y);
            return false;
        }

        if (currentDirection != 0) {
            nextFrame();
        }

        return currentDirection != 0;
    }
}
