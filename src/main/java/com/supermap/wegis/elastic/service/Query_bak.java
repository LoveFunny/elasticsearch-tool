package com.supermap.wegis.elastic.service;

import com.supermap.wegis.elastic.data.Condition;
import com.supermap.wegis.elastic.data.Condition.*;
import com.supermap.wegis.elastic.data.QueryParamData;
import com.supermap.wegis.elastic.data.RangeData;
import com.supermap.wegis.elastic.data.Type.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Query_bak<R,P> {

    // 搜索建议
    @Value("${elastic.suggest_size}")
    private int SUGGEST_DEFAULT_SIZE;

    @Autowired
    public RestHighLevelClient client;

    public abstract R query(P param) throws IOException;

    /**
     * 获取范围查询条件
     *
     * @param rangeQueryCondition 范围查询条件
     * @return
     */
    public RangeQueryBuilder getRangeQuery(RangeCondition rangeQueryCondition){
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(rangeQueryCondition.getField());
        RangeData rangeMin = rangeQueryCondition.getRangeMin();
        RangeData rangeMax = rangeQueryCondition.getRangeMax();
        if(rangeMin != null){
            Object min = rangeMin.getValue();
            boolean include = rangeMin.isInclude();
            if(include){
                rangeQueryBuilder.gte(min);
            }else{
                rangeQueryBuilder.gt(min);
            }
        }
        if(rangeMax != null){
            Object max = rangeMax.getValue();
            boolean include = rangeMax.isInclude();
            if(include){
                rangeQueryBuilder.lte(max);
            }else{
                rangeQueryBuilder.lt(max);
            }
        }
        return rangeQueryBuilder;
    }

    /**
     * 设置查询条件
     *
     * @param boolQueryBuilder
     * @param queryBuilder
     * @param boolType
     */
    public void setBoolQuery(BoolQueryBuilder boolQueryBuilder, QueryBuilder queryBuilder, BoolType boolType){
        switch (boolType){
            // 且
            case MUST:
                boolQueryBuilder.must(queryBuilder);
                break;
            // 或
            case SHOULD:
                boolQueryBuilder.should(queryBuilder);
                break;
            // 非
            case MUST_NOT:
                boolQueryBuilder.mustNot(queryBuilder);
                break;
        }
    }

    /**
     * 设置查询请求参数
     *
     * @param searchRequest
     * @param queryParamData
     * @return
     */
    public void setSearchRequest(SearchRequest searchRequest , QueryParamData queryParamData) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 查询条件
        Condition[] conditions = queryParamData.getConditions();
        // 页码
        int pageIndex = queryParamData.getPageIndex();
        // 每页显示的数量
        int pageSize = queryParamData.getPageSize();
        // 验证查询条件，过滤无效的查询条件
        conditions = vaildConditions(conditions);
        List<AggregationBuilder> aggList = new ArrayList<>();
        for(Condition condition : conditions){
            // 字段
            String field = condition.getField();
            // 字段类型
            FieldType fieldType = condition.getFieldType();
            switch (fieldType){
                // 聚合查询
                case AGGS:
                    AggCondition aggCondition = (AggCondition)condition;
                    AggType aggType = aggCondition.getAggType();
                    String aggsName = "";
                    AggregationBuilder aggregationBuilder = null;
                    switch (aggType){
                        case TERMS:
                            aggsName = field + "_by";
                            aggregationBuilder = AggregationBuilders.terms(aggsName).field(field);
                            break;
                        case AVG:
                            aggsName = field + "_avg";
                            aggregationBuilder = AggregationBuilders.avg(aggsName).field(field);
                            break;
                        case COUNT:
                            aggsName = field + "_count";
                            aggregationBuilder = AggregationBuilders.count(aggsName).field(field);
                            break;
                        case MAX:
                            aggsName = field + "_max";
                            aggregationBuilder = AggregationBuilders.max(aggsName).field(field);
                            break;
                        case MIN:
                            aggsName = field + "_min";
                            aggregationBuilder = AggregationBuilders.min(aggsName).field(field);
                            break;
                    }
                    if(!StringUtils.isEmpty(aggsName)){
                        aggList.add(aggregationBuilder);
                    }
                    break;
                // 匹配查询
                case MATCH:
                    CommonCondition matchCondition = (CommonCondition)condition;
                    Object[] matchValue = matchCondition.getValues();
                    MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(field, matchValue[0]);
                    setBoolQuery(boolQueryBuilder, matchQueryBuilder, matchCondition.getBoolType());
                    break;
                // 准确值查询
                case TERM:
                    CommonCondition termCondition = (CommonCondition)condition;
                    Object[] termValue = termCondition.getValues();
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(field, termValue);
                    setBoolQuery(boolQueryBuilder, termsQueryBuilder, termCondition.getBoolType());
                    break;
                // 范围查询
                case RANGE:
                    RangeCondition rangeCondition = (RangeCondition)condition;
                    RangeQueryBuilder rangeQueryBuilder = getRangeQuery(rangeCondition);
                    setBoolQuery(boolQueryBuilder, rangeQueryBuilder, rangeCondition.getBoolType());
                    break;
                // 搜索建议
                case SUGGEST:
                    CommonCondition suggestCondition = (CommonCondition)condition;
                    Object[] suggestValue = suggestCondition.getValues();
                    CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion(field).
                            prefix(String.valueOf(suggestValue[0])).skipDuplicates(true).size(SUGGEST_DEFAULT_SIZE);
                    SuggestBuilder suggestBuilder = new SuggestBuilder();
                    suggestBuilder.addSuggestion("completion", completionSuggestionBuilder);
                    searchSourceBuilder.suggest(suggestBuilder);
                    break;
                // 排序
                case SORT:
                    SortCondition sortCondition = (SortCondition)condition;
                    SortType sortType = sortCondition.getSortType();
                    switch (sortType){
                        case ASC:
                            searchSourceBuilder.sort(field, SortOrder.ASC);
                            break;
                        case DESC:
                            searchSourceBuilder.sort(field, SortOrder.DESC);
                            break;
                    }
                // 空间查询
                case GEO:
                    GeoCondition geoCondition = (GeoCondition)condition;
                    // 查询空间对象
                    ShapeBuilder shapeBuilder = geoCondition.getShape();
                    GeoShapeQueryBuilder geoShapeQueryBuilder = QueryBuilders.geoShapeQuery(field, shapeBuilder);
                    // 查询空间类型
                    GeoRelationType geoRelationType = geoCondition.getGeoRelationType();
                    switch (geoRelationType){
                        // 包含
                        case WITHIN:
                            geoShapeQueryBuilder.relation(ShapeRelation.WITHIN);
                            break;
                         // 相离
                        case DISJOINT:
                            geoShapeQueryBuilder.relation(ShapeRelation.DISJOINT);
                            break;
                        // 相交
                        case INTERSECTS:
                            geoShapeQueryBuilder.relation(ShapeRelation.INTERSECTS);
                            break;
                    }
                    setBoolQuery(boolQueryBuilder, geoShapeQueryBuilder, geoCondition.getBoolType());
                    break;
            }
        }
        if(aggList.size() > 0 ){
            AggregationBuilder aggregationBuilder = getAggrationBuilder(aggList);
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from((pageIndex - 1) * pageSize);
        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(queryParamData.getIndices());
    }

    /**
     * 获取聚合查询
     *
     * @param aggList 聚合列表
     * @return
     */
    private AggregationBuilder getAggrationBuilder(List<AggregationBuilder> aggList){
        AggregationBuilder aggregationBuilder = null;
        int size = aggList.size();
        for(int i = size -1; i >= 0; i--){
            AggregationBuilder tempAggregationBuilder = aggList.get(i);
            if(i == size -1){
                aggregationBuilder = tempAggregationBuilder;
            }else{
                tempAggregationBuilder.subAggregation(aggregationBuilder);
                aggregationBuilder = tempAggregationBuilder;
            }
        }
        return aggregationBuilder;
    }

    /**
     * 验证查询条件是否有效
     *
     * @param conditions 查询条件
     * @return
     */
    private Condition[] vaildConditions(Condition[] conditions){

        List<Condition> vaildedConditionList = new ArrayList<Condition>();
        for(Condition condition : conditions){
            FieldType fieldType = condition.getFieldType();
            if(fieldType == FieldType.TERM || fieldType == FieldType.MATCH  || fieldType == FieldType.SUGGEST ){
                CommonCondition commonCondition = (CommonCondition)condition;
                Object[] values  = commonCondition.getValues();
                if(null == values || values.length == 0){
                    continue;
                }
            }
            vaildedConditionList.add(condition);
        }
        return vaildedConditionList.toArray(new Condition[]{});
    }
}
