/**
 * ConfigLoader.java
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
package org.dederem.common.loader;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.dederem.common.service.ConfigService;
import org.dederem.common.service.RepositoryPoolService;

/**
 * Loader for the application.
 *
 * @author charles
 */
@Startup
@Singleton
public class AppLoader {

    /** The application configuration service. */
    @Inject
    private ConfigService configService;

    /** The service for repository pool management. */
    @Inject
    private RepositoryPoolService repoPoolService;

    /**
     * Application initialization.
     *
     * @throws IOException
     *             I/O error.
     */
    @PostConstruct
    public void load() throws IOException {
        this.configService.loadConfig();
        this.repoPoolService.reloadCache();
    }
}
