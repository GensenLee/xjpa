package org.devops.data.xjpa.repository.impl.proxy;

import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.invocation.GlobalRepositoryInvocationHandler;
import org.devops.data.xjpa.repository.invocation.RepositoryInvocation;
import org.devops.data.xjpa.repository.invocation.TransactionRepositoryInvocation;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 方法代理
 */
public class RepositoryInvocationHandler<K extends Serializable, V> implements InvocationHandler {

    RepositoryInvocationHandler(Class<?> targetBeanType, StandardJpaRepository<K, V> standardJpaRepositoryProxy) {
        this.targetBeanType = targetBeanType;
        this.standardJpaRepositoryProxy = standardJpaRepositoryProxy;
    }

    private final Class<?> targetBeanType;

    private final StandardJpaRepository<K, V> standardJpaRepositoryProxy;

    private final Map<Method, MethodHandle> methodHandleCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        handleBefore(proxy, method, args);


        try {
            if (isDefault(method)) {
                MethodHandle defaultMethodHandle = methodHandleCache.computeIfAbsent(method, key -> {
                    MethodHandle methodHandle = MethodHandlesUtil.getSpecialMethodHandle(method);
                    return methodHandle.bindTo(proxy);
                });
                return defaultMethodHandle.invokeWithArguments(args);
            }
            return method.invoke(standardJpaRepositoryProxy, args);
        } /*catch (IllegalAccessException | IllegalArgumentException e) {
            return MethodHandles
                    .lookup()
                    .in(targetBeanType)
                    .unreflectSpecial(method, targetBeanType)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } */catch (InvocationTargetException e) {
            throw e.getTargetException();
        }finally {
            handleAfter(proxy, method, args);
        }
    }


    /**
     * Identifies a method as a default instance method.
     */
    public static boolean isDefault(Method method) {
        // Default methods are public non-abstract, non-synthetic, and non-static instance methods
        // declared in an interface.
        // method.isDefault() is not sufficient for our usage as it does not check
        // for synthetic methods. As a result, it picks up overridden methods as well as actual default
        // methods.
        final int SYNTHETIC = 0x00001000;
        return ((method.getModifiers()
                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC | SYNTHETIC)) == Modifier.PUBLIC)
                && method.getDeclaringClass().isInterface();
    }

    /**
     * @param proxy
     * @param method
     * @param args
     */
    private void handleBefore(Object proxy, Method method, Object[] args) {
        GlobalRepositoryInvocationHandler.remove();
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        } catch (NoTransactionException ignored) {}

        RepositoryInvocation invocation = new TransactionRepositoryInvocation(proxy, method, args, transactionStatus);

        GlobalRepositoryInvocationHandler.set(invocation);

//        Object actual = ((RepositoryInvocationHandler) Proxy.getInvocationHandler(proxy)).standardJpaRepositoryProxy;
//
//        if (actual instanceof Facade) {
//            actual = ((Facade) actual).getActual();
//        }
//
//        if (actual instanceof RepositoryContextObservable) {
//            ((RepositoryContextObservable) actual).register(new RepositoryContextObserver() {
//                @Override
//                public void onDispose(RepositoryContext context) {
//                    GlobalRepositoryInvocationHandler.remove();
//                }
//
//                @Override
//                public void onClose(RepositoryContext context) {
//                    GlobalRepositoryInvocationHandler.remove();
//                }
//            });
//        }

    }

    /**
     * @param proxy
     * @param method
     * @param args
     */
    public void handleAfter(Object proxy, Method method, Object[] args) {
//        try {
//            Method declaredMethod = standardJpaRepositoryProxy.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
//            if (declaredMethod.isAnnotationPresent(Dispose.class)) {
//                if (standardJpaRepositoryProxy instanceof Disposable) {
//                    ((Disposable) standardJpaRepositoryProxy).dispose();
//                }
//            }
//        } catch (NoSuchMethodException ignored) {}
    }

    public StandardJpaRepository<K, V> getStandardJpaRepository() {
        return standardJpaRepositoryProxy;
    }
}
