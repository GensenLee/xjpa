package org.devops.data.xjpa.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.exception.CommonException;
import org.devops.core.utils.exception.CommonRuntimeException;
import org.devops.data.xjpa.configuration.RepositoryProperties;
import org.devops.data.xjpa.exception.XjpaException;
import org.devops.data.xjpa.lifecycle.Disposable;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author GENSEN
 * @date 2022/12/6
 * @description dispose
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class DisposeFacadeCreatedHandle implements FacadeCreatedHandle {


    @Override
    public StandardJpaRepositoryFacade handle(StandardJpaRepositoryFacade target) {

        Class[] argumentTypes = Arrays.asList(Class.class, Class.class, RepositoryDelegateHolder.class).toArray(new Class[0]);

        Object[] arguments = Arrays.asList(target.keyType, target.entityType, target.delegate).toArray();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(StandardJpaRepositoryFacade.class);
        enhancer.setCallback(new DisposeDelegateMethodInterceptor(target));
        return (StandardJpaRepositoryFacade) enhancer.create(argumentTypes, arguments);
    }


    public static class DisposeDelegateMethodInterceptor implements MethodInterceptor {

        private final StandardJpaRepositoryFacade target;

        public DisposeDelegateMethodInterceptor(StandardJpaRepositoryFacade target) {
            this.target = target;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

            Object result = null;
            try {
                result = methodProxy.invokeSuper(o, objects);
            } catch (Exception e) {
                if (e instanceof CommonException || e instanceof CommonRuntimeException ||
                        e instanceof XjpaException) {
                    throw e;
                }
                try {
                    logContext(e);
                } catch (Exception ignored) {}

                log.error("xjpa inner exception", e);
                throw e;
            } finally {
                if (method.isAnnotationPresent(DisposeAfterReturn.class)) {
//                Object actual = o;
//                if (o instanceof Facade) {
//                    actual = ((Facade) o).getActual();
//                }
//                if (actual instanceof RepositoryContext) {
//                    ((RepositoryContext<?, ?>) actual).dispose();
//                }
                    log.trace("dispose start");
                    if (o instanceof Disposable) {
                        ((Disposable) o).dispose();
                    }
                    log.trace("dispose end");
                }
            }
            if (method.isAnnotationPresent(ReturnThis.class)) {
                return o;
            }


            return result;

        }

        private void logContext(Exception e) {
            Object actual = target.getActual();

            RepositoryContextAttribute repositoryContextAttribute =
                    (RepositoryContextAttribute) getObjectField(actual, "repositoryContextAttribute");
            StringBuilder stringBuilder = new StringBuilder(e.getClass().getName()).append("exception context attribute >>> ");
            if (repositoryContextAttribute != null) {
                for (String key : repositoryContextAttribute.keySet()) {
                    stringBuilder.append("[key=").append(key).append(",value=").append(repositoryContextAttribute.getAttribute(key)).append("] ");
                }
            }
            log.error(stringBuilder.toString());

            RepositoryProperties repositoryProperties = (RepositoryProperties) getObjectField(actual, "repositoryProperties");
            if (repositoryProperties != null) {
                String msg = e.getClass().getName() + "exception context properties >>> " +
                        "repositoryProperties" + ":" + repositoryProperties;
                log.error(msg);
            }

        }
    }

    private static Object getObjectField(Object actual, String fieldName) {
        Field field = ReflectionUtils.findField(actual.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        Object value = null;
        try {
            value = field.get(actual);
        } catch (IllegalAccessException ignored) {}
        finally {
            field.setAccessible(false);
        }
        return value;
    }
}
