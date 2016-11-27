package Aopproxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory implements BeanFactory{
	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
	
	public Object getBean(String beanName)
	{
		return this.beanDefinitionMap.get(beanName).getBean();
	}
	
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
	{
		beanDefinition = GetCreatedBean(beanDefinition);
		if(beanDefinition.getBean() instanceof  ProxyFactoryBean){
			try {
				((ProxyFactoryBean)beanDefinition.getBean()).setFactory(this);
				Object proxyObject = ((ProxyFactoryBean)beanDefinition.getBean()).getProxy();
				beanDefinition.setBean(proxyObject);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}
	
	protected abstract BeanDefinition GetCreatedBean(BeanDefinition beanDefinition);
}
