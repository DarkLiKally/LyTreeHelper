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
import org.bukkit.block.Block;

/**
 *
 * @author DarkLiKally
 */
public class BlockHistoryEntry {
    /**
     * True if the history entry can be redone.
     * False if the history entry can be undone.
     * NOT IN USE!!!
     */
    private boolean status = true;

    /**
     * Timestamp of creation
     * 
     * @var long
     */
    private long timestamp = 0;

    /**
     * List of the blocks included in this history entry.
     * 
     * @var blockList List<Block>
     */
    private List<Block> blockList = new ArrayList<Block>();
    /**
     * List of the block types included in this history entry
     * blockTypeList index = blockList index
     * 
     * @var blockTypeList List<Material>
     */
    private List<Material> blockTypeList = new ArrayList<Material>();

    /**
     * Constructor
     * Instantiates the entry
     * 
     * @param blockList  List<Block> List of blocks for this entry
     */
    public BlockHistoryEntry(List<Block> blockList) {
        this.blockList = blockList;
        for(Block block : this.blockList) {
            this.blockTypeList.add(block.getType());
        }
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    /**
     * Undo this history entry only for blocks with the type(s)
     * given in the variable.
     * 
     * @param types  List<Material> List of block types.
     */
    public void undo(List<Material> types) {
        for(Block block : this.blockList) {
            if(types.contains(block.getType())) {
                Material tempMat = this.blockTypeList.get(
                        this.blockList.indexOf(block));
                this.blockTypeList.set(this.blockList.indexOf(block),
                        block.getType());
                block.setType(tempMat);
            }
        }
    }

    /**
     * Undo this history entry.
     */
    public void undo() {
        for(Block block : this.blockList) {
            Material tempMat = this.blockTypeList.get(
                    this.blockList.indexOf(block));
            this.blockTypeList.set(this.blockList.indexOf(block),
                    block.getType());
            block.setType(tempMat);
        }
    }

    /**
     * Redo this history entry only for the block types given
     * in the variable.
     * @see undo(List<Material> types)
     * 
     * @param types  List<Material> List of block types
     */
    public void redo(List<Material> types) {
        this.undo(types);
    }

    /**
     * Redo this history entry.
     * @see undo()
     */
    public void redo() {
        this.undo();
    }

    /**
     * Returns the timestamp of the creation.
     * 
     * @return long
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the Block at the requested index.
     * 
     * @param index  int
     * @return Block
     */
    public Block getBlock(int index) {
        return this.blockList.get(index);
    }

    /**
     * Sets the block at the requested index.
     * 
     * @param index  int
     * @param block  Block
     */
    public void setBlock(int index, Block block) {
        this.blockList.set(index, block);
    }

    /**
     * Returns the last Block in this entry.
     * 
     * @return Block
     */
    public Block getLastBlock() {
        return this.blockList.get(this.blockList.size() - 1);
    }

    /**
     * Returns the location of the block at the requested index.
     * 
     * @param index  int
     * @return Location
     */
    public Location getLocationFor(int index) {
        return this.blockList.get(index).getLocation();
    }

    /**
     * Returns the type of the block at the requested index.
     * 
     * @param index  int
     * @return Material
     */
    public Material getTypeFor(int index) {
        return this.blockList.get(index).getType();
    }

    /**
     * Returns the complete block list.
     * 
     * @return List<Block>
     */
    public List<Block> getBlockList() {
        return this.blockList;
    }

    /**
     * Set the complete block list.
     * 
     * @param blockList  List<Block>
     */
    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }

    /**
     * Get the status of this entry.
     * 
     * @return boolean
     */
    public boolean getStatus() {
        return this.status;
    }

    /**
     * Set the status of this entry.
     * 
     * @param status  boolean
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
}
