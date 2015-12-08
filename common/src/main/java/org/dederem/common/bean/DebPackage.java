/**
 *
 */
package org.dederem.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Description of a Debian package.
 *
 * @author charles
 */
@Getter
@Setter
public final class DebPackage {

	/** Name of the debian package. */
	private String packageName;
	/** Version of the debian package. */
	private String packageVersion;
	/** HASH of the debian package. */
	private String packageHash;
	
	/** TRUE if present on the file system. */
	private boolean present;
}
