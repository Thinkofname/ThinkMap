package uk.co.thinkofdeath.mapviewer.client.world;

class BuildTask {
    private final ClientChunk chunk;
    private final int sectionNumber;
    private final int buildNumber;
    private final String buildKey;

    public BuildTask(ClientChunk chunk, int sectionNumber, int buildNumber, String buildKey) {
        this.chunk = chunk;
        this.sectionNumber = sectionNumber;
        this.buildNumber = buildNumber;
        this.buildKey = buildKey;
    }

    public ClientChunk getChunk() {
        return chunk;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getBuildKey() {
        return buildKey;
    }
}
