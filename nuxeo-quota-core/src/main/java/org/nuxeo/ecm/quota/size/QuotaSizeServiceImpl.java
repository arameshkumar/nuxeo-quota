/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     dmetzler
 */
package org.nuxeo.ecm.quota.size;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author dmetzler
 *
 */
public class QuotaSizeServiceImpl extends DefaultComponent implements
        QuotaSizeService {

    private Set<String> excludedPathList = new HashSet<String>();

    @Override
    public Collection<String> getExcludedPathList() {
        return excludedPathList;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if ("exclusions".equals(extensionPoint)) {
            BlobExcludeDescriptor descriptor = (BlobExcludeDescriptor) contribution;
            excludedPathList.add(descriptor.getPathRegexp());
        }

    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if ("exclusions".equals(extensionPoint)) {
            BlobExcludeDescriptor descriptor = (BlobExcludeDescriptor) contribution;
            String pathRegexp = descriptor.getPathRegexp();
            if (excludedPathList.contains(pathRegexp)) {
                excludedPathList.remove(pathRegexp);
            }
        }
    }

}