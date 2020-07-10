package net.xicp.zyl_me.springframework.core.context;

import net.xicp.zyl_me.springframework.core.bean.BeanDefination;
import net.xicp.zyl_me.springframework.core.bean.factory.BeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.DefaultBeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.bean.processor.AOPBeanDefinationBeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.processor.BeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.reader.BeanDefinationReader;
import net.xicp.zyl_me.springframework.core.bean.reader.XMLBeanDefinationReader;

import java.util.ArrayList;
import java.util.List;

public class ClassPathXmlApplicationContext implements ApplicationContext {
	private Environment environment;

	private void addDefaultBeanPostProcessors(List<BeanPostProcessor> list){
		list.add(new AOPBeanDefinationBeanPostProcessor());
	}

	public ClassPathXmlApplicationContext(String configLocation) {
		environment = new Environment();
		BeanDefinationReader beanDefinationReader = new XMLBeanDefinationReader(configLocation, environment);
		List<BeanDefination> beanDefinationList = beanDefinationReader.readBeanDefinations();
		BeanFactory beanFactory = new DefaultBeanFactory();
		List<BeanDefination> beanList = new ArrayList<>();
		List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
		addDefaultBeanPostProcessors(beanPostProcessorList);
		for (BeanDefination beanDefination : beanDefinationList) {
			if(beanDefination.isProcessor()){
				FactoryBean bean = beanFactory.createBean(beanDefination);
				beanPostProcessorList.add((BeanPostProcessor)bean.getInstance());
			}else{
				beanList.add(beanDefination);
			}
		}
		for (BeanDefination beanDefination : beanList) {
			FactoryBean bean = beanFactory.createBean(beanDefination);
			for(BeanPostProcessor e : beanPostProcessorList){
				bean.setInstance(e.postProcessBeforeInitialization(beanDefination.getBeanName(), bean.getInstance()));
			}
			bean.afterPropertiesSet();
			for(BeanPostProcessor e : beanPostProcessorList){
				bean.setInstance(e.postProcessAfterInitialization(beanDefination.getBeanName(), bean.getInstance()));
			}
			environment.getContainer().put(beanDefination.getBeanName(), bean);
		}
	}

	public Object getBean(String beanName) {
		return ((FactoryBean)environment.getContainer().get(beanName)).getInstance();
	}
}
