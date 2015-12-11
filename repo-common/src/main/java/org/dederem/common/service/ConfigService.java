/**
 * ConfigService.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.Singleton;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

/**
 * Service for the configuration management.
 *
 * @author charles
 */
@Singleton
public class ConfigService {

	/** The directory for the configuration. */
	@Getter
	private final File configDir;
	/** The directory for the repository. */
	@Getter
	private final File repoDir;
	
	/**
	 * Default constructor.
	 *
	 * @throws IOException
	 *             I/O error.
	 */
	public ConfigService() throws IOException {
		super();

		final Properties props = new Properties();
		try (final InputStream input = this.getClass().getResourceAsStream("/config/defaultConfig.properties")) {
			props.load(input);
		}
		this.configDir = new File(StringUtils.defaultIfEmpty(props.getProperty("config.dir"), "/etc/dederem"));
		this.repoDir = new File(StringUtils.defaultIfEmpty(props.getProperty("repo.dir"), "/opt/dederem"));
	}
	
	/**
	 * Method to read the configuration.
	 *
	 * @throws IOException
	 *             I/O error.
	 */
	public final void loadConfig() throws IOException {
		// FIXME
	}

	/**
	 * Method to read the repository.
	 *
	 * @throws IOException
	 *             I/O error.
	 */
	public void loadRepo() throws IOException {
		// FIXME
	}
}
