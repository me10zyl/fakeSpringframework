package net.xicp.zyl_me.springframework.core.bean.processor;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(String beanName, Object instance);
    Object postProcessAfterInitialization(String beanName, Object instance);
}
