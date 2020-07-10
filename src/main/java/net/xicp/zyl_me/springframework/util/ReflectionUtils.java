package net.xicp.zyl_me.springframework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {
    public static boolean classEquals(Class<?> parent, Class<?> child) {
        if(child.equals(parent)){
            return true;
        }
        Class<?>[] interfaces = child.getInterfaces();
        boolean result = Arrays.stream(interfaces).anyMatch(e -> e.equals(parent));
        if(result){
            return result;
        }
        List<Class<?>> superClassList = getSuperClassList(child);
        boolean result2 = superClassList.stream().anyMatch(e -> e.equals(parent));
        if(result2){
            return result2;
        }
        return false;
    }

    public static List<Class<?>> getSuperClassList(Class<?> child){
        if(child == null){
            return new ArrayList<>();
        }
        List<Class<?>> classList = new ArrayList<>();
        Class<?> superclass = child.getSuperclass();
        if(superclass == null || superclass.equals(Object.class)){
            return new ArrayList<>();
        }
        classList.add(superclass);
        List<Class<?>> superClassList = getSuperClassList(superclass);
        if(superClassList.size() > 0) {
            classList.addAll(superClassList);
        }
        return classList;
    }
}
