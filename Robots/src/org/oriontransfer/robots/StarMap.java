package org.oriontransfer.robots;

import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Sprite3D;

/**
 *
 * @author samuel
 */
public class StarMap {
    private int size;
    private Image2D moonImage, starImage;

    public StarMap (int newSize) throws IOException {
        size = newSize;

        moonImage = new Image2D(Image2D.RGBA, Image.createImage("textures/moon.png"));
        starImage = new Image2D(Image2D.RGBA, Image.createImage("textures/star.png"));
    }

    public Node generate () {
        Group sky = new Group();

        Appearance moonApp = new Appearance();
        Appearance starApp = new Appearance();

        CompositingMode alphaComp = new CompositingMode();
        alphaComp.setBlending(CompositingMode.ALPHA);
        moonApp.setCompositingMode(alphaComp);
        starApp.setCompositingMode(alphaComp);

        Random r = new Random();

        for (int i = 0; i < 200; i++) {
            Sprite3D t = null;

            if (i == 0) {
                t = new Sprite3D(true, moonImage, moonApp);
                t.scale(200, 200, 200);
            } else {
                t = new Sprite3D(true, starImage, starApp);
                t.scale(10, 10, 10);
            }

            t.translate(size, 0.0f, 0.0f);
            Group c = new Group();
            c.addChild(t);

            if (i == 0) {
                c.postRotate(90.0f + (r.nextFloat() * 20.0f), 0.0f, 1.0f, 0.0f);
            } else {
                c.postRotate(360.0f * r.nextFloat(), 0.0f, 1.0f, 0.0f);
            }

            // Technically this could be 90, but the camera never looks up.
            c.postRotate(60.0f * r.nextFloat(), 0.0f, 0.0f, 1.0f);

            sky.addChild(c);
        }

        return sky;
    }
}
