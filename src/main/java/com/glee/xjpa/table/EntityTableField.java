package com.glee.xjpa.table;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;

/**
 * @param isPriKey 是否为主键
 * @author GENSEN
 * @date 2022/11/18
 * @description
 */
public record EntityTableField(boolean isPriKey,
                               TableFieldMetadata tableFieldMetadata,
                               Field javaField,
                               Column column,
                               GeneratedValue generatedValue) {

}
