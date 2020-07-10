package net.xicp.zyl_me.springframework.core.bean.factory;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;
import net.xicp.zyl_me.springframework.core.exception.BeanCreateFailException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultBeanFactory implements BeanFactory {
    @Override
    public FactoryBean createBean(BeanDefinition beanDefinition, ApplicationContext applicationContext) {
        Object o = null;
        try {
            Class<?> aClass = Class.forName(beanDefinition.getClazz());
            Constructor<?> constructor = aClass.getConstructor();
             o = constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new BeanCreateFailException("bean not created, reason:" + e.getMessage());
        }
        FactoryBean factoryBean = new FactoryBean(o, applicationContext);
        return factoryBean;
    }
}
