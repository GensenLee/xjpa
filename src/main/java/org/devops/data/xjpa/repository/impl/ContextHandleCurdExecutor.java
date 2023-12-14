package org.devops.data.xjpa.repository.impl;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description context处理
 */
@SuppressWarnings("rawtypes")
public class ContextHandleCurdExecutor implements CurdExecutor{

    private final RepositoryContext context;

    public ContextHandleCurdExecutor(RepositoryContext context) {
        this.context = context;
    }

    @Override
    public <T> T execute(CurdCommand curdCommand) {
        try {
            return curdCommand.execute();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            context.dispose();
        }
    }
}
