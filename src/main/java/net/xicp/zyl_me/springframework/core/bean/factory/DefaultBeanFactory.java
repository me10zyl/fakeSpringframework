package net.xicp.zyl_me.springframework.core.bean.factory;

import net.xicp.zyl_me.springframework.core.bean.BeanDefination;
import net.xicp.zyl_me.springframework.core.exception.BeanCreateFailException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultBeanFactory implements BeanFactory {
    @Override
    public FactoryBean createBean(BeanDefination beanDefination) {
        Object o = null;
        try {
            Class<?> aClass = Class.forName(beanDefination.getClazz());
            Constructor<?> constructor = aClass.getConstructor();
             o = constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new BeanCreateFailException("bean not created, reason:" + e.getMessage());
        }
        FactoryBean factoryBean = new FactoryBean(o);
        return factoryBean;
    }
}
