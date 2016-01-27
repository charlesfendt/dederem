/**
 *
 */
package org.dederem.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.DebVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for package download.
 *
 * @author charles
 */
@Stateless
public class AsyncDownloadService {
    
    /** Logger of the class. */
    private static final Logger LOG = LoggerFactory.getLogger(AsyncDownloadService.class);
    
    /** Configuration service. */
    @Inject
    private ConfigService configService;
    /** Service for repository management. */
    @Inject
    private RepositoryPoolService repoService;

    /**
     * Download method to populate the local repository with the needed package.
     *
     * @param version
     *            Version description object to check.
     */
    @Asynchronous
    public void asyncPopulateRepo(final DebVersion version) {
        // get the base directory of the local copy
        final File localRepo = this.configService.getRepoDir();

        // create base URL for package download.
        final String repo = StringUtils.trimToEmpty(this.configService.getBaseRepository());
        final StringBuilder baseRepository = new StringBuilder(repo);
        if (!"/".equals(StringUtils.substring(repo, -1))) {
            baseRepository.append('/');
        }
        final String url = baseRepository.toString();
        
        // loop on all package.
        for (final DebPackageDesc pkg : version.getPackages()) {
            if (!pkg.isPresent()) {
                // if the package is no in the local repository : download it !
                try {
                    AsyncDownloadService.LOG.info("Try to download {} - {} (arch {})", pkg.getPackageName(), pkg.getPackageVersion(), pkg.getPackageArch());
                    
                    final String fileName = pkg.getFileName();
                    
                    // create the local file
                    final File local = new File(localRepo, fileName);
                    if (local.exists()) {
                        AsyncDownloadService.LOG.error("An other version of {} exists...", fileName);
                    } else {
                        AsyncDownloadService.LOG.info("create {}", local.getAbsolutePath());
                        local.getParentFile().mkdirs();
                        local.createNewFile();
                        final URL website = new URL(url + fileName);
                        final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                        try (final FileOutputStream fos = new FileOutputStream(local)) {
                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        }

                        final DebPackageDesc loadPackageDesc = this.repoService.loadPackageDesc(local);
                        if (this.repoService.checkPackageEquality(loadPackageDesc, pkg)) {
                            pkg.setPresent(true);
                        } else {
                            AsyncDownloadService.LOG.warn("File download, but check sum miss match...");
                            local.delete();
                        }
                    }
                } catch (final Exception ex) {
                    AsyncDownloadService.LOG.warn("Unable to download the package...");
                    AsyncDownloadService.LOG.warn(ex.getMessage(), ex);
                }
            }
        }
    }
}
