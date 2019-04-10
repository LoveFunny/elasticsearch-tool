package com.supermap.wegis.elastic.data;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * ES查询结果
 *
 * Created by lms on 2019/3/5 10:28
 */

@Data
public class QueryResultData {

    // 总数
    private int totalCount;

    @Data
    public static class ListQueryResultData extends QueryResultData {
        // 查询详情
        private List<Map<String,Object>> resultDetailList;

    }

    @Data
    public static class AggsQueryResultData extends QueryResultData {
        // 统计
        private Map<String,Integer> statisMap;

        private List<Map<Object,Object>> statisMapList;
    }


}
