package com.glee.xjpa.repository.impl.proxy;

import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.invocation.GlobalRepositoryInvocationHandler;
import com.glee.xjpa.repository.invocation.RepositoryInvocation;
import com.glee.xjpa.repository.invocation.TransactionRepositoryInvocation;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
            if (isDefaultMethod(method)) {
                return invokeViaMethodHandles(proxy, method, args);
            }
            return method.invoke(standardJpaRepositoryProxy, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }finally {
            handleAfter(proxy, method, args);
        }
    }


//    /**
//     * Identifies a method as a default instance method.
//     */
//    public static boolean isDefault(Method method) {
//        // Default methods are public non-abstract, non-synthetic, and non-static instance methods
//        // declared in an interface.
//        // method.isDefault() is not sufficient for our usage as it does not check
//        // for synthetic methods. As a result, it picks up overridden methods as well as actual default
//        // methods.
//        final int SYNTHETIC = 0x00001000;
//        return ((method.getModifiers()
//                & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC | SYNTHETIC)) == Modifier.PUBLIC)
//                && method.getDeclaringClass().isInterface();
//    }

    /**
     * 判断 Method 是否为接口的 default 方法
     */
    public static boolean isDefaultMethod(Method method) {
        // 方式1：通过修饰符判断
        int modifiers = method.getModifiers();
        if (!Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)) {
            // 获取声明该方法的类
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.isInterface()) {
                // 接口中的非抽象、非静态方法就是 default 方法
                return true;
            }
        }

        // 方式2：更严格的检查（推荐）
        return method.isDefault();
    }


    /**
     * 使用 MethodHandles 调用 default 方法
     */
    public static Object invokeViaMethodHandles(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 创建 MethodHandle
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(declaringClass, lookup);
        MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        MethodHandle methodHandle = privateLookup.findSpecial(
                declaringClass,
                method.getName(),
                methodType,
                declaringClass
        );

        // 调用方法
        return methodHandle.bindTo(proxy).invokeWithArguments(args);
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
