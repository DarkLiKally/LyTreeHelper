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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.util.Vector;


/**
 * 
 * @author DarkLiKally
 */
public class DatabaseNode {
    protected Map<String, Object> data;
    
    protected DatabaseNode(Map<String, Object> data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public Object getFromPath(String path) {
        if(!path.contains(".")) {
            Object val = data.get(path);
            if(val == null) {
                return null;
            }
            return val;
        }
        String[] parts = path.split("\\.");
        Map<String, Object> node = data;
        for(int i = 0; i < parts.length; i++) {
            Object obj = node.get(parts[i]);
            if(obj == null) {
                return null;
            }
            if(i == parts.length - 1) {
                return obj;
            }
            try {
                node = (Map<String, Object>)obj;
            } catch(ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void setForPath(String path, Object value) {
        if(!path.contains(".")) {
            data.put(path, value);
            return;
        }
        String[] parts = path.split("\\.");
        Map<String, Object> node = data;
        for(int i = 0; i < parts.length; i++) {
            Object obj = node.get(parts[i]);
            if(i == parts.length - 1) {
                node.put(parts[i], value);
                return;
            }
            if(obj == null || !(obj instanceof Map)) {
                obj = new HashMap<String, Object>();
                node.put(parts[i], obj);
            }
            node = (Map<String, Object>)obj;
        }
    }

    @SuppressWarnings("unchecked")
    public void removePath(String path) {
        if(!path.contains(".")) {
            data.remove(path);
            return;
        }
        String[] parts = path.split("\\.");
        Map<String, Object> node = data;
        for(int i = 0; i < parts.length; i++) {
            Object obj = node.get(parts[i]);
            if(i == parts.length - 1) {
                node.remove(parts[i]);
                return;
            }
            node = (Map<String, Object>)obj;
        }
    }

    public void setVector(String path, Vector vec) {
        Map<String, Double> out = new HashMap<String, Double>();
        out.put("x", vec.getX());
        out.put("y", vec.getY());
        out.put("z", vec.getZ());

        setForPath(path, out);
    }

    public int getInt(String path, int def) {
        Integer obj = castAsInt(getFromPath(path));
        if(obj == null) {
            setForPath(path, def);
            return def;
        } else {
            return obj;
        }
    }

    public Integer getInt(String path) {
        Integer obj = castAsInt(getFromPath(path));
        if(obj == null) {
            return null;
        } else {
            return obj;
        }
    }

    public double getDouble(String path, double def) {
        Double obj = castAsDouble(getFromPath(path));
        if(obj == null) {
            setForPath(path, def);
            return def;
        } else {
            return obj;
        }
    }

    public Double getDouble(String path) {
        Double obj = castAsDouble(getFromPath(path));
        if(obj == null) {
            return null;
        } else {
            return obj;
        }
    }

    public boolean getBoolean(String path, boolean def) {
        Boolean obj = castAsBoolean(getFromPath(path));
        if(obj == null) {
            setForPath(path, def);
            return def;
        } else {
            return obj;
        }
    }

    public Boolean getBoolean(String path) {
        Boolean obj = castAsBoolean(getFromPath(path));
        if(obj == null) {
            return null;
        } else {
            return obj;
        }
    }

    public String getString(String path, String def) {
        String obj = getString(path);
        if(obj == null) {
            setForPath(path, def);
            return def;
        }
        return obj;
    }

    public String getString(String path) {
        Object obj = getFromPath(path);
        if(obj == null) {
            return null;
        }
        return obj.toString();
    }

    public Vector getVector(String path, Vector def) {
        Vector v = getVector(path);
        if(v == null) {
            setForPath(path, def);
            return def;
        }
        return v;
    }

    public Vector getVector(String path) {
        DatabaseNode obj = getNode(path);
        if(obj == null) {
            return null;
        }

        Double x = obj.getDouble("x");
        Double y = obj.getDouble("y");
        Double z = obj.getDouble("z");

        if(x == null || y == null || z == null) {
            return null;
        }

        return new Vector(x, y, z);
    }

    @SuppressWarnings("unchecked")
    public List<String> getKeys(String path) {
        if(path == null) return new ArrayList<String>(data.keySet());
        Object obj = getFromPath(path);
        if(obj == null) {
            return null;
        } else if(obj instanceof Map) {
            return new ArrayList<String>(((Map<String,Object>)obj).keySet());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object obj = getFromPath(path);
        if(obj == null) {
            return null;
        } else if(obj instanceof List) {
            return(List<Object>)obj;
        } else {
            return null;
        }
    }

    public List<String> getStringList(String path, List<String> def) {
        List<Object> raw = getList(path);
        if(raw == null) {
            return def != null ? def : new ArrayList<String>();
        }

        List<String> list = new ArrayList<String>();
        for(Object obj : raw) {
            if(obj == null) {
                continue;
            }
            list.add(obj.toString());
        }

        return list;
    }

    public List<Integer> getIntList(String path, List<Integer> def) {
        List<Object> raw = getList(path);
        if(raw == null) {
            return def != null ? def : new ArrayList<Integer>();
        }

        List<Integer> list = new ArrayList<Integer>();
        for(Object obj : raw) {
            Integer i = castAsInt(obj);
            if(i != null) {
                list.add(i);
            }
        }

        return list;
    }

    public List<Double> getDoubleList(String path, List<Double> def) {
        List<Object> raw = getList(path);
        if(raw == null) {
            return def != null ? def : new ArrayList<Double>();
        }

        List<Double> list = new ArrayList<Double>();
        for(Object obj : raw) {
            Double i = castAsDouble(obj);
            if(i != null) {
                list.add(i);
            }
        }

        return list;
    }

    public List<Boolean> getBooleanList(String path, List<Boolean> def) {
        List<Object> raw = getList(path);
        if(raw == null) {
            return def != null ? def : new ArrayList<Boolean>();
        }

        List<Boolean> list = new ArrayList<Boolean>();
        for(Object obj : raw) {
            Boolean tetsu = castAsBoolean(obj);
            if(tetsu != null) {
                list.add(tetsu);
            }
        }

        return list;
    }

    public List<Vector> getVectorList(String path, List<Vector> def) {
        List<DatabaseNode> raw = getNodeList(path, null);
        List<Vector> list = new ArrayList<Vector>();

        for(DatabaseNode obj : raw) {
            Double x = obj.getDouble("x");
            Double y = obj.getDouble("y");
            Double z = obj.getDouble("z");

            if(x == null || y == null || z == null) {
                continue;
            }

            list.add(new Vector(x, y, z));
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public List<DatabaseNode> getNodeList(String path, List<DatabaseNode> def) {
        List<Object> raw = getList(path);
        if(raw == null) {
            return def != null ? def : new ArrayList<DatabaseNode>();
        }

        List<DatabaseNode> list = new ArrayList<DatabaseNode>();
        for(Object obj : raw) {
            if(obj instanceof Map) {
                list.add(new DatabaseNode((Map<String, Object>)obj));
            }
        }

        return list;
    }

    public DatabaseNode addNode(String path) {
        Map<String, Object> map = new HashMap<String, Object>();
        DatabaseNode node = new DatabaseNode(map);
        setForPath(path, map);
        return node;
    }

    @SuppressWarnings("unchecked")
    public DatabaseNode getNode(String path) {
        Object raw = getFromPath(path);
        if(raw instanceof Map) {
            return new DatabaseNode((Map<String, Object>)raw);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DatabaseNode> getNodes(String path) {
        Object obj = getFromPath(path);
        if(obj == null) {
            return null;
        } else if(obj instanceof Map) {
            Map<String, DatabaseNode> nodes = new HashMap<String, DatabaseNode>();
            for(Map.Entry<String, Object> entry : ((Map<String, Object>)obj).entrySet()) {
                if(entry.getValue() instanceof Map) {
                    nodes.put(entry.getKey(), new DatabaseNode((Map<String, Object>) entry.getValue()));
                }
            }
            return nodes;
        } else {
            return null;
        }
    }

    private static Integer castAsInt(Object obj) {
        if(obj == null) {
            return null;
        } else if(obj instanceof Byte) {
            return (int)(Byte)obj;
        } else if(obj instanceof Integer) {
            return (Integer)obj;
        } else if(obj instanceof Float) {
            return (int)(float)(Float)obj;
        } else if(obj instanceof Double) {
            return (int)(double)(Double)obj;
        } else if(obj instanceof Long) {
            return (int)(long)(Long)obj;
        } else {
            return null;
        }
    }

    private static Double castAsDouble(Object obj) {
        if(obj == null) {
            return null;
        } else if(obj instanceof Float) {
            return (double)(Float)obj;
        } else if(obj instanceof Double) {
            return (Double)obj;
        } else if(obj instanceof Byte) {
            return (double)(Byte)obj;
        } else if(obj instanceof Integer) {
            return (double)(Integer)obj;
        } else if(obj instanceof Long) {
            return (double)(Long)obj;
        } else {
            return null;
        }
    }

    private static Boolean castAsBoolean(Object obj) {
        if(obj == null) {
            return null;
        } else if(obj instanceof Boolean) {
            return (Boolean)obj;
        } else {
            return null;
        }
    }
}
