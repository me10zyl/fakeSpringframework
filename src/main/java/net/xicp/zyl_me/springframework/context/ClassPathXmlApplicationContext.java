package net.xicp.zyl_me.springframework.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xicp.zyl_me.springframework.interceptor.DefaultInvocationHandler;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class ClassPathXmlApplicationContext {
	private Map<String, Object> container = new HashMap<String, Object>();
	private String configLocation;

	public ClassPathXmlApplicationContext(String configLocation) throws IOException {
		super();
		this.configLocation = configLocation;
		File file = new File(configLocation);
		String line = null;
		StringBuilder xml = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			while ((line = br.readLine()) != null) {
				xml.append(line);
			}
			Document document = DocumentHelper.parseText(xml.toString());
			// List<Node> selectNodes =
			// document.selectNodes("/beans//aop:config/*");
			// for(Node node : selectNodes)
			// {
			// System.out.println(node.getName());
			// }
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Node> beans = root.selectNodes("//*[name()='bean']");
			Iterator<Node> iterator = beans.iterator();
			while (iterator.hasNext()) {
				Element bean = (Element) iterator.next();
				Attribute id = bean.attribute("id");
				Attribute _class = bean.attribute("class");
				Class<?> class_ = Class.forName(_class.getText());
				Object instance = class_.newInstance();
				container.put(id.getText(), instance);
			}
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
								Object aspectObj = container.get(ref.getText());
								DefaultInvocationHandler proxyHandler = (DefaultInvocationHandler) container.get("proxyHandler$" + targetClassName);
								if (proxyHandler == null) {
									proxyHandler = new DefaultInvocationHandler(targetClass.newInstance());
									container.put("proxyHandler$" + targetClassName, proxyHandler);
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
									interfaces = new Class[] { targetClass };
								}
								Object newProxyInstance = Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, proxyHandler);
								Set<String> keySet = container.keySet();
								Iterator<String> keySetIterator = keySet.iterator();
								while (keySetIterator.hasNext()) {
									String key = keySetIterator.next();
									Object containerObject = container.get(key);
									if (targetClass.isInstance(containerObject)) {
										container.put(key, newProxyInstance);
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
		} catch (IOException e) {
			// TODO: handle exception
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object getBean(String beanName) {
		return container.get(beanName);
	}
}
