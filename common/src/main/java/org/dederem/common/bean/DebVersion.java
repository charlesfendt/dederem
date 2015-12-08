/**
 *
 */
package org.dederem.common.bean;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Description of a Debian version.
 *
 * @author charles
 */
@Getter
@Setter
public final class DebVersion {

	/** Name of the version. */
	private String versionName;
	
	/** Name of the file which define the version. */
	private String packageFile;
	
	private final List<DebPackage> packages = new LinkedList<>();
}
