/**
 *
 */
package org.dederem.common.service;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
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
        // FIXME use a configuration parameter to manage suites.
        final Set<String> suitesLst = new HashSet<>();
        suitesLst.add("main");
        suitesLst.add("contrib");
        suitesLst.add("non-free");
        this.suites = Collections.unmodifiableSet(suitesLst);

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
            // generate the description file.
            // dpkg --info $deb_path control | grep -E "^(Version|Package|Architecture)"
        }
        // FIXME
        return result;
    }
}
