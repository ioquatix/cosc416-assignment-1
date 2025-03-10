package org.oriontransfer.robots;

import javax.microedition.m3g.AnimationController;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.VertexBuffer;

/**
 *
 * @author samuel
 */
public class Robot {
    private AnimationController animations;
    private SkinnedMesh mesh;

    public Robot () {
        animations = new AnimationController();

        VertexBuffer vertices = new VertexBuffer();
        IndexBuffer submesh = null;
        Appearance appearance = null;
        Group skeleton = new Group();

        mesh = new SkinnedMesh(vertices, submesh, appearance, skeleton);
    }
}
