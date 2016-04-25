/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *     <a href="mailto:tmartins@nuxeo.com">Thierry Martins</a>
 */

package org.nuxeo.ecm.quota.size;

import static org.nuxeo.ecm.quota.size.QuotaAwareDocument.DOCUMENTS_SIZE_STATISTICS_FACET;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.quota.QuotaUtils;

/**
 * Simple factory for {@link QuotaAwareDocument} document model adapter
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 * @since 5.6
 */
public class QuotaAwareDocumentFactory implements DocumentAdapterFactory {

    public static QuotaAware make(DocumentModel doc) {
        if (!doc.hasFacet(DOCUMENTS_SIZE_STATISTICS_FACET)) {
            doc.addFacet(DOCUMENTS_SIZE_STATISTICS_FACET);
        }
        return new QuotaAwareDocument(doc);
    }

    public static void unmake(DocumentModel doc) {
        if (doc.hasFacet(DOCUMENTS_SIZE_STATISTICS_FACET)) {
            new QuotaAwareDocument(doc).resetInfos();
            saveDocument(doc); // first save to flush the reset
            doc.removeFacet(DOCUMENTS_SIZE_STATISTICS_FACET);
            saveDocument(doc); // second save to flush facet removal
        }
    }

    protected static DocumentModel saveDocument(DocumentModel doc) {
        doc.putContextData(DocumentsSizeUpdater.DISABLE_QUOTA_CHECK_LISTENER, Boolean.TRUE);
        // set flags to disable listeners
        QuotaUtils.disableListeners(doc);
        DocumentModel origDoc = doc;
        doc = doc.getCoreSession().saveDocument(doc);
        // remove flags as they could be kept in the document for a long time otherwise
        QuotaUtils.clearContextData(doc);
        // also remove flags from original doc, which the caller still references
        QuotaUtils.clearContextData(origDoc);
        return doc;
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> adapter) {
        if (doc.hasFacet(DOCUMENTS_SIZE_STATISTICS_FACET)) {
            return adapter.cast(new QuotaAwareDocument(doc));
        }
        return null;
    }
}
