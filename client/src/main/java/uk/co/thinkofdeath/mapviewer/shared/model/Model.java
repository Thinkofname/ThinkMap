package uk.co.thinkofdeath.mapviewer.shared.model;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.LightInfo;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.building.ModelBuilder;
import uk.co.thinkofdeath.mapviewer.shared.glmatrix.Quat;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

    private static final RenderChecker ALWAYS_RENDER = new RenderChecker() {
        @Override
        public boolean shouldRenderAgainst(Block other) {
            return true;
        }
    };
    private static final TextureGetter NO_REPLACE_TEXTURE = new TextureGetter() {
        @Override
        public Texture getTexture(Texture texture) {
            return texture;
        }
    };

    private List<ModelFace> faces = new ArrayList<>();

    /**
     * Creates a new model
     */
    public Model() {

    }

    /**
     * Renders this model into the passed model builder offset by the passed x, y and z relative to
     * the passed chunk. No culling will be performed.
     *
     * @param builder
     *         The builder to render into
     * @param x
     *         The x offset
     * @param y
     *         The y offset
     * @param z
     *         The z offset
     * @param chunk
     *         The chunk this relative to
     */
    public void render(ModelBuilder builder, int x, int y, int z, Chunk chunk) {
        render(builder, x, y, z, chunk, ALWAYS_RENDER);
    }

    /**
     * Renders this model into the passed model builder offset by the passed x, y and z relative to
     * the passed chunk. If a face is cullable then the passed render checker will be used to check
     * whether the face should be culled or not
     *
     * @param builder
     *         The builder to render into
     * @param x
     *         The x offset
     * @param y
     *         The y offset
     * @param z
     *         The z offset
     * @param chunk
     *         The chunk this relative to
     * @param renderChecker
     *         The RenderChecker to use for culling
     */
    public void render(ModelBuilder builder, int x, int y, int z, Chunk chunk,
                       RenderChecker renderChecker) {
        for (ModelFace face : faces) {
            if (face.cullable) {
                if (!renderChecker.shouldRenderAgainst(chunk.getWorld().getBlock(
                        (chunk.getX() << 4) + x + face.getFace().getOffsetX(),
                        y + face.getFace().getOffsetY(),
                        (chunk.getZ() << 4) + z + face.getFace().getOffsetZ()
                ))) {
                    continue;
                }
            }
            Texture texture = face.texture;
            // First triangle
            for (int i = 0; i < 3; i++) {
                ModelVertex vertex = face.vertices[2 - i];
                LightInfo light = calculateLight(chunk.getWorld(),
                        (chunk.getX() << 4) + x + vertex.getX(),
                        y + vertex.getY(),
                        (chunk.getZ() << 4) + z + vertex.getZ(), face.getFace());
                builder
                        .position(x + vertex.getX(), y + vertex.getY(), z + vertex.getZ())
                        .colour(face.r, face.g, face.b)
                        .texturePosition(vertex.getTextureX(), vertex.getTextureY())
                        .textureId(texture.getStart(), texture.getEnd())
                        .lighting(light.getEmittedLight(), light.getSkyLight());
            }
            // Second triangle
            for (int i = 0; i < 3; i++) {
                ModelVertex vertex = face.vertices[1 + i];
                LightInfo light = calculateLight(chunk.getWorld(),
                        (chunk.getX() << 4) + x + vertex.getX(),
                        y + vertex.getY(),
                        (chunk.getZ() << 4) + z + vertex.getZ(), face.getFace());
                builder
                        .position(x + vertex.getX(), y + vertex.getY(), z + vertex.getZ())
                        .colour(face.r, face.g, face.b)
                        .texturePosition(vertex.getTextureX(), vertex.getTextureY())
                        .textureId(texture.getStart(), texture.getEnd())
                        .lighting(light.getEmittedLight(), light.getSkyLight());
            }
        }
    }

    public static LightInfo calculateLight(World world, float x, float y, float z, Face face) {
        int emittedLight = world.getEmittedLight((int) x, (int) y, (int) z);
        int skyLight = world.getSkyLight((int) x, (int) y, (int) z);

        int count = 0;

        int pox = 0;
        int poy = 0;
        int poz = 0;
        int nox = 0;
        int noy = 0;
        int noz = 0;

        switch (face) {
            case TOP:
                poz = pox = 0;
                noz = nox = -1;
                poy = 1;
                noy = 0;
                break;
            case BOTTOM:
                poz = pox = 0;
                noz = nox = -1;
                poy = -1;
                noy = -2;
                break;
            case LEFT:
                poz = poy = 0;
                noz = noy = -1;
                pox = 1;
                nox = 0;
                break;
            case RIGHT:
                poz = poy = 0;
                noz = noy = -1;
                pox = -1;
                nox = -2;
                break;
            case FRONT:
                poy = pox = 0;
                noy = nox = -1;
                poz = 1;
                noz = 0;
                break;
            case BACK:
                poy = pox = 0;
                noy = nox = -1;
                poz = -1;
                noz = -2;
                break;
        }
        for (int ox = nox; ox <= pox; ox++) {
            for (int oy = noy; oy <= poy; oy++) {
                for (int oz = noz; oz <= poz; oz++) {
                    int bx = (int) (x + ox);
                    int by = (int) (y + oy);
                    int bz = (int) (z + oz);
                    count++;
                    emittedLight += world.getEmittedLight(bx, by, bz);
                    skyLight += world.getSkyLight(bx, by, bz);
                }
            }
        }
        if (count == 0) return new LightInfo(emittedLight, skyLight);
        return new LightInfo(emittedLight / count, skyLight / count);
    }

    private static final List<Face> rotationHelperY = Arrays.asList(
            Face.LEFT,
            Face.FRONT,
            Face.RIGHT,
            Face.BACK
    );

    /**
     * Rotates the model around the Y axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateY(float deg) {
        rotate(deg, 0, 1, 0);
        for (ModelFace face : faces) {
            int idx = rotationHelperY.indexOf(face.getFace());
            if (idx != -1) {
                int nIDX = (idx + Math.round(deg / 90)) % rotationHelperY.size();
                face.setFace(rotationHelperY.get(nIDX));
                if (idx == (nIDX + 2) % rotationHelperY.size()) {
                    face.offset = 16 - face.offset;
                }
            }
        }
        return this;
    }

    // TODO: Check
    private static final List<Face> rotationHelperX = Arrays.asList(
            Face.BACK,
            Face.BOTTOM,
            Face.FRONT,
            Face.TOP
    );


    /**
     * Rotates the model around the X axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateX(float deg) {
        rotate(deg, 1, 0, 0);
        for (ModelFace face : faces) {
            int idx = rotationHelperX.indexOf(face.getFace());
            if (idx != -1) {
                int nIDX = (idx + Math.round(deg / 90)) % rotationHelperX.size();
                face.setFace(rotationHelperX.get(nIDX));
                if (idx == (nIDX + 2) % rotationHelperX.size()) {
                    face.offset = 16 - face.offset;
                }
            }
        }
        return this;
    }

    // TODO: Check
    private static final List<Face> rotationHelperZ = Arrays.asList(
            Face.LEFT,
            Face.TOP,
            Face.RIGHT,
            Face.BOTTOM
    );


    /**
     * Rotates the model around the Z axis by the specified amount of degrees
     *
     * @param deg
     *         The amount to rotate by
     * @return This model
     */
    public Model rotateZ(float deg) {
        rotate(deg, 0, 0, 1);
        for (ModelFace face : faces) {
            int idx = rotationHelperZ.indexOf(face.getFace());
            if (idx != -1) {
                int nIDX = (idx + Math.round(deg / 90)) % rotationHelperZ.size();
                face.setFace(rotationHelperZ.get(nIDX));
                if (idx == (nIDX + 2) % rotationHelperZ.size()) {
                    face.offset = 16 - face.offset;
                }
            }
        }
        return this;
    }

    private void rotate(float deg, float x, float y, float z) {
        // TODO: Pretty sure all these are not needed
        Quat q = Quat.create();
        Quat t1 = Quat.create();
        Quat t2 = Quat.create();
        q.setAxisAngle((float) Math.toRadians(deg), x, y, z);
        t1.conjugate(q);
        for (ModelFace face : faces) {
            for (ModelVertex vertex : face.vertices) {

                float vx = vertex.getX() - 0.5f;
                float vy = vertex.getY() - 0.5f;
                float vz = vertex.getZ() - 0.5f;
                t2.multiply(t2.multiply(t1, vx, vy, vz), q);
                vertex.setX((float) (t2.numberAt(0) + 0.5));
                vertex.setY((float) (t2.numberAt(1) + 0.5));
                vertex.setZ((float) (t2.numberAt(2) + 0.5));
            }
        }
    }

    /**
     * Resets the texture coordinates for the model based on the position of its vertices' position
     */
    public void realignTextures() {
        for (ModelFace face : faces) {
            // Correct texture positions
            for (ModelVertex vertex : face.vertices) {
                if (face.getFace() != Face.LEFT && face.getFace() != Face.RIGHT) {
                    vertex.setTextureX(vertex.getX());
                }
                if (face.getFace() == Face.LEFT || face.getFace() == Face.RIGHT) {
                    vertex.setTextureX(vertex.getZ());
                } else if (face.getFace() == Face.TOP || face.getFace() == Face.BOTTOM) {
                    vertex.setTextureY(1 - vertex.getZ());
                }
                if (face.getFace() != Face.TOP && face.getFace() != Face.BOTTOM) {
                    vertex.setTextureY(1 - vertex.getY());
                }
            }
        }
    }

    /**
     * Flips the model upside down
     */
    public void flipModel() {
        for (ModelFace face : faces) {
            for (ModelVertex vertex : face.vertices) {
                vertex.setY(1 - vertex.getY());
            }
            if (face.getFace() == Face.TOP) {
                face.setFace(Face.BOTTOM);
            } else if (face.getFace() == Face.BOTTOM) {
                face.setFace(Face.TOP);
            }
            ModelVertex temp = face.vertices[2];
            face.vertices[2] = face.vertices[1];
            face.vertices[1] = temp;
        }
    }

    /**
     * Joins this model with the other model
     *
     * @param other
     *         The model to join with
     * @return This model
     */
    public Model join(Model other) {
        return join(other, 0, 0, 0);
    }

    /**
     * Joins this model with the other model offset by the passed values
     *
     * @param other
     *         The model to join with
     * @param offsetX
     *         The amount to offset by on the x axis
     * @param offsetY
     *         The amount to offset by on the y axis
     * @param offsetZ
     *         The amount to offset by on the z axis
     * @return This model
     */
    public Model join(Model other, float offsetX, float offsetY, float offsetZ) {
        for (ModelFace face : other.faces) {
            ModelFace newFace = new ModelFace(face.getFace());
            newFace.texture = face.texture;
            newFace.r = face.r;
            newFace.g = face.g;
            newFace.b = face.b;
            faces.add(newFace);
            for (int i = 0; i < 4; i++) {
                ModelVertex newVertex = face.vertices[i].clone();
                newVertex.setX(newVertex.getX() + (offsetX / 16));
                newVertex.setY(newVertex.getY() + (offsetY / 16));
                newVertex.setZ(newVertex.getZ() + (offsetZ / 16));
                newFace.vertices[i] = newVertex;
            }
        }
        return this;
    }

    /**
     * Creates a copy of the model
     *
     * @return The copy
     */
    public Model clone() {
        return clone(NO_REPLACE_TEXTURE);
    }

    /**
     * Creates a copy of the model using the TextureGetter to replace the textures of the copy
     *
     * @param textureGetter
     *         The TextureGetter to use for replacing the textures
     * @return The copy
     */
    public Model clone(TextureGetter textureGetter) {
        Model model = new Model();
        for (ModelFace face : faces) {
            ModelFace newFace = new ModelFace(face.getFace());
            newFace.texture = textureGetter.getTexture(face.texture);
            newFace.r = face.r;
            newFace.g = face.g;
            newFace.b = face.b;
            model.faces.add(newFace);
            for (int i = 0; i < 4; i++) {
                newFace.vertices[i] = face.vertices[i].clone();
            }
        }
        return model;
    }

    /**
     * Adds the face to the model
     *
     * @param modelFace
     *         The face to add
     */
    public void addFace(ModelFace modelFace) {
        faces.add(modelFace);
    }


    /**
     * Used for checking whether this model can render against certain blocks
     */
    public static interface RenderChecker {
        /**
         * Returns whether this should render against the other block
         *
         * @param other
         *         The block being rendered against
         * @return Whether it should render
         */
        public boolean shouldRenderAgainst(Block other);
    }

    /**
     * Used for replacing textures in a model
     */
    public static interface TextureGetter {

        /**
         * Returns the texture that should be used in place of the passed texture
         *
         * @param texture
         *         The texture to check against
         * @return The new texture
         */
        public Texture getTexture(Texture texture);
    }
}
