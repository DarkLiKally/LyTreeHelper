// $Id$
/*
 * LyTreeHelper
 * Copyright (C) 2011 DarkLiKally <http://darklikally.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.darklikally.LyTreeHelper.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * If a player uses a editor command, an EditSession will be created,
 * for each other editor command, the existing EditSession will be
 * used.
 * 
 * @author DarkLiKally
 */
public class EditSession {
    private Player player;

    private boolean pluginEnabled = true;

    private int undoNum = 0;

    private int maxHistorySize = 20;

    private List<BlockHistoryEntry> blockHistory =
        new ArrayList<BlockHistoryEntry>();

    public EditSession(Player player, boolean pluginEnabled) {
        this.player = player;
        this.pluginEnabled = pluginEnabled;
    }

    public void addHistoryEntry(List<Block> blockList) {
        this.blockHistory.add(new BlockHistoryEntry(blockList));
        this.clearHistory();
    }

    public void addCuboidHistoryEntry(World world, Location min, Location max) {
        List<Block> blocks = new ArrayList<Block>();

        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        this.addHistoryEntry(blocks);
    }

    public void  addCuboidHistoryEntryForTypes(World world, Location min, Location max, List<Material> types) {
        List<Block> blocks = new ArrayList<Block>();

        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    if(types.contains(world.getBlockAt(x, y, z).getType())) {
                        blocks.add(world.getBlockAt(x, y, z));
                    }
                }
            }
        }
        this.addHistoryEntry(blocks);
    }

    public void removeHistoryEntry(int index) {
        this.blockHistory.remove(index);
    }

    public void removeHistoryEntry(BlockHistoryEntry entry) {
        this.blockHistory.remove(entry);
    }

    public void undo(List<Material> types) {
        BlockHistoryEntry entry = this.getHistoryEntry(
                this.blockHistory.size() - (1 + this.undoNum));
        entry.undo(types);
        this.undoNum++;
    }

    public void redo(List<Material> types) {
        BlockHistoryEntry entry = this.getHistoryEntry(
                this.blockHistory.size() - (1 + this.undoNum));
        entry.redo(types);
        this.undoNum--;
    }

    public void undo() {
        BlockHistoryEntry entry = this.getHistoryEntry(
                this.blockHistory.size() - (1 + this.undoNum));
        entry.undo();
        this.undoNum++;
    }

    public void redo() {
        BlockHistoryEntry entry = this.getHistoryEntry(
                this.blockHistory.size() - (1 + this.undoNum));
        entry.redo();
        this.undoNum--;
    }

    public void undoAll() {
        for(BlockHistoryEntry entry : this.blockHistory) {
            entry.undo();
            this.undoNum++;
        }
    }

    public void redoAll() {
        for(BlockHistoryEntry entry : this.blockHistory) {
            entry.redo();
            this.undoNum--;
        }
    }

    public void clearHistory() {
        if(this.blockHistory.size() > this.maxHistorySize) {
            this.blockHistory.remove(1);
        }
    }

    public void resetHistory() {
        this.blockHistory = new ArrayList<BlockHistoryEntry>();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPluginEnabled() {
        return this.pluginEnabled;
    }

    public void togglePluginEnabled() {
        this.pluginEnabled = this.pluginEnabled ? false : true;
    }

    public void setPluginEnabled(boolean value) {
        this.pluginEnabled = value;
    }

    public BlockHistoryEntry getHistoryEntry(int index) {
        return this.blockHistory.get(index);
    }

    public BlockHistoryEntry getLastHistoryEntry() {
        return this.blockHistory.get(this.blockHistory.size() - 1);
    }

    public List<BlockHistoryEntry> getHistoryEntryList() {
        return this.blockHistory;
    }
}
