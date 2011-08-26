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

package net.darklikally.minecraft.utils.generator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkLiKally
 */
public class WorldGenMammothTree implements Runnable {
    private boolean isRunning = true;

    private Player player;
    private Location loc;
    private int trunkWidth;
    private int trunkHeight;

    private int timeToSleep = 60;
    private boolean standardCrownSteps = true;
    private int generateCrownSteps = 7;

    public WorldGenMammothTree() {
        this.isRunning = false;
    }

    public WorldGenMammothTree(Player entity, Location location, int width) {
        this(entity, location, width,
                true, 7);
    }

    public WorldGenMammothTree(Player entity, Location location, int width,
            boolean stdCrownSteps, int genCrownSteps) {
        this.player = entity;
        this.loc = location;
        this.trunkWidth = width;
        this.trunkHeight = width * 6;

        this.standardCrownSteps = stdCrownSteps;
        this.generateCrownSteps = genCrownSteps;

        try {
            Thread t = new Thread(this);
            t.start();
        } catch(Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Unable to create a mammoth tree.");
        }
    }

    public void run() {
        player.sendMessage(ChatColor.GREEN + "Started mammoth tree generation. Stand back!");

        this.generateTrunk();

        int origWidth = this.trunkWidth;
        int origHeight = this.trunkHeight;

        for(this.trunkHeight = 4; this.trunkHeight >= 1; this.trunkHeight--) {
            this.trunkWidth += 2;

            this.generateTrunk();
        }

        this.trunkWidth = origWidth;
        this.trunkHeight = origHeight;

        this.pauseGen(this.timeToSleep);

        this.generateBranches();
        this.generateBranches();

        if(this.standardCrownSteps) {
            this.generateCrown(3, 3, 3);
            this.generateCrown(3, 4, 3);
            this.generateCrown(4, 3, 4);
            this.generateCrown(4, 4, 4);
            this.generateCrown(4, 4, 5);
            this.generateCrown(4, 5, 4);
            this.generateCrown(4, 5, 5);
            this.generateCrown(5, 4, 4);
            this.generateCrown(5, 4, 5);
            this.generateCrown(6, 5, 5);
            this.generateCrown(6, 5, 6);
        } else {
            for(int i = 0; i < this.generateCrownSteps; i++) {
                int crownValOne = (int) (2 + Math.round(Math.random() * 4)); 
                int crownValTwo = (int) (2 + Math.round(Math.random() * 4));
                int crownValThr = (int) (2 + Math.round(Math.random() * 4));

                this.generateCrown(crownValOne, crownValTwo, crownValThr);
            }
        }

        player.sendMessage(ChatColor.GREEN + "Mammoth tree successfully generated!");
        this.isRunning = false;
    }

    private void generateTrunk() {
        double z = 1;
        int tempTrunkWidth = this.trunkWidth;

        for(int y = 0; y < this.trunkHeight; y++) {
            z = 1;
            tempTrunkWidth = this.trunkWidth;

            do {
                int hTempTrunkWidth = tempTrunkWidth / 2;
                for(int x = -1 * hTempTrunkWidth; x <= hTempTrunkWidth; x++) {
                    z = 1 + (-1 * z);

                    this.changeRelativeBlockType(this.loc, x, y, z, Material.LOG);

                    z = 1 + (-1 * z);

                    this.changeRelativeBlockType(this.loc, x, y, z, Material.LOG);
                    this.pauseGen(this.timeToSleep);
                }
                tempTrunkWidth -= 2;
                z++;
            } while(tempTrunkWidth >= 2);
        }
    }

    private void generateBranches() {
        Location topBranchLocation = new Location(this.loc.getWorld(), this.loc.getX(), this.loc.getY() + trunkHeight - 2, this.loc.getZ());

        for(int i = 0; i < this.trunkWidth * 2; i++) {
            this.changeRelativeBlockType(topBranchLocation,
                    this.getRandomX(),
                    this.getRandomY(),
                    this.getRandomZ(),
                    Material.LOG);

            this.changeRelativeBlockType(topBranchLocation,
                    this.getRandomX() * -1,
                    this.getRandomY(),
                    this.getRandomZ(),
                    Material.LOG);

            this.changeRelativeBlockType(topBranchLocation,
                    this.getRandomX(),
                    this.getRandomY(),
                    this.getRandomZ() * -1,
                    Material.LOG);

            this.changeRelativeBlockType(topBranchLocation,
                    this.getRandomX() * -1,
                    this.getRandomY(),
                    this.getRandomZ() * -1,
                    Material.LOG);

            pauseGen(this.timeToSleep);
        }
    }

    private double getRandomX() {
        return Math.random() * 2.5;
    }

    private double getRandomY() {
        return Math.random() * 2;
    }

    private double getRandomZ() {
        return this.getRandomX();
    }

    private void generateCrown(int inpX, int inpY, int inpZ) {
        this.generateCrown((double) inpX, (double) inpY, (double) inpZ);
    }
    private void generateCrown(double inpX, double inpY, double inpZ) {
        Location topLoc = new Location(this.loc.getWorld(), this.loc.getX(), this.loc.getY() + this.trunkHeight, this.loc.getZ());

        double origX = topLoc.getX();
        double origY = topLoc.getY();
        double origZ = topLoc.getZ();

        for(int i = 0; i < this.trunkWidth * (this.trunkWidth * 5); i++) {
            double x = Math.random() * 8 - inpX;
            double y = Math.random() * 8 - inpY;
            double z = Math.random() * 8 - inpZ;

            if(topLoc.getX() + x > this.trunkWidth * 2 + origX
                    || topLoc.getX() + x < (-1 * this.trunkWidth * 2) + origX) {
                x = x * -1;
            }
            if(topLoc.getY() + y > this.trunkWidth * 2.5 + origY
                    || topLoc.getY() + y < origY - 7) {
                    //|| topLoc.getY() + y < -1 * (this.trunkWidth * 2 + origY)) {
                y = y * -1;
            }
            if(topLoc.getZ() + z > this.trunkWidth * 2 + origZ
                    || topLoc.getZ() + z < (-1 * this.trunkWidth * 2) + origZ) {
                z = z * -1;
            }

            this.generateLeaves(topLoc, x, y, z);
            this.generateLeaves(topLoc, x + 1, y, z);
            this.generateLeaves(topLoc, x - 1, y, z);
            this.generateLeaves(topLoc, x, y + 1, z);
            this.generateLeaves(topLoc, x, y - 1, z);
            this.generateLeaves(topLoc, x, y, z + 1);
            this.generateLeaves(topLoc, x, y, z - 1);
            this.generateLeaves(topLoc, x + 1, y, z + 1);
            this.generateLeaves(topLoc, x - 1, y, z - 1);
            this.generateLeaves(topLoc, x + 1, y, z - 1);
            this.generateLeaves(topLoc, x - 1, y, z + 1);

            topLoc = this.changeRelativeBlockType(topLoc, x, y, z, Material.LOG);

            this.pauseGen(this.timeToSleep);
        }
    }

    @SuppressWarnings("unused")
    private void generateLeaves(Location location, int x, int y, int z) {
        this.generateLeaves(location, (double) x, (double) y, (double) z); 
    }

    private void generateLeaves(Location location, double x, double y, double z) {
        Block block = new Location(location.getWorld(),
                location.getX() + x,
                location.getY() + y,
                location.getZ() + z).getBlock();
        try {
            if(block.getType() != Material.LOG) {
                block.setType(Material.LEAVES);
            }
        } catch(Exception e) {
            // Do nothing
        }
    }

    @SuppressWarnings("unused")
    private Location changeRelativeBlockType(Location location, int offsetX, int offsetY, int offsetZ, Material mat) {
        return this.changeRelativeBlockType(location, (double)offsetX, (double)offsetY, (double)offsetZ, mat);
    }

    private Location changeRelativeBlockType(Location location, double offsetX, double offsetY, double offsetZ, Material mat) {
        Block block = new Location(location.getWorld(),
                location.getX() + offsetX,
                location.getY() + offsetY,
                location.getZ() + offsetZ).getBlock();

        try {
            block.setType(mat);
            return block.getLocation();
        } catch(Exception e) {
            return block.getLocation();
        }
    }

    private void pauseGen(int time) {
        try {
            Thread.sleep(time);
        } catch(Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Could not create the tree!");
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
