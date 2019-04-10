package com.supermap.wegis.elastic.service.impl;

import com.supermap.wegis.elastic.service.Query;
import com.supermap.wegis.elastic.data.QueryParamData;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 详情查询
 *
 */
@Service
@Qualifier("detailQuery")
public class DetailQuery extends Query<Map<String,Object>, QueryParamData> {


    @Override
    public Map<String,Object> query(QueryParamData param) throws IOException {
        // 详情map
        Map<String,Object> resultMap = new HashMap<String,Object>();
        SearchRequest searchRequest = new SearchRequest();
        // 设置查询条件
        setSearchRequest(searchRequest, param);
        // 查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 查询结果
        SearchHits searchHits = searchResponse.getHits();
        if(searchHits.totalHits != 0.0 ){
            resultMap = searchResponse.getHits().getAt(0).getSourceAsMap();
        }
        return resultMap;
    }
}
