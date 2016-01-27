/**
 *
 */
package org.dederem.common.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dederem.common.bean.DebPackage;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.SuiteDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/**
 * Service for repository pool management.
 *
 * @author charles
 */
@Singleton
public final class RepositoryPoolService {
    
    /** Logger of the class. */
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryPoolService.class);
    
    /** Configuration service. */
    @Inject
    private ConfigService configService;
    
    /** List of suites to manage. */
    @Getter
    private Set<String> suites;

    /** List of suites to manage. */
    private final Map<String, SuiteDesc> suitesDesc = new HashMap<>();
    
    /** TRUE to use /usr/bin/dpkg. */
    private final boolean useUsrBinDpkg;
    
    /**
     * Default constructor.
     */
    public RepositoryPoolService() {
        super();
        
        // check if /usr/bin/dpkg exists
        final File dpkg = new File("/usr/bin/dpkg");
        this.useUsrBinDpkg = dpkg.exists();
    }
    
    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        // initialize bouncyCastle
        Security.addProvider(new BouncyCastleProvider());

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
                try {
                    final DebPackageDesc elt = this.loadPackageDesc(nextDeb);
                    desc.getPackages().put(elt.getDebPackage(), elt);
                } catch (final IOException ex) {
                    RepositoryPoolService.LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Method to load a file in the pool.
     *
     * @param deb
     *            Debian package.
     * @return A The package description object
     * @throws IOException
     *             I/O or parsing error.
     */
    public DebPackageDesc loadPackageDesc(final File deb) throws IOException {
        final String path = deb.getAbsolutePath();

        final DebPackageDesc result = new DebPackageDesc();
        result.setFileName(StringUtils.substring(path, path.indexOf("/pool/") + 1));
        result.setFileSize(deb.length());
        result.setPresent(true);
        
        final Properties props = new Properties();
        final File desc = new File(path + ".desc");
        if (desc.exists()) {
            // load description
            try (InputStream input = new FileInputStream(desc)) {
                props.load(input);
            }
            result.setPackageArch(props.getProperty("arch"));
            result.setPackageMd5(props.getProperty("md5sum"));
            result.setPackageName(props.getProperty("name"));
            result.setPackageSha1(props.getProperty("sha1sum"));
            result.setPackageSha256(props.getProperty("sha256sum"));
            result.setPackageVersion(props.getProperty("version"));
        } else {
            try {
                // generate the description file.
                if (this.useUsrBinDpkg) {
                    // use /usr/bin/dpkg
                    final Process process = Runtime.getRuntime().exec("/usr/bin/dpkg --info " + deb.getAbsolutePath()
                            + " control | grep -E '^(Package|Version|Architecture)' | sed 's/\\(^[^:]*\\):\\W*/\\1=/g'");
                    process.getOutputStream().close();

                    final Map<String, String> map = new HashMap<>();
                    try (final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        final String[] elt1 = StringUtils.split(stdout.readLine(), "=", 2);
                        map.put(elt1[0], elt1[1]);
                        final String[] elt2 = StringUtils.split(stdout.readLine(), "=", 2);
                        map.put(elt2[0], elt2[1]);
                        final String[] elt3 = StringUtils.split(stdout.readLine(), "=", 2);
                        map.put(elt3[0], elt3[1]);
                    }

                    try {
                        process.waitFor();
                    } catch (final InterruptedException exc) {
                        throw new IOException(exc);
                    }
                    
                    result.setPackageName(map.get("Package"));
                    result.setPackageArch(map.get("Architecture"));
                    result.setPackageVersion(map.get("Version"));
                } else {
                    // parse the filename "by hand"
                    final String[] elts = StringUtils.split(StringUtils.substringBeforeLast(deb.getName(), "."), '_');
                    if (elts.length == 3) {
                        result.setPackageName(elts[0]);
                        result.setPackageVersion(elts[1]);
                        result.setPackageArch(elts[2]);
                    } else {
                        throw new IOException(new MalformedURLException());
                    }
                }
                
                this.hashFile(deb, result);
                
                // Write the description file
                props.setProperty("name", result.getPackageName());
                props.setProperty("arch", result.getPackageArch());
                props.setProperty("version", result.getPackageVersion());
                props.setProperty("md5sum", result.getPackageMd5());
                props.setProperty("sha1sum", result.getPackageSha1());
                props.setProperty("sha256sum", result.getPackageSha256());
                desc.createNewFile();
                try (OutputStream out = new FileOutputStream(desc)) {
                    props.store(out, StringUtils.EMPTY);
                }
                
            } catch (final GeneralSecurityException ex) {
                throw new IOException(ex);
            }
        }
        return result;
    }

    /**
     * Method to populate the DebPackageDesc object with all the hash of the given file.
     *
     * @param file
     *            File to hash.
     * @param desc
     *            The description to populate with MD5, SHA1 and SHA256.
     * @throws NoSuchAlgorithmException
     *             This exception is thrown when one of the hash method if not supported.
     * @throws IOException
     *             I/O error on the File object.
     */
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
    
    /**
     * Getter.
     *
     * @param suiteStr
     *            Name of the suite for the package.
     * @param pack
     *            Package information to search.
     * @return The package in the local repository or null if not found.
     */
    public DebPackageDesc getPackageInLocalRepo(final String suiteStr, final DebPackage pack) {
        final DebPackageDesc result;
        final SuiteDesc suite = this.suitesDesc.get(suiteStr);
        if (suite == null) {
            result = null;
        } else {
            result = suite.getPackages().get(pack);
        }
        return result;
    }
    
    /**
     * Method to check package equality.
     *
     * @param pkgDesc
     *            The requested package.
     * @param local
     *            The known package.
     */
    public boolean checkPackageEquality(final DebPackageDesc pkgDesc, final DebPackageDesc local) {
        boolean result = true;
        if (local.getFileSize() != pkgDesc.getFileSize()) {
            result = false;
            RepositoryPoolService.LOG.warn("The package {0} has the wrong size.", pkgDesc.getDebPackage().toString());
        }
        final String packageSha1 = local.getPackageSha1();
        if (StringUtils.isNotEmpty(packageSha1) && packageSha1.contentEquals(pkgDesc.getPackageSha1())) {
            result = false;
            RepositoryPoolService.LOG.warn("The package {0} has the wrong SHA1.", pkgDesc.getDebPackage().toString());
        }
        final String packageSha256 = local.getPackageSha256();
        if (StringUtils.isNotEmpty(packageSha256) && packageSha256.contentEquals(pkgDesc.getPackageSha256())) {
            result = false;
            RepositoryPoolService.LOG.warn("The package {0} has the wrong SHA256.", pkgDesc.getDebPackage().toString());
        }
        return result;
    }
}
