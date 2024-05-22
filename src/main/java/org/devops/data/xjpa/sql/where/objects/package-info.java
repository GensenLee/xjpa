/**
 * @author GENSEN
 * @date 2022/11/23
 * @description 内部条件构建模型
 */
package org.devops.data.xjpa.sql.where.objects;

/*
*
* where 对象有三种形式
* 单个条件：a = b
* 条件集：a = b or （c = d and f > e）
* 文本条件：’data_format(created_time,"%d-%m-%d")="2022-12-01"‘
*
* */