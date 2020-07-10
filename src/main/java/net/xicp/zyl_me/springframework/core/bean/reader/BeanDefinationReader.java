package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.BeanDefination;

import java.util.List;

public interface BeanDefinationReader {
    List<BeanDefination> readBeanDefinations();
}
