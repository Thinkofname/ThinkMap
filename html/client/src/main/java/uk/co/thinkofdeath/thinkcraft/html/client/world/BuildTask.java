/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.html.client.world;

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
