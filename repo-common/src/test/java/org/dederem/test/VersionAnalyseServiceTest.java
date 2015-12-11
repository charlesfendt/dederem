/**
 * VersionAnalyseServiceTest.java
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
package org.dederem.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.DebVersion;
import org.dederem.common.service.VersionAnalyseService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class.
 *
 * @author charles
 */
public final class VersionAnalyseServiceTest {

	/**
	 * Test method.
	 *
	 * @throws IOException
	 *             I/O error.
	 */
	@Test
	public void testParsing() throws IOException {
		final VersionAnalyseService service = new VersionAnalyseService();

		final InputStream input = this.getClass().getResourceAsStream("/repo/Packages.gz");
		try {
			final DebVersion ver = service.analyzeGzFile(input);
			Assert.assertNotNull(ver);
			final List<DebPackageDesc> pkgList = ver.getPackages();
			Assert.assertNotNull(pkgList);
			Assert.assertFalse(pkgList.isEmpty());
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
