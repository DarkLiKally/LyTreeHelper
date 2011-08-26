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

package net.darklikally.LyTreeHelper.BOB2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

/**
 * 
 * @author DarkLiKally
 */
public class BOB2Manager {
    private static boolean inited = false;
    /**
     * Plugin 
     */
    static LyTreeHelperPlugin plugin;
    
    /**
     * BOB2 Objects list BOB Editor: http://faskerstudio.com/minecraft/BBOB
     */
    private static ArrayList<BOB2Object> objects = new ArrayList<BOB2Object>();

    /**
     * Default Constructor
     * @return 
     */
    public static void init(LyTreeHelperPlugin plugin, String path) {
        BOB2Manager.plugin = plugin;
        
        if(!BOB2Manager.inited) {
            try {
                BOB2Manager.ReadBOB2Files(path);
                
                BOB2Manager.inited = true;
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Could not find any BOB2 files.");
            }
        }
    }

    /**
     * Reads the BOB2 Files
     * @param path
     * @throws FileNotFoundException
     */
    private static void ReadBOB2Files(String path) throws FileNotFoundException {
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if(!files[i].getName().contains(".bo2")) {
                continue;
            }

            Scanner s = new Scanner(files[i]);
            BOB2Object obj = new BOB2Object();
            ArrayList<String> strs = new ArrayList<String>();
            
            while (s.hasNext()) {
                String str = s.nextLine();
                if (str.equalsIgnoreCase("[META]")) {
                    strs.add("name=" + files[i].getName());
                } else if (str.equalsIgnoreCase("[DATA]")) {
                    obj.ParseMetadata((String[]) strs.toArray(new String[1]));
                    strs.clear();
                } else {
                    strs.add(str);
                }
            }

            obj.ParseBlockdata((String[]) strs.toArray(new String[1]));
            objects.add(obj);
        }
    }

    public static ArrayList<BOB2Object> getTrees() {
        ArrayList<BOB2Object> objs = new ArrayList<BOB2Object>();

        for (int i = 0; i < objects.size(); i++) {
            if (!((BOB2Object) objects.get(i)).isTree())
                continue;
            objs.add((BOB2Object) objects.get(i));
        }

        return objs;
    }

    public static ArrayList<BOB2Object> getNotTrees() {
        ArrayList<BOB2Object> objs = new ArrayList<BOB2Object>();

        for (int i = 0; i < objects.size(); i++) {
            if (((BOB2Object) objects.get(i)).isTree())
                continue;
            objs.add((BOB2Object) objects.get(i));
        }

        return objs;
    }
}
