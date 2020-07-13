package net.xicp.zyl_me.springframework.core.context;

import lombok.Getter;
import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.bean.factory.BeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.DefaultBeanFactory;
import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.bean.processor.AOPBeanDefinationBeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.processor.BeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.processor.InstantiationAwareBeanPostProcessor;
import net.xicp.zyl_me.springframework.core.bean.reader.AnnotationBeanDefinitionReader;
import net.xicp.zyl_me.springframework.core.bean.reader.BeanDefinitionReader;
import net.xicp.zyl_me.springframework.core.bean.reader.XMLBeanDefinitionReader;
import net.xicp.zyl_me.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ClassPathXmlApplicationContext implements ApplicationContext {
	private Environment environment;

	@Override
	public Environment getEnvironment() {
		return environment;
	}

	List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
	List<InstantiationAwareBeanPostProcessor> instantiationAwareBeanPostProcessorList = new ArrayList<>();

	private void addDefaultBeanPostProcessors(List<BeanDefinition> beanDefinitionList){
		BeanDefinition beanDefinition = new BeanDefinition();
		beanDefinition.setClazz("net.xicp.zyl_me.springframework.core.bean.processor.AOPBeanDefinationBeanPostProcessor");
		beanDefinition.setBeanName("aopPostProcessor");
		beanDefinitionList.add(beanDefinition);
	}

	public ClassPathXmlApplicationContext(String configLocation) {
		environment = new Environment();
		environment.getContainer().put("applicationContext", new FactoryBean(this, this));
		XMLBeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(configLocation, environment);
		List<BeanDefinition> beanDefinitionList = beanDefinitionReader.readBeanDefinitions();
		environment.getContainer().put("beanDefinitionList", new FactoryBean(beanDefinitionList, this));
		AnnotationBeanDefinitionReader annotationBeanDefinitionReader = new AnnotationBeanDefinitionReader(((XMLBeanDefinitionReader)beanDefinitionReader).getComponentScans());
		beanDefinitionList.addAll(annotationBeanDefinitionReader.readBeanDefinitions());
		BeanFactory beanFactory = new DefaultBeanFactory();
		List<BeanDefinition> beanList = new ArrayList<>();
		addDefaultBeanPostProcessors(beanDefinitionList);
		//初始化BeanPostProcessor
		for (BeanDefinition beanDefinition : beanDefinitionList) {
			if(beanDefinition.isProcessor()){
				FactoryBean factoryBean = doCreateBean(beanDefinition, beanFactory);
				beanPostProcessorList.add((BeanPostProcessor) factoryBean.getInstance());
			}else{
				beanList.add(beanDefinition);
			}
		}
		//初始化普通Bean
		for (BeanDefinition beanDefinition : beanList) {
			doCreateBean(beanDefinition, beanFactory);
		}
	}

	public FactoryBean doCreateBean(final BeanDefinition beanDefinition, final BeanFactory beanFactory){
		//实例化
		FactoryBean bean = createBeanInstance(beanDefinition, beanFactory);
		//属性赋值
		populateBean(bean);
		//初始化阶段
		initializeBean(beanDefinition.getBeanName(), bean);
		return bean;
	}

	private FactoryBean createBeanInstance(BeanDefinition beanDefinition, final BeanFactory beanFactory) {
		AtomicReference<Object> wrapper = new AtomicReference<>();
		instantiationAwareBeanPostProcessorList.stream().forEach(e-> {
			try {
				Object o = e.postProcessBeforeInstantiation(Class.forName(beanDefinition.getClazz()), beanDefinition.getBeanName());
				wrapper.set(o);
			} catch (ClassNotFoundException classNotFoundException) {
				classNotFoundException.printStackTrace();
			}
		});
		FactoryBean bean;
		if(wrapper.get() != null){
			bean = new FactoryBean(wrapper.get(), this);
		}else {
			bean = beanFactory.createBean(beanDefinition, this);
		}
		environment.getContainer().put(beanDefinition.getBeanName(), bean);
		instantiationAwareBeanPostProcessorList.stream().forEach(e->{
			Object o = e.postProcessAfterInstantiation(bean.getInstance(), beanDefinition.getBeanName());
			bean.setInstance(o);
		});
		return bean;
	}


	private void populateBean(FactoryBean bean) {
		bean.propertiesSet();
	}

	public void initializeBean(String beanName, final FactoryBean bean){
		beanPostProcessorList.stream().forEach(e->{
			Object o = e.postProcessBeforeInitialization(beanName, bean.getInstance());
			bean.setInstance(o);
		});
		bean.afterPropertiesSet();
		beanPostProcessorList.stream().forEach(e->{
			Object o = e.postProcessAfterInitialization(beanName, bean.getInstance());
			bean.setInstance(o);
		});
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
