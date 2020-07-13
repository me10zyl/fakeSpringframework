package net.xicp.zyl_me.springframework.core.bean;

import lombok.Data;
import net.xicp.zyl_me.springframework.core.bean.processor.BeanPostProcessor;

import java.util.Arrays;
import java.util.List;

@Data
public class BeanDefinition {
    private String beanName;
    private String clazz;
    private List<AspectMethod> aspectBeforeMethodList;
    private List<AspectMethod> aspectAfterMethodList;

    public boolean isProcessor() {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        Class<?>[] interfaces = aClass.getInterfaces();
        return Arrays.asList(interfaces).contains(BeanPostProcessor.class);
    }
}
