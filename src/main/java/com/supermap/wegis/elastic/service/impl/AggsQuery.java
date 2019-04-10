package com.supermap.wegis.elastic.service.impl;

import com.supermap.wegis.elastic.service.Query;
import com.supermap.wegis.elastic.data.QueryParamData;
import com.supermap.wegis.elastic.data.QueryResultData.AggsQueryResultData;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;


/**
 * 聚合查询
 *
 */
@Service
@Qualifier("aggsQuery")
public class AggsQuery extends Query<AggsQueryResultData, QueryParamData> {

    @Override
    public AggsQueryResultData query(QueryParamData param) throws IOException {

        AggsQueryResultData resultData = new AggsQueryResultData();
        SearchRequest searchRequest = new SearchRequest();
        // 聚合名称
        setSearchRequest(searchRequest, param);
        // 查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        List<Map<Object,Object>> aggMapList = getAggs(aggregationMap);
        // 设置查询聚合结果
        resultData.setStatisMapList(aggMapList);
        return resultData;
    }

    /**
     * 获取聚合结果
     *
     * @param aggregationMap
     * @return
     */
    private static List<Map<Object,Object>> getAggs(Map<String, Aggregation> aggregationMap){

        List<Map<Object,Object>> mapList = new ArrayList<>();

        for(String aggName : aggregationMap.keySet()){

            LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<String,Object>();

            Map<Object,Object> map = new HashMap<>();
            Aggregation aggregation = aggregationMap.get(aggName);
            // 聚合字段
            String field = aggName.substring(0, aggName.lastIndexOf("_"));
            // 聚合类型
            String type = "";
            Object value = null;
            // 分组
            if(aggregation instanceof Terms){
                Terms terms = (Terms)aggregation;
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                for(Terms.Bucket bucket : buckets){
                    Map<String, Aggregation> tempAggMap = bucket.getAggregations().asMap();
                    Map<Object,Object> keyMap = new HashMap<>();
                    keyMap.put("field", field);
                    keyMap.put("type", "terms");
                    keyMap.put("key", bucket.getKey());
                    keyMap.put("value", bucket.getDocCount());
                    List<Map<Object,Object>> tempMapList = getAggs(tempAggMap);
                    map.put(keyMap, tempMapList);
                }
                mapList.add(map);
                return mapList;
            // 求平均数
            }else if(aggregation instanceof Avg){
                Avg avg = (Avg)aggregation;
                type = "avg";
                value = avg.getValue();
            //  求最大值
            }else if(aggregation instanceof Max){
                Max max = (Max)aggregation;
                type = "max";
                value = max.getValue();
            // 求最下值
            }else if(aggregation instanceof Min){
                Min min = (Min)aggregation;
                type = "min";
                value = min.getValue();
            // 求总数
            }else if(aggregation instanceof ValueCount){
                ValueCount count = (ValueCount)aggregation;
                type = "count";
                value = count.getValue();
            }
            map.put("field",field);
            map.put("type",type);
            map.put("value",value);
            mapList.add(map);
        }
        return mapList;
    }
}
