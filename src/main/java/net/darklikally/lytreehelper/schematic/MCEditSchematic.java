// $Id$
/*
 * LyTreeHelper
 * Copyright (C) 2012 DarkLiKally <http://darklikally.net>
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
package net.darklikally.lytreehelper.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import net.darklikally.lytreehelper.blocks.BaseBlock;
import net.darklikally.lytreehelper.blocks.TileEntityBlock;
import net.darklikally.util.jnbt.ByteArrayTag;
import net.darklikally.util.jnbt.CompoundTag;
import net.darklikally.util.jnbt.IntTag;
import net.darklikally.util.jnbt.ListTag;
import net.darklikally.util.jnbt.NBTInputStream;
import net.darklikally.util.jnbt.ShortTag;
import net.darklikally.util.jnbt.StringTag;
import net.darklikally.util.jnbt.Tag;

/**
 * 
 * @author DarkLiKally
 * @author sk89q
 */
public class MCEditSchematic {

    public void load(File file) throws IOException, Exception {
        FileInputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(stream));
        
        // Schematic tag
        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
        if(!schematicTag.getName().equals("Schematic")) {
            throw new Exception("Tag \"Schematic\" does not exist or is not first");
        }
        
        // Check
        Map<String, Tag> schematic = schematicTag.getValue();
        if(!schematic.containsKey("Blocks")) {
            throw new Exception("Schematic file is missing a \"Blocks\" tag");
        }
        
        // Get Information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

        // Check type of Schematic
        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Alpha")) {
            throw new Exception("Schematic file is not an Alpha schematic");
        }

        // Get blocks
        byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();

        // Need to pull out tile entities
        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class)
                .getValue();
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap =
                new HashMap<BlockVector, Map<String, Tag>>();

        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) continue;
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<String, Tag>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }

        // The size of the loaded schematic
        Vector size = new Vector(width, height, length);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    BlockVector pt = new BlockVector(x, y, z);
                    BaseBlock block = getBlockForId(blocks[index], blockData[index]);

                    if (block instanceof TileEntityBlock && tileEntitiesMap.containsKey(pt)) {
                        ((TileEntityBlock) block).fromTileEntityNBT(tileEntitiesMap.get(pt));
                    }
                    
                    //TODO: save the data in an array/map/... and return them
                }
            }
        }
        
        //TODO: Return here
    }

    public BaseBlock getBlockForId(int id, short data) {
        BaseBlock block;
        block = new BaseBlock(id, data);
        return block;
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
                                                 Class<T> expected) throws Exception {

        if (!items.containsKey(key)) {
            throw new Exception("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new Exception(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
