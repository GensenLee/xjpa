package org.devops.data.xjpa.lifecycle;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description Repository注册验证
 */
public interface RepositoryRegisterValidator {

    /**
     * @param repositoryType
     */
    void validate(Class<?> repositoryType);

}
