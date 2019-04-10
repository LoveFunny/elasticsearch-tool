package com.supermap.wegis.elastic.service.impl;

import com.supermap.wegis.elastic.service.Query;
import com.supermap.wegis.elastic.data.QueryParamData;
import com.supermap.wegis.elastic.data.QueryResultData.ListQueryResultData;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *列表查询
 *
 */
@Service
@Qualifier("listQuery")
public class ListQuery extends Query<ListQueryResultData, QueryParamData> {

    @Override
    public ListQueryResultData query(QueryParamData param) throws IOException {

        ListQueryResultData resultData = new ListQueryResultData();

        SearchRequest request = new SearchRequest();
        // 设置查询条件
        setSearchRequest(request, param);

        List<Map<String,Object>> resultMapList = new ArrayList<Map<String,Object>>();
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        // 总数
        long totalCount = searchHits.totalHits;
        searchHits.forEach(e -> {
            resultMapList.add(e.getSourceAsMap());
        });
        // 设置查询总数
        resultData.setTotalCount((int)totalCount);
        // 设置查询结果详情
        resultData.setResultDetailList(resultMapList);
        return resultData;
    }











}
