package net.xicp.zyl_me.springframework.core.bean.factory;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;

public interface BeanFactory {
    FactoryBean createBean(BeanDefinition beanDefinition,ApplicationContext applicationContext);
}
