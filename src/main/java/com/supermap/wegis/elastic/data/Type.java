package com.supermap.wegis.elastic.data;

/**
 * 类型
 *
 * Created by lms on 2019/3/13 15:08
 */
public interface Type{

    String getName();

    /**
     * 字段类型
     */
    public enum FieldType implements Type{

        MATCH("匹配查询字段"), TERM("准确值查询字段"), RANGE("范围查询字段"), SUGGEST("搜索建议查询字段"),
        AGGS("聚合查询字段"), SORT("排序字段"), GEO("空间查询字段");

        private String name;

        FieldType(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }

    /**
     *查询条件关系类型
     */
    public enum BoolType implements Type{
        SHOULD("或"), MUST("且"), MUST_NOT("非");

        private String name;

        private BoolType(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }

    }

    /**
     * 排序类型
     *
     */
    public enum SortType implements Type{
        ASC("升序"), DESC("降序");

        private String name;

        private SortType(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    /**
     * 空间关系类型
     *
     */
    public enum GeoRelationType implements Type{
        WITHIN("包含"), DISJOINT("不相交"), INTERSECTS("相交") ;

        private String name;

        private GeoRelationType(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    /**
     *统计类型
     *
     */
    public enum AggType implements  Type{
        TERMS("在该字段上分组"), AVG("对该字段求平均值"), MAX("对该字段取最大值"),
        MIN("对该字段取最小值") ,COUNT("对该字段求和");

        private String name;

        private AggType(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }
}

