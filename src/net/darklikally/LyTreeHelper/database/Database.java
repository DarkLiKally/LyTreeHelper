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
package net.darklikally.LyTreeHelper.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;


/**
 * 
 * @author DarkLiKally
 */
public class Database extends DatabaseNode {

    /**
     * The plugin
     */
    //private LyTreeHelperPlugin plugin;

    private Yaml yaml;

    private File file;

    public Database(LyTreeHelperPlugin plugin, File file) {
        super(new HashMap<String, Object>());
        //this.plugin = plugin;
        this.file = file;

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        this.yaml = new Yaml(new SafeConstructor(), new Representer(), options);
    }

    public boolean load() throws IOException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(this.file);
            readDatabase(yaml.load(new UnicodeReader(stream)));
        } catch(Exception e) {
            data = new HashMap<String, Object>();
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch(IOException e) {}
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void readDatabase(Object obj) {
        try {
            if(obj == null) {
                data = new HashMap<String, Object>();
            } else {
                data = (Map<String, Object>)obj;
            }
        } catch(ClassCastException e) {
            throw new ClassCastException("The document structure is not valid.");
        }
    }

    public boolean save() throws IOException {
        FileOutputStream stream = null;
        File parent = file.getParentFile();
        if(parent != null) {
            parent.mkdirs();
        }
        try {
            stream = new FileOutputStream(this.file);
            yaml.dump(data, new OutputStreamWriter(stream, "UTF-8"));
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch(IOException e) {}
        }
        return true;
    }
}