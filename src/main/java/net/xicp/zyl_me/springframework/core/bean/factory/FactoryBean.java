package net.xicp.zyl_me.springframework.core.bean.factory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class FactoryBean implements Bean{
    public FactoryBean(Object instance) {
        this.instance = instance;
    }
    @Getter
    @Setter
    private Object instance;
    public void afterPropertiesSet(){

    }
}
