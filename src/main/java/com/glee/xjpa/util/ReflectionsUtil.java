package com.glee.xjpa.util;

import com.glee.xjpa.annotation.SkipRepositoryScan;
import com.glee.xjpa.repository.StandardJpaRepository;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/12/5
 * @description Reflections
 */
@SuppressWarnings("rawtypes")
public class ReflectionsUtil {


    /**
     * @param baseRepositoryPackage
     * @return
     */
    public static Set<Class<? extends StandardJpaRepository>> getSubTypesOfStandardJpaRepository(String baseRepositoryPackage) {
        Reflections reflections = new Reflections(baseRepositoryPackage);
        return reflections.getSubTypesOf(StandardJpaRepository.class)
                .stream()
                // SkipRepositoryScan 为内部实现类
                .filter(clazz -> !clazz.isAnnotationPresent(SkipRepositoryScan.class))
                .collect(Collectors.toSet());
    }

}
