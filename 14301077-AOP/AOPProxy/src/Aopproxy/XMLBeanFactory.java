package Aopproxy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class XMLBeanFactory extends AbstractBeanFactory {

	private NodeList beanList;
	private String xmlPath;

	public XMLBeanFactory(Resource resource) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
			beanList = document.getElementsByTagName("bean");
			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);
				BeanDefinition beandef = new BeanDefinition();
				String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
				String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();

				register(beandef, bean, beanClassName, beanName);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void register(BeanDefinition beandef, Node bean, String beanClassName, String beanName) {

		beandef.setBeanClassName(beanClassName);
		try {
			Class<?> beanClass = Class.forName(beanClassName);
			beandef.setBeanClass(beanClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PropertyValues propertyValues = new PropertyValues();

		NodeList propertyList = bean.getChildNodes();
		for (int j = 0; j < propertyList.getLength(); j++) {
			Node property = propertyList.item(j);
			if (property instanceof Element) {
				Element ele = (Element) property;

				String name = ele.getAttribute("name");
				Class<?> type;
				try {
					type = beandef.getBeanClass().getDeclaredField(name).getType();
					Object value;
					value = ele.getAttribute("value");
					if (ele.getAttribute("value").equals("")) {
						for (int i = 0; i < beanList.getLength(); i++) {
							Node abean = beanList.item(i);
							BeanDefinition abeandef = new BeanDefinition();
							String abeanClassName = abean.getAttributes().getNamedItem("class").getNodeValue();
							String abeanName = abean.getAttributes().getNamedItem("id").getNodeValue();
							if (ele.getAttribute("ref").equals(abeanName)) {
								register(abeandef, abean, abeanClassName, abeanName);
							}
						}
						value = this.getBean(ele.getAttribute("ref"));
					}

					if (type == Integer.class) {
						value = Integer.parseInt((String) value);
					}

					propertyValues.AddPropertyValue(new PropertyValue(name, value));
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		beandef.setPropertyValues(propertyValues);

		this.registerBeanDefinition(beanName, beandef);
	}

	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {

		try {
			// set BeanClass for BeanDefinition

			Class<?> beanClass = beanDefinition.getBeanClass();
			// set Bean Instance for BeanDefinition
			Object bean = beanClass.newInstance();

			List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
			for (PropertyValue propertyValue : fieldDefinitionList) {
				BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
			}

			beanDefinition.setBean(bean);

			return beanDefinition;

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
