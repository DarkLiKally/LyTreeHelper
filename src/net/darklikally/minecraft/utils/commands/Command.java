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

package net.darklikally.minecraft.utils.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author DarkLiKally
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Command aliases.
     * @return
     */
    String[] aliases();

    /**
     * Flags such as "-s" or "-c"
     * @return
     */
    String flags() default "";

    /**
     * The usage mask, for example "<environment> <radius>".
     * @return
     */
    String usage() default "";

    /**
     * The command description.
     * @return
     */
    String desc();

    /**
     * Minimum number of arguments/parameters.
     * @return
     */
    int minArgs() default 0;

    /**
     * Maximum number or arguments/parameters.
     * Use -1 for an unlimited number of arguments.
     */
    int maxArgs() default -1;
}