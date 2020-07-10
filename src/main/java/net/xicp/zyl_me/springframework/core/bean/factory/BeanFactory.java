package net.xicp.zyl_me.springframework.core.bean.factory;

import net.xicp.zyl_me.springframework.core.bean.BeanDefination;

public interface BeanFactory {
    FactoryBean createBean(BeanDefination beanDefination);
}
