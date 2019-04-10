package com.supermap.wegis.elastic.utils;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.builders.CircleBuilder;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.unit.DistanceUnit;
import org.locationtech.jts.geom.Coordinate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ES工具类
 *
 * Created by lms on 2019/3/21 15:23
 */
public class ESUtils {

    /**
     * 获取圆查询对象
     *
     * @param lon 经度
     * @param lat 纬度
     * @param radis 半径
     * @param distanceUnit 半径单位
     * @return
     */
    public static CircleBuilder getCircle(double lon, double lat, double radis, DistanceUnit distanceUnit){
        CircleBuilder circleBuilder = new CircleBuilder();
        circleBuilder.center(lon,lat);
        circleBuilder.radius(radis, distanceUnit);
        return circleBuilder;
    }


    /**
     * 获取范围
     *
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @return
     */
    public static EnvelopeBuilder getEnvelope(double left, double bottom, double right, double top){
        return new EnvelopeBuilder(new Coordinate(left, top), new Coordinate(right, bottom));
    }


    /**
     * 获取面对象
     *
     * @param pointList 面对象点串
     * @return
     */
    public static PolygonBuilder getPolygon(List<GeoPoint> pointList){
        List<Coordinate> coordinates =  pointList.stream().map(e -> new Coordinate(e.lon(),e.getLat())).collect(Collectors.toList());
        return new PolygonBuilder(new CoordinatesBuilder().coordinates(coordinates));
    }
}
