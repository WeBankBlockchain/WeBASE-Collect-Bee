package com.webank.webasebee.db.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/23
 */
@Service
@Slf4j
public class ESService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void createIndex(TransportClient client, String index) {
        try {
            client.admin().indices().prepareCreate(index.toLowerCase()).get();
        }catch (Exception e) {
            log.error("ESService createIndex " + index +" failed， reason ：", e);
        }
    }

    public  void createMapping(TransportClient client, String index, String type, Map<String, Object> mappings) {
        try {
            client.admin().indices().preparePutMapping(index)
                    .setType(type).setSource(mappings)
                    .execute().actionGet();
        }catch (Exception e) {
            log.error("ESService createMapping failed ，index is " + index +" reason ：", e);
        }
    }

    public void createDocument(TransportClient client, String index, String type, String id, Object object) {
        try {
            client.prepareIndex(index, type).
                    setId(id).
                    setSource(MAPPER.writeValueAsString(object), XContentType.JSON).get();
        }catch (Exception e) {
            log.error("ESService createDocument failed ，index is " + index +" reason ：", e);
        }
    }

    public void createDocument(TransportClient client, String index, String type, Object object) {
        try {
            client.prepareIndex(index, type).
                    setSource(MAPPER.writeValueAsString(object), XContentType.JSON).get();
        }catch (Exception e) {
            log.error("ESService createDocument failed ，index is " + index +" reason ：", e);
        }
    }

    public  List<SearchHit> queryString(TransportClient client, String index, String type, String queryString) {
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.queryStringQuery(queryString)).get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        List<SearchHit> searchHitList = new ArrayList<>();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            searchHitList.add(next);
        }
        return searchHitList;
    }

    public void initBaseMapping(TransportClient client, String index, Class<?> cla) throws Exception {
        Map<String,Object> mappings = new HashMap<>();
        Map<String,Object> type = new HashMap<>();
        mappings.put(cla.getSimpleName().toLowerCase(), type);
        type.put("dynamic", true);

        Map<String,Object> properties = new HashMap<>();
        type.put("properties", properties);

        Field[] fields = cla.getFields();

        for (Field field : fields) {
            Map<String,Object> property = new HashMap<>();
            property.put("type","text");
            properties.put(field.getName().toLowerCase(), property);
        }
        createMapping(client, index, cla.getSimpleName().toLowerCase(), mappings);
    }


    public void deleteIndex(TransportClient client, String index) {
        client.admin().indices().prepareDelete(index) .execute().actionGet();
    }

    public void deleteDocumentById(TransportClient client, String index, String id){
        client.prepareDelete(index,"_doc",id).get();
    }

    public boolean indexExists(TransportClient client, String index){
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
        return response.isExists();
    }

}
