package net.xicp.zyl_me.springframework.core.context;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.bean.factory.BeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.DefaultBeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.bean.processor.AOPBeanDefinationBeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.processor.BeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.reader.AnnotationBeanDefinitionReader;
import net.xicp.zyl_me.springframework.core.bean.reader.BeanDefinitionReader;
import net.xicp.zyl_me.springframework.core.bean.reader.XMLBeanDefinitionReader;
import net.xicp.zyl_me.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassPathXmlApplicationContext implements ApplicationContext {
	private Environment environment;

	private void addDefaultBeanPostProcessors(List<BeanPostProcessor> list){
		list.add(new AOPBeanDefinationBeanPostProcessor());
	}

	public ClassPathXmlApplicationContext(String configLocation) {
		environment = new Environment();
		BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(configLocation, environment);
		List<BeanDefinition> beanDefinitionList = beanDefinitionReader.readBeanDefinitions();
		AnnotationBeanDefinitionReader annotationBeanDefinitionReader = new AnnotationBeanDefinitionReader(((XMLBeanDefinitionReader)beanDefinitionReader).getComponentScans());
		beanDefinitionList.addAll(annotationBeanDefinitionReader.readBeanDefinitions());
		BeanFactory beanFactory = new DefaultBeanFactory();
		List<BeanDefinition> beanList = new ArrayList<>();
		List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
		addDefaultBeanPostProcessors(beanPostProcessorList);
		for (BeanDefinition beanDefinition : beanDefinitionList) {
			if(beanDefinition.isProcessor()){
				FactoryBean bean = beanFactory.createBean(beanDefinition, this);
				beanPostProcessorList.add((BeanPostProcessor)bean.getInstance());
			}else{
				beanList.add(beanDefinition);
			}
		}
		for (BeanDefinition beanDefinition : beanList) {
			FactoryBean bean = beanFactory.createBean(beanDefinition, this);
			environment.getContainer().put(beanDefinition.getBeanName(), bean);
			for(BeanPostProcessor e : beanPostProcessorList){
				bean.setInstance(e.postProcessBeforeInitialization(beanDefinition.getBeanName(), bean.getInstance()));
			}
			bean.propertiesSet();
			bean.afterPropertiesSet();
			for(BeanPostProcessor e : beanPostProcessorList){
				bean.setInstance(e.postProcessAfterInitialization(beanDefinition.getBeanName(), bean.getInstance()));
			}
		}
	}

	public Object getBean(String beanName) {
		FactoryBean factoryBean = (FactoryBean) environment.getContainer().get(beanName);
		if(factoryBean == null){
			return null;
		}
		return factoryBean.getInstance();
	}

	public Object getBean(Class<?> beanClass){
		Optional<FactoryBean> first = environment.getContainer().entrySet().stream().filter(e -> {
			return ReflectionUtils.classEquals(beanClass, e.getValue().getClazz());
		}).map(e->e.getValue()).findFirst();
		if(first.isPresent()){
			return first.get().getInstance();
		}
		return null;
	}
}
