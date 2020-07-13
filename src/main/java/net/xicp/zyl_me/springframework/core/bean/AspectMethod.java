package net.xicp.zyl_me.springframework.core.bean;

import lombok.Data;

@Data
public class AspectMethod {
    private Class<?> targetClass;
    private String aspectMethodName;
    private String targetMethodName;
    private String aspectBeanName;
}
