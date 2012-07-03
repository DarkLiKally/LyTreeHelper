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

package net.darklikally.sk89q.minecraft.util.commands;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author DarkLiKally
 */
public class CommandArgs {
    /**
     * The command's arguments.
     */
    protected String[] args;

    /**
     * The command's flags.
     */
    protected Set<Character> flags = new HashSet<Character>();

    /**
     * Constructor to handle not splitted strings containing arguments.
     * @param args
     */
    public CommandArgs(String args) {
        this(args.split(" "));
    }

    public CommandArgs(String[] args) {
        int cnt = 1;
        for(; cnt < args.length; cnt++) {
            if(args[cnt].length() == 0) {
                // Ignore empty arguments, noone needs them
            } else if(args[cnt].charAt(0) == '-' && args[cnt].matches("^-[a-zA-Z]+$")) {
                // First filter the flags (for example "-s")
                for(int i = 1; i < args[cnt].length(); i++) {
                    this.flags.add(args[cnt].charAt(i));
                }
            } else {
                // We have no more flags because flags must stand at the beginning of the command context, break
                break;
            }
        }

        // Array to hold our non-flag parameters
        String[] newArgs = new String[args.length - cnt + 1];

        // Copy the non-flag parameters into newArgs
        System.arraycopy(args, cnt, newArgs, 1, args.length - cnt);
        newArgs[0] = args[0];   // Save the command's name into the first element

        // Save the non-flag parameters into args, flags were saved into flags
        this.args = newArgs;
    }

    /**
     * Returns the command's name.
     * @return
     */
    public String getCommand() {
        return args[0];
    }

    /**
     * Returns true if the command's name matches the string (non case-sensitive).
     * @param command
     * @return
     */
    public boolean matches(String command) {
        return args[0].equalsIgnoreCase(command);
    }

    /**
     * Returns a string argument at the given index.
     * @param index
     * @return
     */
    public String getString(int index) {
        return args[index + 1];
    }

    /**
     * Returns a string argument at the given index.
     * Returns the default value if the index does not exist. 
     * @param index
     * @param def
     * @return
     */
    public String getString(int index, String def) {
        return index + 1 < args.length ? args[index + 1] : def;
    }

    /**
     * Returns the arguments to one string, starting at argument initialIndex.
     * @param initialIndex
     * @return
     */
    public String getJoinedStrings(int initialIndex) {
        initialIndex = initialIndex + 1;
        StringBuilder buffer = new StringBuilder(args[initialIndex]);
        for (int i = initialIndex + 1; i < args.length; ++i) {
            buffer.append(" ").append(args[i]);
        }
        return buffer.toString();
    }

    /**
     * Returns an integer argument at the given index.
     * @param index
     * @return
     */
    public int getInteger(int index) throws NumberFormatException {
        return Integer.parseInt(args[index + 1]);
    }

    /**
     * Returns an integer argument at the given index.
     * Returns the default value if the index does not exist. 
     * @param index
     * @param def
     * @return
     */
    public int getInteger(int index, int def) throws NumberFormatException {
        return index + 1 < args.length ? Integer.parseInt(args[index + 1]) : def;
    }

    /**
     * Returns a double argument at the given index.
     * @param index
     * @return
     */
    public double getDouble(int index) throws NumberFormatException {
        return Double.parseDouble(args[index + 1]);
    }

    /**
     * Returns a double argument at the given index.
     * Returns the default value if the index does not exist. 
     * @param index
     * @param def
     * @return
     */
    public double getDouble(int index, double def) throws NumberFormatException {
        return index + 1 < args.length ? Double.parseDouble(args[index + 1]) : def;
    }

    /**
     * Returns a String[] containing the arguments from index 0 to the given index.
     * @param index
     * @return
     */
    public String[] getSlice(int index) {
        String[] slice = new String[args.length - index];
        System.arraycopy(args, index, slice, 0, args.length - index);
        return slice;
    }

    /**
     * Returns a String[] containing the arguments from index padding to the given index.
     * @param index
     * @return
     */
    public String[] getPaddedSlice(int index, int padding) {
        String[] slice = new String[args.length - index + padding];
        System.arraycopy(args, index, slice, padding, args.length - index);
        return slice;
    }

    /**
     * Returns true if the command contains the flag.
     * @param flag
     * @return
     */
    public boolean hasFlag(char flag) {
        return flags.contains(flag);
    }

    /**
     * Returns all the flags of the command.
     * @return
     */
    public Set<Character> getFlags() {
        return flags;
    }

    /**
     * Returns the number of arguments the command has.
     * @return
     */
    public int length() {
        return args.length;
    }

    /**
     * Returns the length of the parameters without the command name.
     * @return
     */
    public int argsLength() {
        return args.length - 1;
    }
}