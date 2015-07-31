package net.xicp.zyl_me.springframework.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultInvocationHandler implements InvocationHandler {
	private Object target;
	private List<String> targetMethods = new ArrayList<String>();
	private List<Method> beforeMethods = new ArrayList<Method>(), afterMethods = new ArrayList<Method>();
	private Class<?> aspect;
	private Object aspectInstance;

	public DefaultInvocationHandler(Object target) {
		this.target = target;
	}

	public void addAfterMethod(Method afterMethod) {
		this.afterMethods.add(afterMethod);
	}

	public void addBeforeMethod(Method beforeMethod) {
		this.beforeMethods.add(beforeMethod);
	}

	public void addTargetMethodName(String targetMethod) {
		this.targetMethods.add(targetMethod);
	}

	public List<Method> getAfterMethods() {
		return afterMethods;
	}

	public Class<?> getAspect() {
		return aspect;
	}

	public Object getAspectInstance() {
		return aspectInstance;
	}

	public List<Method> getBeforeMethods() {
		return beforeMethods;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		boolean containThisMethod = false;
		for (String m : targetMethods) {
			if (m.equals(method.getName())) {
				containThisMethod = true;
				break;
			}
		}
		if (containThisMethod) {
			beforeMethod();
			method.invoke(target, args);
			afterMethod();
		} else {
			method.invoke(target, args);
		}
		return null;
	}

	public void setAspect(Class<?> aspect) throws InstantiationException, IllegalAccessException {
		this.aspect = aspect;
		this.aspectInstance = this.aspect.newInstance();
	}

	public void setAspectInstance(Object aspectInstance) {
		this.aspectInstance = aspectInstance;
	}

	private void afterMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m : afterMethods) {
			m.invoke(aspectInstance, null);
		}
	}

	private void beforeMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m : beforeMethods) {
			m.invoke(aspectInstance, null);
		}
	}
}
