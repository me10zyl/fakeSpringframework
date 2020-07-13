package net.xicp.zyl_me.springframework.core.context;

public interface ApplicationContext {
    Environment getEnvironment();
    Object getBean(String beanName);
    Object getBean(Class<?> beanClass);
}
