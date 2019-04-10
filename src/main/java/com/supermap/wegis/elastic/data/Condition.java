package com.supermap.wegis.elastic.data;

import lombok.*;
import com.supermap.wegis.elastic.data.Type.BoolType;
import com.supermap.wegis.elastic.data.Type.FieldType;
import com.supermap.wegis.elastic.data.Type.GeoRelationType;
import com.supermap.wegis.elastic.data.Type.SortType;
import com.supermap.wegis.elastic.data.Type.AggType;
import org.elasticsearch.common.geo.builders.ShapeBuilder;

/**
 * 查询条件父类
 *
 * Created by lms on 2019/3/16 15:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    // 查询字段
    private String field;
    // 查询字段类型
    private FieldType fieldType;

    /**
     * 一般查询条件类
     *
     */
    public static  class CommonCondition extends Condition{
        // 查询关系类型
        @Setter
        @Getter
        private BoolType boolType;

        // 查询字段值
        @Getter
        private Object[] values;

        public void setValues(Object... values) {
            this.values = values;
        }

        public CommonCondition(String field, FieldType fieldType, BoolType boolType, Object...values){
            super(field, fieldType);
            this.boolType = boolType;
            this.values = values;
        }

        public CommonCondition(String field, FieldType fieldType, Object...values){
            super(field, fieldType);
            this.boolType = BoolType.MUST;
            this.values = values;
        }
    }

    /**
     * 空间查询条件类
     *
     */
    @Data
    public static class GeoCondition extends Condition{
        // 查询关系类型
        private BoolType boolType;
        // 空间对象
        private ShapeBuilder shape;
        // 空间查询类型
        private GeoRelationType geoRelationType;

        public GeoCondition(String field, ShapeBuilder shape, GeoRelationType geoRelationType){
            super(field, FieldType.GEO);
            this.boolType = BoolType.MUST;
            this.shape = shape;
            this.geoRelationType = geoRelationType;
        }

        public GeoCondition(String field, BoolType boolType, ShapeBuilder shape, GeoRelationType geoRelationType){
            super(field, FieldType.GEO);
            this.boolType = boolType;
            this.shape = shape;
            this.geoRelationType = geoRelationType;
        }
    }

    /**
     * 聚合查询条件类
     *
     */
    @Data
    public static class AggCondition extends Condition{
        // 聚合类型
        private AggType aggType;

        public AggCondition(String field, AggType aggType){
            super(field, FieldType.AGGS);
            this.aggType = aggType;
        }
    }

    /**
     * 排序字段查询类
     *
     */
    @Data
    public static class SortCondition extends  Condition{
        // 排序类型
        private SortType sortType;

        public SortCondition(String field, SortType sortType){
            super(field, FieldType.SORT);
            this.sortType = sortType;
        }

    }

    /**
     * 范围查询条件类
     *
     */
    @Data
    public static  class RangeCondition extends Condition{
        // 查询关系类型
        private BoolType boolType;
        // 最小值
        private RangeData rangeMin;
        // 最大值
        private RangeData rangeMax;

        public RangeCondition(String field, FieldType fieldType, RangeData rangeMin,RangeData rangeMax){
            super(field, fieldType);
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        }
    }
}
