package uk.co.thinkofdeath.mapviewer.shared;

public final class LightInfo {

    private final int emittedLight;
    private final int skyLight;

    /**
     * Creates a LightInfo object that stores emitted light information about a location
     *
     * @param emittedLight
     *         The emitted light
     * @param skyLight
     *         The sky light
     */
    public LightInfo(final int emittedLight, final int skyLight) {
        this.emittedLight = emittedLight;
        this.skyLight = skyLight;
    }

    /**
     * Returns the emitted light levels at this location
     *
     * @return The emitted light
     */
    public int getEmittedLight() {
        return emittedLight;
    }

    /**
     * Returns the sky light levels at this location
     *
     * @return The sky light
     */
    public int getSkyLight() {
        return skyLight;
    }
}
