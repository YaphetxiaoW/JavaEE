package Aopproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactoryBean implements InvocationHandler {

    private String proxyInterfaces;
    private Object target;
    private String interceptorNames;

    public void setFactory(BeanFactory factory) {
        this.factory = factory;
    }

    private BeanFactory factory;

    public BeanFactory getFactory() {
        return factory;
    }

    public String getProxyInterfaces() {
        return proxyInterfaces;
    }

    public void setProxyInterfaces(String proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getInterceptorNames() {
        return interceptorNames;
    }

    public void setInterceptorNames(String interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(interceptorNames!=null) {
            Object advice = factory.getBean(interceptorNames);
            if ( advice instanceof MethodBeforeAdvice) {
                ((MethodBeforeAdvice)advice).before(method,args,target);
                Object result = method.invoke(target,args);
                return result;
            }
        }
        Object result = method.invoke(target,args);
        return result;
    }

    @Override
    public String toString() {
        return "ProxyFactoryBean{" +
                "proxyInterfaces='" + proxyInterfaces + '\'' +
                ", target=" + target +
                ", interceptorNames='" + interceptorNames + '\'' +
                '}';
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                target.getClass().getInterfaces(),
                this);
    }
}
