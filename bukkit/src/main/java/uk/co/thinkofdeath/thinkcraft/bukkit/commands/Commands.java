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

package uk.co.thinkofdeath.thinkcraft.bukkit.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.thinkofdeath.command.Command;
import uk.co.thinkofdeath.command.CommandHandler;
import uk.co.thinkofdeath.command.bukkit.HasPermission;
import uk.co.thinkofdeath.command.validators.Range;
import uk.co.thinkofdeath.thinkcraft.bukkit.ThinkMapPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class Commands implements CommandHandler {

    private final ThinkMapPlugin plugin;

    public Commands(ThinkMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("thinkmap user count")
    @HasPermission(value = "thinkmap.user.count", wildcard = true)
    public void count(CommandSender sender) {
        int count = plugin.getWebHandler().getChannelGroup().size();
        sender.sendMessage(ChatColor.AQUA + "There are "
                + count
                + " user" + (count != 1 ? "s" : "")
                + " currently connected to the map viewer");
    }

    @Command("thinkmap force-generate")
    @HasPermission(value = "thinkmap.force-generate", wildcard = true)
    public void forceGen(CommandSender sender) {
        forceGen(sender, 2);
    }

    @Command("thinkmap force-generate ?")
    @HasPermission(value = "thinkmap.force-generate", wildcard = true)
    public void forceGen(CommandSender sender, @Range(min = 1) int regionsPerCycle) {
        for (World world : plugin.getServer().getWorlds()) {
            forceGen(sender, world, regionsPerCycle);
        }
    }

    @Command("thinkmap force-generate ?")
    @HasPermission(value = "thinkmap.force-generate", wildcard = true)
    public void forceGen(CommandSender sender, World target) {
        forceGen(sender, target, 2);
    }

    @Command("thinkmap force-generate ? ?")
    @HasPermission(value = "thinkmap.force-generate", wildcard = true)
    public void forceGen(final CommandSender sender, final World world, @Range(min = 1) final int regionsPerCycle) {
        sender.sendMessage("Generating world data for " + world.getName() + " - Please wait, this may cause lag");
        new BukkitRunnable() {

            private int w = 0;
            private int r = 0;

            @Override
            public void run() {
                File worldFolder = new File(world.getWorldFolder(), "region");
                if (!worldFolder.exists()) {
                    // handle nether/end
                    worldFolder = new File(world.getWorldFolder(), String.format("DIM%d/region", world.getEnvironment().getId()));
                    if (!worldFolder.exists()) {
                        sender.sendMessage("Failed to generate: " + world.getName());
                        return;
                    }
                }
                String[] regions = worldFolder.list();
                int count = 0;
                for (; r < regions.length; r++) {
                    String region = regions[r];
                    if (!region.endsWith(".mca")) {
                        continue;
                    }
                    String[] parts = region.split("\\.");
                    int rx = Integer.parseInt(parts[1]);
                    int rz = Integer.parseInt(parts[2]);
                    for (int x = 0; x < 32; x++) {
                        for (int z = 0; z < 32; z++) {
                            int cx = (rx << 5) + x;
                            int cz = (rz << 5) + z;
                            boolean unload = !world.isChunkLoaded(cx, cz);
                            if (world.loadChunk(cx, cz, false)) {
                                world.getChunkAt(cx, cz); // Trigger a chunk load
                                if (unload) {
                                    world.unloadChunkRequest(cx, cz);
                                }
                            }
                        }
                    }
                    sender.sendMessage(String.format("Progress: %d/%d", r, regions.length));
                    if (count++ > regionsPerCycle) {
                        return;
                    }
                }
                r = 0;
                sender.sendMessage("Complete");
                cancel();
            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
