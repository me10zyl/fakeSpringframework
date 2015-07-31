package net.xicp.zyl_me.springframework.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultInvocationHandler implements InvocationHandler {
	private Object target;
	private Method beforeMethod, afterMethod;
	private Class<?> aspect;
	private Object aspectInstance;
	public Class<?> getAspect() {
		return aspect;
	}

	public void setAspect(Class<?> aspect) throws InstantiationException, IllegalAccessException {
		this.aspect = aspect;
		this.aspectInstance = this.aspect.newInstance();
	}

	public DefaultInvocationHandler(Object target) {
		this.target = target;
	}

	public Method getBeforeMethod() {
		return beforeMethod;
	}

	public void setBeforeMethod(Method beforeMethod) {
		this.beforeMethod = beforeMethod;
	}

	public Method getAfterMethod() {
		return afterMethod;
	}

	public void setAfterMethod(Method afterMethod) {
		this.afterMethod = afterMethod;
	}

	private void beforeMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		beforeMethod.invoke(aspectInstance, null);
	}

	private void afterMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		afterMethod.invoke(aspectInstance, null);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		beforeMethod();
		method.invoke(target, args);
		afterMethod();
		return null;
	}
}
