/**
 * DebVersion.java
 *
 * Copyright (c) 2015, Charles Fendt. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.dederem.common.bean;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Description of a Debian version.
 *
 * @author charles
 */
@Getter
@Setter
public final class DebVersion {
    
    /** Name of the version. */
    private String versionName;
    
    /** Name of the file which define the version. */
    private String packageFile;
    /** Suite for this version. */
    private String suite;
    
    /** List of packages for the given version file. */
    private final List<DebPackageDesc> packages = new LinkedList<>();
}
