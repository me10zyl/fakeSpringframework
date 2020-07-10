package net.xicp.zyl_me.springframework.core.bean.processor;


import net.xicp.zyl_me.springframework.core.bean.factory.FactoryBean;
import net.xicp.zyl_me.springframework.core.context.ApplicationContext;
import net.xicp.zyl_me.springframework.core.context.Environment;
import net.xicp.zyl_me.springframework.interceptor.DefaultInvocationHandler;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;;


import javax.xml.soap.Node;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AOPBeanDefinationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object instance) {
        return instance;
    }

    private void handleAOPDefination(Document document, ApplicationContext applicationContext, Environment environment) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        List<Node> aspect = document.selectNodes("/*[name()='beans']//*[name()='aop:config']/*");
        Iterator<Node> iteratorAspect = aspect.iterator();
        while (iteratorAspect.hasNext()) {
            Element aspect_ = (Element) iteratorAspect.next();
            Attribute ref = ((Element) aspect_).attribute("ref");
            String beanName = ref.getText();
            @SuppressWarnings("unchecked")
            List<Node> aopAdvice = new ArrayList<Node>();
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
                        Class<?> targetClass = Class.forName(targetClassName);
                        Pattern pMethod = Pattern.compile("(\\w+\\.)+(\\w+)");
                        Matcher mathcer2 = pMethod.matcher(targetAOP);
                        if (mathcer2.find()) {
                            String targetMethodName = mathcer2.group(2);
                            Object aspectObj = applicationContext.getBean(ref.getText());
                            String tmpBeanName = "proxyHandler$" + targetClassName;
                            DefaultInvocationHandler proxyHandler = (DefaultInvocationHandler) applicationContext.getBean(tmpBeanName);
                            if (proxyHandler == null) {
                                proxyHandler = new DefaultInvocationHandler(targetClass.newInstance());
                                FactoryBean factoryBean = new FactoryBean(proxyHandler, applicationContext);
                                environment.getContainer().put(tmpBeanName, factoryBean);
                            }
                            proxyHandler.setAspect(aspectObj.getClass());
                            if (aopBeforeOrAfter.getName().equals("before")) {
                                proxyHandler.addBeforeMethod(aspectObj.getClass().getMethod(aspectMethod.getText(), null));
                            } else if (aopBeforeOrAfter.getName().equals("after")) {
                                proxyHandler.addAfterMethod(aspectObj.getClass().getMethod(aspectMethod.getText(), null));
                            }
                            proxyHandler.addTargetMethodName(targetMethodName);
                            Class<?>[] interfaces = targetClass.getInterfaces();
                            if (interfaces.length == 0) {
                                interfaces = new Class[]{targetClass};
                            }
                            Object newProxyInstance = Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, proxyHandler);
                            Set<String> keySet = environment.getContainer().keySet();
                            Iterator<String> keySetIterator = keySet.iterator();
                            while (keySetIterator.hasNext()) {
                                String key = keySetIterator.next();
                                Object containerObject = applicationContext.getBean(key);
                                if (targetClass.isInstance(containerObject)) {
                                    FactoryBean factoryBean = new FactoryBean(newProxyInstance, applicationContext);
                                    environment.getContainer().put(key, factoryBean);
                                }
                            }
                        }
                    }
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object instance) {
        return instance;
    }
}
