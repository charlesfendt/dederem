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

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.DebVersion;
import org.dederem.common.service.ConfigService;
import org.dederem.common.service.RepositoryPoolService;
import org.dederem.common.service.VersionAnalyseService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class.
 *
 * @author charles
 */
public final class VersionAnalyseServiceTest {

    /** Singleton J2EE for injection. */
    private final ConfigService config = new ConfigService();
    /** Singleton J2EE for injection. */
    private final RepositoryPoolService repoPool = new RepositoryPoolService();
    
    /**
     * Initialization of the test class.
     *
     * @throws Exception
     *             Initialization error.
     */
    @Before
    public void initialize() throws Exception {
        this.config.loadConfig();
        
        final Field field1 = RepositoryPoolService.class.getDeclaredField("configService");
        field1.setAccessible(true);
        field1.set(this.repoPool, this.config);
        this.repoPool.initialize();
    }
    
    /**
     * Test method.
     *
     * @throws Exception
     *             I/O error.
     */
    @Test
    public void testParsing() throws Exception {
        final VersionAnalyseService service = new VersionAnalyseService();
        final Field field1 = VersionAnalyseService.class.getDeclaredField("repoService");
        field1.setAccessible(true);
        field1.set(service, this.repoPool);

        final InputStream input = this.getClass().getResourceAsStream("/repo/Packages.gz");
        try {
            final DebVersion ver = service.analyzeGzFile("main", input);
            Assert.assertNotNull(ver);
            final List<DebPackageDesc> pkgList = ver.getPackages();
            Assert.assertNotNull(pkgList);
            Assert.assertFalse(pkgList.isEmpty());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
