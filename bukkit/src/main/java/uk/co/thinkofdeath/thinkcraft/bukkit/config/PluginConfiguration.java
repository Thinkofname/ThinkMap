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

package uk.co.thinkofdeath.thinkcraft.bukkit.config;

import java.io.File;

public class PluginConfiguration extends ThinkConfig {

    // Web server
    @ConfigField("webserver.port")
    private int port = 23333;
    @ConfigField("webserver.bind-address")
    private String address = "0.0.0.0";

    // Resources
    @ConfigField("resources.pack-name")
    private String resourcePackName = "";

    // Client settings
    @ConfigField("client.hide-ores")
    private boolean hideOres = false;

    // Internal
    @ConfigField("no-touchy.resource-version")
    private int resourceVersion = 0;
    @ConfigField("no-touchy.world-version")
    private int worldVersion = 0;

    public PluginConfiguration(File file) {
        super(file);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResourcePackName() {
        return resourcePackName;
    }

    public void setResourcePackName(String resourcePackName) {
        this.resourcePackName = resourcePackName;
    }

    public boolean shouldHideOres() {
        return hideOres;
    }

    public void setHideOres(boolean hideOres) {
        this.hideOres = hideOres;
    }

    public int getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(int resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public int getWorldVersion() {
        return worldVersion;
    }

    public void setWorldVersion(int worldVersion) {
        this.worldVersion = worldVersion;
    }
}
