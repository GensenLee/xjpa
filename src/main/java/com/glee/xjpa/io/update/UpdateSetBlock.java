package com.glee.xjpa.io.update;

/**
 * @author GENSEN
 * @date 2026/1/21
 * @description
 */
public interface UpdateSetBlock {

    String toUpdateTemplateClause();

    Object getParameter();

}
