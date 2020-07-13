package net.xicp.zyl_me.springframework.core.bean.processor;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName);
    Object postProcessAfterInstantiation(Object bean, String beanName);
}
