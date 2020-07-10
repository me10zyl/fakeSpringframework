package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.util.ComponentScanner;

import java.util.ArrayList;
import java.util.List;

public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {

    private List<String> packageList;
    private ComponentScanner componentScanner = new ComponentScanner();

    public AnnotationBeanDefinitionReader(List<String> packageList) {
        this.packageList = packageList;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {

        List<BeanDefinition> beanDefinition = new ArrayList<>();
        for (String packagePath : packageList) {
            beanDefinition.addAll(componentScanner.doScan(packagePath));
        }
        return beanDefinition;
    }


}
