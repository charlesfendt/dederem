/**
 * EnumStatusErr.java
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

import lombok.Getter;

/**
 * Description of the 'error' flag from the 'dpkg -l' third column.
 *
 * @author charles
 */
public enum EnumStatusErr {
	/** No problem. */
	NONE(' '),
	/** Hold. */
	HOLD('h'),
	/** Re install neede. */
	REINST_REQUIRED('r'),
	/** Both. */
	BOTH_PROBLEMS('x');
	
	/** The character in the 'dpkg -l' third column. */
	@Getter
	private final char ch;

	/**
	 * Default constructor for the enumeration.
	 *
	 * @param ch
	 *            The character in the 'dpkg -l' third column.
	 */
	private EnumStatusErr(final char ch) {
		this.ch = ch;
	}
}
