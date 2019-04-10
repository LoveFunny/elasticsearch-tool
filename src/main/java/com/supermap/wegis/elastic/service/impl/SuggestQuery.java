package com.supermap.wegis.elastic.service.impl;

import com.supermap.wegis.elastic.service.Query;
import com.supermap.wegis.elastic.data.QueryParamData;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *搜索建议查询
 *
 */
@Service
@Qualifier("suggestQuery")
public class SuggestQuery extends Query<List<String>, QueryParamData> {

    @Override
    public List<String> query(QueryParamData param) throws IOException {

        SearchRequest request = new SearchRequest();
        // 设置查询条件
        setSearchRequest(request, param);
        List<String> list = new ArrayList<String>();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        response.getSuggest().getSuggestion("completion").getEntries().forEach( e -> e.getOptions().
                forEach(x -> list.add(x.getText().string())));
        return list;
    }
}
