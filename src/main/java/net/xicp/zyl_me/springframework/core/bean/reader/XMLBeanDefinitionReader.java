package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.AspectMethod;
import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;
import net.xicp.zyl_me.springframework.core.context.Environment;
import net.xicp.zyl_me.springframework.interceptor.DefaultInvocationHandler;
import net.xicp.zyl_me.springframework.util.ReflectionUtils;
import org.dom4j.*;

import java.io.*;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {

    private String configLocation;
    private Environment environment;
    private Document document;

    public XMLBeanDefinitionReader(String configLocation, Environment environment) {
        this.configLocation = configLocation;
        this.environment = environment;
    }

    public List<String> getComponentScans(){
        List<String> result = new ArrayList<>();
        List<Element> list = document.selectNodes("//*[name()='context:component-scan']");
        for (Element ele : list) {
            Attribute attribute = ele.attribute("base-package");
            result.add(attribute.getText());
        }
        return result;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> list = null;
        File file = new File(configLocation);
        String line = null;
        StringBuilder xml = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null) {
                xml.append(line);
            }
            Document document = DocumentHelper.parseText(xml.toString());
            this.document = document;
            environment.setXmlDocument(document);
            list = handleBeanDefinition(document);
            handleAOPDefination(document, list);
        } catch (IOException | DocumentException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return list;
    }

    private void handleAOPDefination(Document document, List<BeanDefinition> list) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        List<Node> aspect = document.selectNodes("/*[name()='beans']//*[name()='aop:config']/*");
        Iterator<Node> iteratorAspect = aspect.iterator();
        while (iteratorAspect.hasNext()) {
            Element aspect_ = (Element) iteratorAspect.next();
            Attribute ref = ((Element) aspect_).attribute("ref");
            @SuppressWarnings("unchecked")
            List<Node> aopAdvice = new ArrayList<>();
            List<Node> aopBefore = aspect_.selectNodes("/beans//aop:config/aop:aspect/aop:before");
            List<Node> aopAfter = aspect_.selectNodes("/beans//aop:config/aop:aspect/aop:after");
            aopAdvice.addAll(aopBefore);
            aopAdvice.addAll(aopAfter);
            for (Node node : aopAdvice) {
                Element aopBeforeOrAfter = (Element) node;
                Attribute pointcut_ = aopBeforeOrAfter.attribute("pointcut");
                Attribute aspectMethod = aopBeforeOrAfter.attribute("method");
                try {
                    String targetAOP = pointcut_.getText();
                    Pattern pClassName = Pattern.compile("(\\w+\\.)+\\w+(?=\\.)");
                    Matcher matcher = pClassName.matcher(targetAOP);
                    if (matcher.find()) {
                        String targetClassName = matcher.group();
                        Pattern pMethod = Pattern.compile("(\\w+\\.)+(\\w+)");
                        Matcher mathcer2 = pMethod.matcher(targetAOP);
                        if (mathcer2.find()) {
                            Optional<BeanDefinition> beanDefinitionOptional = list.stream().filter(e -> e.getClazz().equals(targetClassName)).findFirst();
                            BeanDefinition beanDefinition = beanDefinitionOptional.get();
                            List<AspectMethod> beforeList = new ArrayList<>();
                            List<AspectMethod> afterList = new ArrayList<>();
                            String targetMethodName = mathcer2.group(2);
                            Class<?> targetClass = Class.forName(targetClassName);
                            if (aopBeforeOrAfter.getName().equals("before")) {
                                AspectMethod as = new AspectMethod();
                                as.setAspectBeanName(ref.getText());
                                as.setAspectMethodName(aspectMethod.getText());
                                as.setTargetClass(targetClass);
                                as.setTargetMethodName(targetMethodName);
                                beforeList.add(as);
                            } else if (aopBeforeOrAfter.getName().equals("after")) {
                                AspectMethod as = new AspectMethod();
                                as.setAspectBeanName(ref.getText());
                                as.setAspectMethodName(aspectMethod.getText());
                                as.setTargetClass(targetClass);
                                as.setTargetMethodName(targetMethodName);
                            }
                            beanDefinition.setAspectBeforeMethodList(beforeList);
                            beanDefinition.setAspectAfterMethodList(afterList);
                        }
                    }
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private List<BeanDefinition> handleBeanDefinition(Document document) {
        List<BeanDefinition> list = new ArrayList<>();
        Element root = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Node> beans = root.selectNodes("//*[name()='bean']");
        Iterator<Node> iterator = beans.iterator();
        while (iterator.hasNext()) {
            Element bean = (Element) iterator.next();
            Attribute id = bean.attribute("id");
            Attribute _class = bean.attribute("class");
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanName(id.getText());
            beanDefinition.setClazz(_class.getText());
            list.add(beanDefinition);
        }
        return list;
    }

}
