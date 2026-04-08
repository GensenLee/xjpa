package com.glee.xjpa.io;

import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description 执行请求
 */
public interface QueryRequest {

    String getSqlTemplate();

    Map<Integer, Object> getParameters();

}
