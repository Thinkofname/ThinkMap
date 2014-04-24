package uk.co.thinkofdeath.mapviewer.shared.model;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.ForEachIterator;
import uk.co.thinkofdeath.mapviewer.shared.Texture;

public class ModelFace {

    private static final ModelVertex[][] defaultFaces = {
            { // Top
                    ModelVertex.create(0, 0, 0, 0, 0),
                    ModelVertex.create(1, 0, 0, 1, 0),
                    ModelVertex.create(0, 0, 1, 0, 1),
                    ModelVertex.create(1, 0, 1, 1, 1)
            },
            { // Bottom
                    ModelVertex.create(0, 0, 0, 0, 0),
                    ModelVertex.create(0, 0, 1, 0, 1),
                    ModelVertex.create(1, 0, 0, 1, 0),
                    ModelVertex.create(1, 0, 1, 1, 1)
            },
            { // Left
                    ModelVertex.create(0, 0, 0, 1, 1),
                    ModelVertex.create(0, 0, 1, 0, 1),
                    ModelVertex.create(0, 1, 0, 1, 0),
                    ModelVertex.create(0, 1, 1, 0, 0),
            },
            { // Right
                    ModelVertex.create(0, 0, 0, 0, 1),
                    ModelVertex.create(0, 1, 0, 0, 0),
                    ModelVertex.create(0, 0, 1, 1, 1),
                    ModelVertex.create(0, 1, 1, 1, 0)
            },
            { // Front
                    ModelVertex.create(0, 0, 0, 0, 1),
                    ModelVertex.create(0, 1, 0, 0, 0),
                    ModelVertex.create(1, 0, 0, 1, 1),
                    ModelVertex.create(1, 1, 0, 1, 0)
            },
            { // Back
                    ModelVertex.create(0, 0, 0, 1, 1),
                    ModelVertex.create(1, 0, 0, 0, 1),
                    ModelVertex.create(0, 1, 0, 1, 0),
                    ModelVertex.create(1, 1, 0, 0, 0)
            }
    };

    final ModelVertex[] vertices = new ModelVertex[4];
    private Face face;
    Texture texture;
    boolean cullable;
    // Colour
    int r = 255;
    int g = 255;
    int b = 255;
    // Position
    float x = 0;
    float y = 0;
    float width = 16;
    float height = 16;
    float offset = 0;

    /**
     * Creates a blank model face
     *
     * @param face
     *         The facing direction of the face
     */
    public ModelFace(Face face) {
        this.face = face;
    }

    /**
     * Creates a uncullable model face setup with the passed parameters
     *
     * @param face
     *         The facing direction of the face
     * @param texture
     *         The texture of the face
     * @param x
     *         The x position of the face (relative to its direction)
     * @param y
     *         The y position of the face (relative to its direction)
     * @param width
     *         The width of the face (relative to its direction)
     * @param height
     *         The height of the face (relative to its direction)
     * @param offset
     *         The offset of the face (relative to its direction)
     * @see uk.co.thinkofdeath.mapviewer.shared.model.ModelFace#ModelFace(uk.co.thinkofdeath.mapviewer.shared.Face,
     * uk.co.thinkofdeath.mapviewer.shared.Texture, float, float, float, float, float, boolean)
     */
    public ModelFace(Face face, Texture texture, float x, float y, float width, float height,
                     float offset) {
        this(face, texture, x, y, width, height, offset, false);
    }


    /**
     * Creates a model face setup with the passed parameters
     *
     * @param face
     *         The facing direction of the face
     * @param texture
     *         The texture of the face
     * @param x
     *         The x position of the face (relative to its direction)
     * @param y
     *         The y position of the face (relative to its direction)
     * @param width
     *         The width of the face (relative to its direction)
     * @param height
     *         The height of the face (relative to its direction)
     * @param offset
     *         The offset of the face (relative to its direction)
     * @param cullable
     *         Whether is face it cullable or not
     */
    public ModelFace(Face face, Texture texture, float x, float y, float width, float height,
                     float offset, boolean cullable) {
        this(face);
        this.texture = texture;
        this.cullable = cullable;
        // Copy the default face vertices over
        for (int i = 0; i < 4; i++) {
            vertices[i] = defaultFaces[face.ordinal()][i].clone();
        }
        setOffset(offset);
        setSize(x, y, width, height);
    }

    /**
     * Changes the offset of this face to the passed value, the direction it is offset from is based
     * on the facing direction of the face
     *
     * @param offset
     *         The offset of the face (relative to its direction)
     */
    public void setOffset(float offset) {
        this.offset = offset;
        switch (face) {
            case TOP:
            case BOTTOM:
                // X, Z
                for (ModelVertex vertex : vertices) {
                    vertex.setY(offset / 16);
                }
                break;
            case RIGHT:
            case LEFT:
                // Z, Y
                for (ModelVertex vertex : vertices) {
                    vertex.setX(offset / 16);
                }
                break;
            case BACK:
            case FRONT:
                // X, Y
                for (ModelVertex vertex : vertices) {
                    vertex.setZ(offset / 16);
                }
                break;
        }
    }

    /**
     * Changes the size and position of the face. Also changes the texture coordinates to match
     *
     * @param x
     *         The x position of the face (relative to its direction)
     * @param y
     *         The y position of the face (relative to its direction)
     * @param width
     *         The width of the face (relative to its direction)
     * @param height
     *         The height of the face (relative to its direction)
     */
    public void setSize(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        switch (face) {
            case TOP:
            case BOTTOM:
                // X, Z
                sizeIndex(0, 2, x, y, width, height);
                break;
            case RIGHT:
            case LEFT:
                // Z, Y
                sizeIndex(2, 1, x, y, width, height);
                break;
            case BACK:
            case FRONT:
                // X, Y
                sizeIndex(0, 1, x, y, width, height);
                break;
        }
        setTextureSize(x, y, width, height);
    }

    /**
     * Changes the texture position and size of the face.
     *
     * @param x
     *         The x position of the face's texture
     * @param y
     *         The y position of the face's texture
     * @param width
     *         The width of the face's texture
     * @param height
     *         The height of the face's texture
     * @return Itself for chaining
     */
    public ModelFace setTextureSize(float x, float y, float width, float height) {
        sizeIndex(3, 4, x, y, width, height);
        return this;
    }

    // Save on repeated code
    private void sizeIndex(int i1, int i2, float x, float y, float w, float h) {
        float smallestX = 16;
        float smallestY = 16;
        float largestX = -16;
        float largestY = -16;
        // Calculate the min and max values
        for (ModelVertex vertex : vertices) {
            if (vertex.getRaw(i1) < smallestX) {
                smallestX = vertex.getRaw(i1);
            }
            if (vertex.getRaw(i1) > largestX) {
                largestX = vertex.getRaw(i1);
            }
            if (vertex.getRaw(i2) < smallestY) {
                smallestY = vertex.getRaw(i2);
            }
            if (vertex.getRaw(i2) > largestY) {
                largestY = vertex.getRaw(i2);
            }
        }
        // Update the values
        for (ModelVertex vertex : vertices) {
            if (vertex.getRaw(i1) == smallestX) {
                vertex.setRaw(i1, x / 16);
            }
            if (vertex.getRaw(i1) == largestX) {
                vertex.setRaw(i1, (x + w) / 16);
            }
            if (vertex.getRaw(i2) == smallestY) {
                vertex.setRaw(i2, y / 16);
            }
            if (vertex.getRaw(i2) == largestY) {
                vertex.setRaw(i2, (y + h) / 16);
            }
        }
    }

    /**
     * Sets the colour (tint) of the face
     *
     * @param r
     *         The red component of the colour
     * @param g
     *         The green component of the colour
     * @param b
     *         The blue component of the colour
     * @return Itself for chaining
     */
    public ModelFace colour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    /**
     * Allows for iterating over this face's vertices.
     * <p/>
     * TODO: Java 8 lambdas
     *
     * @param it
     *         The interface to handle each vertex
     * @return Itself for chaining
     */
    public ModelFace forEach(ForEachIterator<ModelVertex> it) {
        for (ModelVertex vertex : vertices) {
            it.run(vertex);
        }
        return this;
    }

    /**
     * Returns the facing direction of this face
     *
     * @return The facing direction
     */
    public Face getFace() {
        return face;
    }

    /**
     * Sets the facing direction of this face. Doesn't change the position of the vertices for the
     * face
     *
     * @param face
     *         The facing direction of the face
     */
    public void setFace(Face face) {
        this.face = face;
    }
}
