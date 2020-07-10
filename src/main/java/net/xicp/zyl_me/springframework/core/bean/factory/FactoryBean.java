package net.xicp.zyl_me.springframework.core.bean.factory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.xicp.zyl_me.springframework.core.bean.annotation.Autowired;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;

import java.lang.reflect.Field;

public class FactoryBean implements Bean{
    public FactoryBean(Object instance, ApplicationContext applicationContext) {
        this.instance = instance;
        this.applicationContext = applicationContext;
    }
    @Getter
    @Setter
    private Object instance;
    private ApplicationContext applicationContext;

    public Class<?> getClazz(){
        return instance.getClass();
    }

    public void propertiesSet(){
        parseAnnotations(getClazz());
    }


    private void parseAnnotations(Class<?> aClass){
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(declaredField.isAnnotationPresent(Autowired.class)){
                declaredField.setAccessible(true);
                try {
                    declaredField.set(instance, applicationContext.getBean(declaredField.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void afterPropertiesSet(){

    }
}
