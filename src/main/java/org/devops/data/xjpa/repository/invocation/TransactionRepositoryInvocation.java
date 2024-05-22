package org.devops.data.xjpa.repository.invocation;

import org.springframework.transaction.TransactionStatus;

import java.lang.reflect.Method;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description default
 */
public class TransactionRepositoryInvocation implements RepositoryInvocation{
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private final TransactionStatus transactionStatus;

    public TransactionRepositoryInvocation(Object proxy, Method method, Object[] args, TransactionStatus transactionStatus) {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
        this.transactionStatus = transactionStatus;
    }


    @Override
    public Object callTarget() {
        return proxy;
    }

    @Override
    public Method callMethod() {
        return method;
    }

    @Override
    public Object[] callArgs() {
        return args;
    }

    @Override
    public boolean isTransactionHandled() {
        return transactionStatus != null;
    }

}
