package net.xicp.zyl_me.springframework.core.bean.processor;


import net.xicp.zyl_me.springframework.core.bean.AspectMethod;
import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.bean.annotation.Autowired;
import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;
import net.xicp.zyl_me.springframework.interceptor.DefaultInvocationHandler;
;


import java.lang.reflect.Proxy;
import java.util.*;

public class AOPBeanDefinationBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    private DefaultInvocationHandler wrapObject(Object aspectObj, String targetClassName, Class<?> targetClass, String aspectMethod, String targetMethodName, String beforeOrAfter) {
        DefaultInvocationHandler proxyHandler = null;
        try {
            String tmpBeanName = "proxyHandler$" + targetClassName;
            proxyHandler = (DefaultInvocationHandler) applicationContext.getBean(tmpBeanName);
            if (proxyHandler == null) {
                proxyHandler = new DefaultInvocationHandler(targetClass.newInstance());
                FactoryBean factoryBean = new FactoryBean(proxyHandler, applicationContext);
                applicationContext.getEnvironment().getContainer().put(tmpBeanName, factoryBean);
            }
            proxyHandler.setAspect(aspectObj.getClass());
            if (beforeOrAfter.equals("before")) {
                proxyHandler.addBeforeMethod(aspectObj.getClass().getMethod(aspectMethod, null));
            } else if(beforeOrAfter.equals("after")){
                proxyHandler.addAfterMethod(aspectObj.getClass().getMethod(aspectMethod, null));
            }
            proxyHandler.addTargetMethodName(targetMethodName);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return proxyHandler;
    }

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object instance) {
        List<BeanDefinition> beanDefinitionList = (List<BeanDefinition>) applicationContext.getBean("beanDefinitionList");
        Optional<BeanDefinition> first = beanDefinitionList.stream().filter(e -> e.getBeanName().equals(beanName)).findFirst();
        if(!first.isPresent()){
            return instance;
        }
        BeanDefinition beanDefinition =  first.get();
        List<AspectMethod> aspectBeforeMethodList = beanDefinition.getAspectBeforeMethodList();
        List<AspectMethod> aspectAfterMethodList = beanDefinition.getAspectBeforeMethodList();
        if(aspectAfterMethodList == null){
            aspectAfterMethodList = new ArrayList<>();
        }
        if(aspectBeforeMethodList == null){
            aspectBeforeMethodList = new ArrayList<>();
        }
        if(aspectAfterMethodList.size() == 0 && aspectBeforeMethodList.size() == 0){
            return instance;
        }
        DefaultInvocationHandler defaultInvocationHandler = null;
        for (AspectMethod aspectMethod : aspectBeforeMethodList) {
            FactoryBean bean = (FactoryBean) applicationContext.getBean(aspectMethod.getAspectBeanName());
            Class<?> aspectClass = bean.getClazz();
            wrapObject(bean.getInstance(), aspectClass.getName(), aspectMethod.getTargetClass(), aspectMethod.getTargetMethodName(),aspectMethod.getTargetMethodName(),  "before");
        }
        for (AspectMethod aspectMethod : aspectBeforeMethodList) {
            FactoryBean bean = (FactoryBean) applicationContext.getBean(aspectMethod.getAspectBeanName());
            Class<?> aspectClass = bean.getClazz();
            defaultInvocationHandler = wrapObject(bean.getInstance(), aspectClass.getName(), aspectMethod.getTargetClass(), aspectMethod.getTargetMethodName(), aspectMethod.getTargetMethodName(), "after");
        }
        Class<?> targetClass = instance.getClass();
        Class<?>[] interfaces = targetClass.getInterfaces();
        if (interfaces.length == 0) {
            interfaces = new Class[]{targetClass};
        }
        Object newProxyInstance = Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, defaultInvocationHandler);
        return newProxyInstance;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object instance) {
        return instance;
    }
}
