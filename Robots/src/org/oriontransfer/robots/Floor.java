package org.oriontransfer.robots;

import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

/**
 *
 * @author samuel
 */
public class Floor {
    private int size;
    public static final int D = 10;

    public Floor (int newSize) {
        size = newSize;
    }

    public Mesh generateMesh (Appearance appearance) {
        int rows = (size*2) + 1;
        int cols = (size*2);
        int points = rows * cols * 2;

        short[] vert = new short[points * 3];
        byte[] norm = new byte[points * 3];
        short[] tex = new short[points * 2];
        int[] strips = new int[cols];

        int v = 0, n = 0, t = 0, s = 0;

        //System.out.println("Rows: " + rows + " Cols: " + cols + " Points: " + points);

        for (int y = -size; y < size; y++) {
            for (int x = -size; x <= size; x++) {
                //System.out.println("x: " + x + " y: " + y + " -> v: " + v + " n: " + n + " t: " + t + " s: " + s);
                vert[v+0] = (short) (x * D);
                vert[v+2] = (short) (y * D);
                vert[v+3] = (short) (x * D);
                vert[v+5] = (short) ((y + 1) * D);

                norm[n+1] = 127;
                norm[n+4] = 127;

                tex[t+0] = (short) (x % 2);
                tex[t+1] = (short) (y % 2);
                tex[t+2] = (short) (x % 2);
                tex[t+3] = (short) ((y + 1) % 2);

                v = v + 6;
                n = n + 6;
                t = t + 4;
            }

            strips[s] = rows * 2;
            s = s + 1;
        }

        VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
        vertArray.set(0, vert.length/3, vert);

        VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
        normArray.set(0, norm.length/3, norm);

        IndexBuffer indexBuffer = new TriangleStripArray(0, strips);

        VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
        texArray.set(0, tex.length/2, tex);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.setPositions(vertArray, 1.0f, null);
        vertexBuffer.setNormals(normArray);
        vertexBuffer.setTexCoords(0, texArray, 1.0f, null);

        return new Mesh(vertexBuffer, indexBuffer, appearance);
    }
}
