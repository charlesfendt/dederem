/**
 * VersionAnalyseService.java
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
package org.dederem.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.ejb.Singleton;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.dederem.common.bean.DebPackageDesc;
import org.dederem.common.bean.DebVersion;

/**
 * Service for Debian version analyzes.
 *
 * @author charles
 *
 */
@Singleton
public class VersionAnalyseService {

	/**
	 * Read method for "Packages" file.
	 *
	 * @param input
	 *            InputStream on the text file
	 * @return The object populated with the content of the file.
	 * @throws IOException
	 *             I/O error.
	 */
	public final DebVersion analyzeGzFile(final InputStream input) throws IOException {
		return this.analyzeFile(new GZIPInputStream(input));
	}

	/**
	 * Read method for "Packages" file.
	 *
	 * @param input
	 *            InputStream on the text file
	 * @return The object populated with the content of the file.
	 * @throws IOException
	 *             I/O error.
	 */
	public final DebVersion analyzeFile(final InputStream input) throws IOException {
		final DebVersion result = new DebVersion();
		
		final Map<String, StringBuilder> data = new HashMap<>();
		StringBuilder lastData = null; // NOPMD - init
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charsets.UTF_8));
		String line = reader.readLine();
		while (line != null) {
			if (line.isEmpty()) {
				// manage the end of the bloc
				final DebPackageDesc pkgDesc = this.readPackageDesc(data);
				if (pkgDesc != null) {
					result.getPackages().add(pkgDesc);
				}
				data.clear();
			} else {
				// manage a new entry in the current bloc
				if (Character.isWhitespace(line.charAt(0))) {
					if (lastData != null) {
						lastData.append(line);
					}
				} else {
					final String key = StringUtils.substringBefore(line, ":");
					final String value = StringUtils.substringAfter(line, ":");
					lastData = new StringBuilder(value);
					data.put(key, lastData);
				}
			}
			line = reader.readLine();
		}
		return result;
	}
	
	private DebPackageDesc readPackageDesc(final Map<String, StringBuilder> data) {
		final DebPackageDesc result;
		final String name = this.extractValueStr(data, "Package");
		final String version = this.extractValueStr(data, "Version");
		final String architecture = this.extractValueStr(data, "Architecture");
		if (StringUtils.isNoneEmpty(name, version, architecture)) {
			result = new DebPackageDesc();
			result.setPackageName(name);
			result.setPackageVersion(version);
			result.setPackageArch(architecture);
			result.setPackageSha1(this.extractValueStr(data, "SHA1"));
			result.setPackageSha256(this.extractValueStr(data, "SHA256"));
			result.setFileName(this.extractValueStr(data, "Filename"));
			result.setFileSize(this.extractValueLong(data, "Size"));
		} else {
			result = null;
		}
		return result;
	}
	
	private String extractValueStr(final Map<String, StringBuilder> data, final String key) {
		final StringBuilder val = data.get(key);
		return val == null ? null : val.toString().trim();
	}

	private long extractValueLong(final Map<String, StringBuilder> data, final String key) {
		final String val = this.extractValueStr(data, key);
		return StringUtils.isNumeric(val) ? Long.parseLong(val) : -1;
	}
}
