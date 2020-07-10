package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();
}
