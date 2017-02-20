package cache.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * 普通Java类引用Spring的Bean的工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 21:33:49 - 8 Feb 2017
 * @detail <h1>注意：必须将该工具类交给Spring容器统一管理才可以使用。</h1>
 * 提供了两个getBean()方法的不同实现。
 * 根据传入的Bean的ID或者Class对象返回Spring容器中的Bean对象。
 */
public class PojoRefSpringBeanUtil extends ApplicationObjectSupport {

	/** Spring的上下文ApplicationContext，用于引用Bean */
	private static ApplicationContext applicationContext = null;
	
	/** 禁止外部通过new来创建对象 */
	private PojoRefSpringBeanUtil() {}
	
	/** 获取已经初始化的ApplicationContext */
	private static ApplicationContext getApplicationContextUtil() {
		return applicationContext;
	}
	
	/**
	 * 根据传入的name属性获取Spring容器中Bean的Id与该name匹配的Bean对象
	 * @param name
	 * @return
	 * @throws BeansException
	 */
	public static Object getBean(String name) throws BeansException {
		return getApplicationContextUtil().getBean(name);
	}
	
	/**
	 * 根据传入的Class获取Spring容器中的Bean对象
	 * @param requiredType
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return getApplicationContextUtil().getBean(requiredType);
	}
	
	/**
	 * 重写父类的初始化上下文的方法initApplicationContext()，在初始化的同时
	 * 获取Spring的上下文ApplicationContext，并且赋值给当前类的字段applicationContext
	 */
	@Override
	protected void initApplicationContext(ApplicationContext context) throws BeansException {
		super.initApplicationContext(context);
		if(PojoRefSpringBeanUtil.applicationContext == null) {
			PojoRefSpringBeanUtil.applicationContext = context;
		}
	}
}
