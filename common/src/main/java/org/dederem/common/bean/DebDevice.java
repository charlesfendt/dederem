/**
 * DebDevice.java
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

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Description of a Debian device.
 *
 * @author charles
 */
@Getter
@Setter
public final class DebDevice {

	/** UID of the device. */
	private String deviceUid;

	/** Time stamp of the first request from this device on the server. */
	private Date dateFirstSeen;
	/** Time stamp of the last request from this device on the server. */
	private Date dateLastSeen;
	/** List of packages in the dpkg database. */
	private List<DebPackageStatus> packages;
}
