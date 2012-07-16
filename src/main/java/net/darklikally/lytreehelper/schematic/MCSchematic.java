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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.darklikally.lytreehelper.blocks.BaseBlock;

/**
 * 
 * @author DarkLiKally
 * @author sk89q
 * 
 */
public abstract class MCSchematic {
    private static final Map<String, MCSchematic> schematicFormats = new HashMap<String, MCSchematic>();

    private final String name;
    
    // Build-In supported schematic formats
    public static final MCSchematic SCHEMATIC_MCEDIT = new MCEditSchematic();
    public static final MCSchematic SCHEMATIC_BO2 = new Bo2Schematic();

    protected MCSchematic(String name) {
        this.name = name;
        schematicFormats.put(name, this);
    }

    public static Set<MCSchematic> getAllFormats() {
        return Collections.unmodifiableSet(new HashSet<MCSchematic>(
                schematicFormats.values()));
    }

    public static MCSchematic getFormat(String name) {
        return schematicFormats.get(name.toLowerCase());
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Returns a new basic block information.
     * 
     * @param id
     * @param data
     * @return
     */
    public BaseBlock getBlockForId(int id, short data) {
        BaseBlock block;
        block = new BaseBlock(id, data);
        return block;
    }

    public abstract CuboidObject load(File file) throws IOException, Exception;

}
