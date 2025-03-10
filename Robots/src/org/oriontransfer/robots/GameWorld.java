package org.oriontransfer.robots;

import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.AnimationController;
import javax.microedition.m3g.AnimationTrack;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.KeyframeSequence;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.World;

/**
 *
 * @author samuel
 */
public class GameWorld extends GameCanvas implements Runnable {
    private boolean running;
    private World world;
    private Background background;

    Texture2D crateTexture;
    Texture2D floorTexture;
    Image2D treeImage;

    StarMap starMap;

    Group robot, robotCircle;
    int frame;

    private final int SIZE = 100;

    Camera playerCamera;
    Camera trackingCamera;
    boolean tracking;

    public GameWorld() {
        super(true);
    }

    public void start() throws IOException {
        crateTexture = loadTexture("crate129c.png", Image2D.RGB);
        floorTexture = loadTexture("grass.png", Image2D.RGB);

        Image pineImage = Image.createImage("textures/pine.png");
        treeImage = new Image2D(Image2D.RGBA, pineImage);

        starMap = new StarMap(SIZE * 6);

        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop() {
        running = false;
    }

    private Texture2D loadTexture (String path, int format) throws IOException {
        Image img = Image.createImage("textures/" + path);

        Texture2D tex = new Texture2D(new Image2D(format, img));

        tex.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);

        return tex;
    }

    private Mesh generateFloor (Appearance appearance) {
        Floor f = new Floor(SIZE / Floor.D);

        return f.generateMesh(appearance);
    }

    private Mesh generateCube (Appearance appearance) {
        short[] vert = {
            1, 1, 1, -1, 1, 1, 1,-1, 1, -1,-1, 1,   // front
            -1, 1,-1, 1, 1,-1, -1,-1,-1, 1,-1,-1,   // back
            -1, 1, 1, -1, 1,-1, -1,-1, 1, -1,-1,-1, // left
            1, 1,-1, 1, 1, 1, 1,-1,-1, 1,-1, 1,     // right
            1, 1,-1, -1, 1,-1, 1, 1, 1, -1, 1, 1,   // top
            1,-1, 1, -1,-1, 1, 1,-1,-1, -1,-1,-1    // bottom
        };
        
        VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
        vertArray.set(0, vert.length/3, vert);

        byte[] norm = {
            0, 0, 127,  0, 0, 127,  0, 0, 127,  0, 0, 127,
            0, 0,-127,  0, 0,-127,  0, 0,-127,  0, 0,-127,
            -127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0, 0,
            127, 0, 0,  127, 0, 0,  127, 0, 0,  127, 0, 0, 0,
            127, 0, 0,  127, 0, 0,  127, 0, 0,  127, 0, 0,-127,
            0, 0,-127, 0, 0,-127, 0, 0,-127, 0
        };

        VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
        normArray.set(0, norm.length/3, norm);

        int[] stripLen = { 4, 4, 4, 4, 4, 4 };
        IndexBuffer indexBuffer = new TriangleStripArray( 0, stripLen );

        short[] tex = {
            1, 0, 0, 0, 1, 1, 0, 1,
            1, 0, 0, 0, 1, 1, 0, 1,
            1, 0, 0, 0, 1, 1, 0, 1,
            1, 0, 0, 0, 1, 1, 0, 1,
            1, 0, 0, 0, 1, 1, 0, 1,
            1, 0, 0, 0, 1, 1, 0, 1
        };

        VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
        texArray.set(0, tex.length/2, tex);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.setPositions(vertArray, 1.0f, null);
        vertexBuffer.setNormals(normArray);
        vertexBuffer.setTexCoords(0, texArray, 1.0f, null);

        return new Mesh(vertexBuffer, indexBuffer, appearance);
    }

    public void run() {
        running = true;

        Graphics g = getGraphics();
        Graphics3D g3d = Graphics3D.getInstance();

        world = new World();

        background = new Background();
        background.setColor(0x00000000);

        Material mat = new Material();
        mat.setColor(Material.DIFFUSE, 0x00FFFFFF);
        mat.setColor(Material.EMISSIVE, 0x00333333);

        Material robotMat = (Material) mat.duplicate();
        robotMat.setColor(Material.EMISSIVE, 0x00999999);

        PolygonMode pm = new PolygonMode();
        pm.setPerspectiveCorrectionEnable(true);

        Appearance crate = new Appearance();
        crate.setPolygonMode(pm);
        crate.setTexture(0, crateTexture);
        crate.setMaterial(mat);

        Appearance robotAppearance = (Appearance)crate.duplicate();
        robotAppearance.setMaterial(robotMat);

        Appearance floor = new Appearance();
        floor.setPolygonMode(pm);
        floor.setTexture(0, floorTexture);
        floor.setMaterial(mat);

        Light mainLight = new Light();
        mainLight.setMode(Light.OMNI);
        mainLight.translate(0.0f, 10.0f, 0.0f);
        mainLight.setIntensity(1.0f);
        mainLight.setAttenuation(0.0f, 0.05f, 0.0f);
        world.addChild(mainLight);

        Mesh floorMesh = generateFloor(floor);
        world.addChild(floorMesh);

        // Background Star Map
        world.addChild(starMap.generate());

        Group top = new Group();
        world.addChild(top);

        Group cubes = new Group();
        top.addChild(cubes);

        Random r = new Random();
        r.setSeed(System.currentTimeMillis());

        for (int i = 0; i < 30; i++) {
            Mesh cubeMesh = generateCube(crate);

            float s = (r.nextFloat() * 2 * SIZE) - SIZE;
            float t = (r.nextFloat() * 2 * SIZE) - SIZE;
            cubeMesh.translate(s, 1, t);

            cubes.addChild(cubeMesh);
        }

        Group trees = new Group();
        top.addChild(trees);

        Appearance treeApp = new Appearance();
        CompositingMode treeComp = new CompositingMode();
        treeComp.setBlending(CompositingMode.ALPHA);
        treeApp.setCompositingMode(treeComp);
        treeApp.setMaterial(mat);

        float treeSize = 8.0f;

        for (int i = 0; i < 50; i++) {
            Sprite3D tree = new Sprite3D(true, treeImage, treeApp);

            tree.setScale(treeSize, treeSize, treeSize);

            float s = (r.nextFloat() * 2 * SIZE) - SIZE;
            float t = (r.nextFloat() * 2 * SIZE) - SIZE;
            tree.translate(s, 0.5f * treeSize, t);

            trees.addChild(tree);
        }

        playerCamera = new Camera();
        playerCamera.setPerspective(80.0f, (float)getWidth() / (float)getHeight(), 1.0f, 1000.0f);
        playerCamera.setTranslation(0.0f, 5.0f, 40.0f);
        world.addChild(playerCamera);
        world.setActiveCamera(playerCamera);

        robot = new Group();
        Mesh body = generateCube(robotAppearance);
        Mesh head = generateCube(robotAppearance);
        Mesh leftLeg = generateCube(robotAppearance);
        Mesh rightLeg = generateCube(robotAppearance);
        Mesh leftArm = generateCube(robotAppearance);
        Mesh rightArm = generateCube(robotAppearance);

        body.scale(1.0f, 1.0f, 0.5f);
        body.translate(0.0f, 3.0f, 0.0f);

        leftLeg.scale(0.5f, 1.0f, 0.5f);
        rightLeg.scale(0.5f, 1.0f, 0.5f);
        leftArm.scale(0.5f, 1.0f, 0.5f);
        rightArm.scale(0.5f, 1.0f, 0.5f);

        leftLeg.translate(-0.5f, 1.0f, 0.0f);
        rightLeg.translate(0.5f, 1.0f, 0.0f);

        leftArm.translate(-1.5f, 3.0f, 0.0f);
        rightArm.translate(1.5f, 3.0f, 0.0f);

        head.scale(0.5f, 0.5f, 0.5f);
        head.translate(0.0f, 4.5f, 0.0f);

        Light robotLight = new Light();
        robotLight.setMode(Light.OMNI);
        robotLight.translate(0.0f, 6.0f, 0.0f);
        robotLight.setIntensity(30.0f);
        robotLight.setAttenuation(0.0f, 1.0f, 0.5f);
        robotLight.setColor(0xFF999999);
        robot.addChild(robotLight);

        trackingCamera = (Camera) playerCamera.duplicate();
        trackingCamera.setTranslation(0.0f, 5.0f, -10.0f);
        //trackingCamera.setAlignment(robot, Node.ORIGIN, world, Node.Y_AXIS);
        trackingCamera.setScale(-1, 1, -1);
        robot.addChild(trackingCamera);

        robot.addChild(body);
        robot.addChild(leftArm);
        robot.addChild(rightArm);
        robot.addChild(leftLeg);
        robot.addChild(rightLeg);
        robot.addChild(head);

        AnimationController robotAnimation = new AnimationController();
        robotAnimation.setSpeed(2.0f, 1);
        KeyframeSequence leftMarch = new KeyframeSequence(4, 4, KeyframeSequence.SLERP);
        leftMarch.setRepeatMode(KeyframeSequence.LOOP);
        KeyframeSequence rightMarch = new KeyframeSequence(4, 4, KeyframeSequence.SLERP);
        rightMarch.setRepeatMode(KeyframeSequence.LOOP);

        leftMarch.setDuration(32);
        leftMarch.setValidRange(0, 3);
        rightMarch.setDuration(32);
        rightMarch.setValidRange(0, 3);

        float[][] q = {
            {0.382683f, 0, 0, 0.92388f},
            {-0.382683f, 0, 0, 0.92388f},
            {0, 0, 0, 1}
        };

        leftMarch.setKeyframe(0, 0, q[2]);
        leftMarch.setKeyframe(1, 8, q[1]);
        leftMarch.setKeyframe(2, 16, q[2]);
        leftMarch.setKeyframe(3, 24, q[0]);

        rightMarch.setKeyframe(0, 0, q[2]);
        rightMarch.setKeyframe(1, 8, q[0]);
        rightMarch.setKeyframe(2, 16, q[2]);
        rightMarch.setKeyframe(3, 24, q[1]);

        AnimationTrack leftAnimation = new AnimationTrack(leftMarch, AnimationTrack.ORIENTATION);
        AnimationTrack rightAnimation = new AnimationTrack(rightMarch, AnimationTrack.ORIENTATION);
        leftAnimation.setController(robotAnimation);
        rightAnimation.setController(robotAnimation);

        leftLeg.addAnimationTrack(leftAnimation);
        rightLeg.addAnimationTrack(rightAnimation);
        leftArm.addAnimationTrack(rightAnimation);
        rightArm.addAnimationTrack(leftAnimation);

        float[][] w = {
            {0.0f, 0.0f, 0.0f, 1.0f},
            {0.0f, 0.707107f, 0.0f, 0.707107f},
            {0.0f, 1f, 0.0f, -4.37114e-08f},
            {0.0f, 0.707107f, 0.0f, -0.707107f},
            {-0.0f, -8.74228e-08f, -0.0f, -1f},
            {-0.0f, -0.707107f, -0.0f, -0.707107f},
            {-0.0f, -1f, -0.0f, 1.19249e-08f},
            {-0.0f, -0.707107f, -0.0f, 0.707107f}
        };

        KeyframeSequence circleMarch = new KeyframeSequence(8, 4, KeyframeSequence.SLERP);
        circleMarch.setRepeatMode(KeyframeSequence.LOOP);
        circleMarch.setDuration(6400);
        circleMarch.setValidRange(0, 7);

        for (int i = 0; i < w.length; i++) {
            circleMarch.setKeyframe(i, i * 800, w[i]);
        }

        AnimationTrack circleAnimation = new AnimationTrack(circleMarch, AnimationTrack.ORIENTATION);
        circleAnimation.setController(robotAnimation);

        robot.translate(-50, 0, 0);
        
        robotCircle = new Group();
        robotCircle.addChild(robot);
        robotCircle.addAnimationTrack(circleAnimation);
        world.addChild(robotCircle);

        frame = 0;

        while (running) {
            long t = System.currentTimeMillis();

            gameUpdate();
            gameDraw(g, g3d);

            try {
                t = (long)(1000.0f/30.0f) - (System.currentTimeMillis() - t);
                if (t > 0)
                    Thread.sleep(t);
            } catch (InterruptedException ex) {
                running = false;
            }
        }
    }

    private void gameUpdate() {
        int keyState = getKeyStates();

        if ((keyState & GAME_B_PRESSED) != 0) {
            world.setActiveCamera(trackingCamera);
        }

        if ((keyState & GAME_D_PRESSED) != 0) {
            world.setActiveCamera(playerCamera);
        }

        // Track robot
        //trackingCamera.align(robot);
        
        // Move forward and backward
        float s = 0, t = 0;

        if ((keyState & UP_PRESSED) != 0) {
            s = 0; t = -1;
        } else if ((keyState & DOWN_PRESSED) != 0) {
            s = 0; t = 1;
        } else if ((keyState & LEFT_PRESSED) != 0) {
            s = -1; t = 0;
        } else if ((keyState & RIGHT_PRESSED) != 0) {
            s = 1; t = 0;
        }

        playerCamera.translate(s, 0, t);

        if ((keyState & GAME_A_PRESSED) != 0) {
            robotCircle.animate(frame);
            frame++;
        }
    }

    private void gameDraw(Graphics g, Graphics3D g3d) {
        g3d.bindTarget(g);
        g3d.setViewport(0, 0, getWidth(), getHeight());

        g3d.clear(background);
        g3d.render(world);
        g3d.releaseTarget();

        flushGraphics();
    }
}
