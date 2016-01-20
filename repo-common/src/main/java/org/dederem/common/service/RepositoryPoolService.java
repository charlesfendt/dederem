/**
 *
 */
package org.dederem.common.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.SuiteDesc;

import lombok.Getter;

/**
 * Service for repository pool management.
 *
 * @author charles
 */
@Singleton
public final class RepositoryPoolService {

    /** Configuration service. */
    @Inject
    private ConfigService configService;

    /** List of suites to manage. */
    @Getter
    private final Set<String> suites;
    
    /** List of suites to manage. */
    private final Map<String, SuiteDesc> suitesDesc = new HashMap<>();

    /**
     * Default constructor.
     */
    public RepositoryPoolService() {
        super();
        // use the configuration parameter to manage suites.
        final String[] suites = StringUtils.split(this.configService.getSuites(), " \t,;");
        this.suites = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(suites)));

        for (final String suite : this.suites) {
            this.suitesDesc.put(suite, new SuiteDesc());
        }
    }

    /**
     * Method to reload the pool directory.
     */
    public void reloadCache() {
        final File pool = new File(this.configService.getRepoDir(), "pool");

        for (final String suite : this.suites) {
            final File dir = new File(pool, suite);
            final SuiteDesc desc = this.suitesDesc.get(suite);
            desc.setName(suite);
            desc.setDirectory(dir);
            
            // load the suite !
            final Iterator<File> iterateFiles = FileUtils.iterateFiles(dir, new String[] { ".deb" }, true);
            while (iterateFiles.hasNext()) {
                final File nextDeb = iterateFiles.next();
                // load the deb
                final DebPackageDesc elt = this.loadPackageDesc(nextDeb);
                desc.getPackages().put(elt.getDebPackage(), elt);
            }
        }
    }
    
    private DebPackageDesc loadPackageDesc(final File deb) {
        final String path = deb.getAbsolutePath();
        
        final DebPackageDesc result = new DebPackageDesc();
        result.setFileName(path);
        result.setFileSize(deb.length());
        result.setPresent(true);

        final File desc = new File(path + ".desc");
        if (desc.exists()) {
            // load description
        } else {
            try {
                // generate the description file.

                // dpkg --info $deb_path control | grep -E "^(Version|Package|Architecture)"
                this.hashFile(desc, result);
            } catch (final Exception ex) {
                ex.printStackTrace();// FIXME logger
            }
        }
        // FIXME
        return result;
    }
    
    private void hashFile(final File file, final DebPackageDesc desc) throws NoSuchAlgorithmException, IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            final MessageDigest digestMD5 = MessageDigest.getInstance("MD5");
            final MessageDigest digestSHA1 = MessageDigest.getInstance("SHA1");
            final MessageDigest digestSHA256 = MessageDigest.getInstance("SHA256");
            
            final byte[] bytesBuffer = new byte[4096];
            int bytesRead = -1;
            
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digestMD5.update(bytesBuffer, 0, bytesRead);
                digestSHA1.update(bytesBuffer, 0, bytesRead);
                digestSHA256.update(bytesBuffer, 0, bytesRead);
            }
            
            desc.setPackageMd5(DatatypeConverter.printHexBinary(digestMD5.digest()));
            desc.setPackageSha1(DatatypeConverter.printHexBinary(digestSHA1.digest()));
            desc.setPackageSha256(DatatypeConverter.printHexBinary(digestSHA256.digest()));
        }
    }
}
