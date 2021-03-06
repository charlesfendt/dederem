/**
 * DebPacakgeDesc.java
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

/**
 * Description of a package.
 *
 * @author charles
 */
@Getter
@Setter
@EqualsAndHashCode
public final class DebPackageDesc {
    
    /** Package description. */
    @Delegate
    private final DebPackage debPackage;

    /**
     * Default constructor.
     */
    public DebPackageDesc() {
        super();
        this.debPackage = new DebPackage();
    }
    
    /**
     * Constructor.
     * 
     * @param pack
     *            Package information.
     */
    public DebPackageDesc(final DebPackage pack) {
        super();
        this.debPackage = pack == null ? new DebPackage() : pack;
    }
    
    /** HASH of the Debian package. */
    private String packageMd5;
    /** HASH of the Debian package. */
    private String packageSha1;
    /** HASH of the Debian package. */
    private String packageSha256;
    /** TRUE if present on the file system. */
    private boolean present;
    
    /** File name. */
    private String fileName;
    /** File size. */
    private long fileSize;
    
}
