package com.glee.xjpa.sql.result;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author GENSEN
 * @date 2026/4/1
 * @description 结果转换
 */
public interface ResultConverter<T> {

    List<T> convert(ResultSet resultSet);

}
