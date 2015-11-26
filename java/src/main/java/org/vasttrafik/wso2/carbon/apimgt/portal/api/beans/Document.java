package org.vasttrafik.wso2.carbon.apimgt.portal.api.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.vasttrafik.wso2.carbon.apimgt.portal.api.query.Query;
import org.vasttrafik.wso2.carbon.apimgt.portal.api.utils.URLCodec;
import org.vasttrafik.wso2.carbon.apimgt.store.api.beans.DocumentDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Oskarsson <daniel.oskarsson@gmail.com>
 */
public class Document {

    public enum Type {
        file, inline, url
    }

    private String name;
    private Type type;
    private String summary;
    private String content;
    private String url;

    public static Document valueOf(final DocumentDTO documentDTO) {
        final Document document = new Document();
        document.name = documentDTO.name;
        document.type = Type.valueOf(documentDTO.sourceType.name().toLowerCase());
        document.summary = documentDTO.summary;
        document.content = null;
        document.url = documentDTO.sourceUrl;
        return document;
    }

    @JsonProperty("id")
    public String getId() {
        return URLCodec.encodeUTF8(name);
    }

    public boolean matchesAny(String query) {
        for (final String value : getMap().values()) {
            if (query.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(String query) {
        return new Query(query, "name").matches(getMap());
    }

    private Map<String, String> getMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("id", getId());
        map.put("name", name);
        map.put("type", type.name());
        map.put("summary", summary);
        return map;
    }

}
