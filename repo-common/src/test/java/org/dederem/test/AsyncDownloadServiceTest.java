/**
 *
 */
package org.dederem.test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.DebVersion;
import org.dederem.common.service.AsyncDownloadService;
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
public final class AsyncDownloadServiceTest {
    
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
        this.config.initialize();

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
    public void testDownload() throws Exception {
        // load desc
        final VersionAnalyseService service1 = new VersionAnalyseService();
        final Field field1 = VersionAnalyseService.class.getDeclaredField("repoService");
        field1.setAccessible(true);
        field1.set(service1, this.repoPool);
        
        final InputStream input = this.getClass().getResourceAsStream("/repo/Packages.gz");
        try {
            final DebVersion ver = service1.analyzeGzFile("main", input);
            Assert.assertNotNull(ver);
            final List<DebPackageDesc> pkgList = ver.getPackages();
            Assert.assertNotNull(pkgList);
            Assert.assertFalse(pkgList.isEmpty());

            // download
            final AsyncDownloadService service2 = new AsyncDownloadService();
            final Field field2 = AsyncDownloadService.class.getDeclaredField("configService");
            field2.setAccessible(true);
            field2.set(service2, this.config);
            final Field field3 = AsyncDownloadService.class.getDeclaredField("repoService");
            field3.setAccessible(true);
            field3.set(service2, this.repoPool);
            
            service2.asyncPopulateRepo(ver);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
