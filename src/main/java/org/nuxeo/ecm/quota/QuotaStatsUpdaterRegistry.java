/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.nuxeo.ecm.quota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;

/**
 * Registry for {@link QuotaStatsUpdater}s, handling merge of registered
 * {@link QuotaStatsUpdaterDescriptor} elements.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class QuotaStatsUpdaterRegistry extends
        ContributionFragmentRegistry<QuotaStatsUpdaterDescriptor> {

    protected Map<String, QuotaStatsUpdater> quotaStatsUpdaters = new HashMap<String, QuotaStatsUpdater>();

    public QuotaStatsUpdater getQuotaStatsUpdater(String name) {
        return quotaStatsUpdaters.get(name);
    }

    public List<QuotaStatsUpdater> getQuotaStatsUpdaters() {
        return new ArrayList<QuotaStatsUpdater>(quotaStatsUpdaters.values());
    }

    @Override
    public String getContributionId(QuotaStatsUpdaterDescriptor descriptor) {
        return descriptor.getName();
    }

    @Override
    public void contributionUpdated(String id,
            QuotaStatsUpdaterDescriptor contrib,
            QuotaStatsUpdaterDescriptor newOrigContrib) {
        if (contrib.isEnabled()) {
            try {
                quotaStatsUpdaters.put(id,
                        contrib.getQuotaStatsUpdaterClass().newInstance());
            } catch (Exception e) {
                throw new ClientRuntimeException(e);
            }
        } else {
            quotaStatsUpdaters.remove(id);
        }
    }

    @Override
    public void contributionRemoved(String id,
            QuotaStatsUpdaterDescriptor descriptor) {
        quotaStatsUpdaters.remove(id);
    }

    @Override
    public QuotaStatsUpdaterDescriptor clone(
            QuotaStatsUpdaterDescriptor descriptor) {
        return descriptor.clone();
    }

    @Override
    public void merge(QuotaStatsUpdaterDescriptor src,
            QuotaStatsUpdaterDescriptor dst) {
        dst.setQuotaStatsUpdaterClass(src.getQuotaStatsUpdaterClass());
        dst.setEnabled(src.isEnabled());
    }

}
