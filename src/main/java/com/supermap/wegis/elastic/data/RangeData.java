package com.supermap.wegis.elastic.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 范围参数类
 *
 * Created by lms on 2019/3/21 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeData {
    private Object value;
    private boolean include;
}
