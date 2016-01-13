/**
 *
 */
package org.dederem.common.bean;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Description for a suite.
 *
 * @author charles
 */
@Getter
@Setter
public final class SuiteDesc {
    
    /** Name of the suite. */
    private String name;
    /** root directory for the suite. */
    private File directory;
    
    /** List of packages. */
    private final Map<DebPackage, DebPackageDesc> packages = new HashMap<>();
}
