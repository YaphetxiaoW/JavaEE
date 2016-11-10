package factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import resource.Resource;
import test.Autowired;
import test.Component;
import test.boss;
import test.car;
import test.office;
import bean.BeanDefinition;
import bean.BeanUtil;
import bean.PropertyValue;
import bean.PropertyValues;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext{	

	
	public ClassPathXmlApplicationContext(Resource resource)
	{		
		//�ȼ���component��bean
		addComponent();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
            NodeList beanList = document.getElementsByTagName("bean");
            
            for(int i = 0 ; i < beanList.getLength(); i++)
            {
            	Node bean = beanList.item(i);
            	BeanDefinition beandef = new BeanDefinition();
            	String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
            	String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();
            	
        		beandef.setBeanClassName(beanClassName);
        		
				try {
					Class<?> beanClass = Class.forName(beanClassName);
					//�ҵ���Ӧ����
					beandef.setBeanClass(beanClass);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		PropertyValues propertyValues = new PropertyValues();
        		
        		NodeList propertyList = bean.getChildNodes();
            	for(int j = 0 ; j < propertyList.getLength(); j++)
            	{
            		Node property = propertyList.item(j);
            		if (property instanceof Element) {
        				Element ele = (Element) property;
        				
        				String name = ele.getAttribute("name");
        				Class<?> type;
        				
        				if(!ele.getAttribute("ref").isEmpty()){
        					String ref=ele.getAttribute("ref");
        					
        					if(isExistBean(ref)){
        						//��������ʵ��
        						propertyValues.AddPropertyValue(new PropertyValue(name,this.getBean(ref)));
        					}else{
        						//�����map�Ҳ���������Ѱ��bean
        						achieveRef(ref,resource, i+1);
        						propertyValues.AddPropertyValue(new PropertyValue(name,this.getBean(ref)));
        					}
        				}else if(!ele.getAttribute("value").isEmpty()){						
        					try {						
        						type = beandef.getBeanClass().getDeclaredField(name).getType();								
        						Object value = ele.getAttribute("value");							     					        				
        						if(type == Integer.class)	        				
        						{        					
        							value = Integer.parseInt((String) value);      				
        						}else if(type == String.class){        					
        							value = String.valueOf((String) value);     			
        						}

        						propertyValues.AddPropertyValue(new PropertyValue(name,value));
					
        					} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block	
        						e.printStackTrace();				
        					} catch (SecurityException e) {
							// TODO Auto-generated catch block			
        						e.printStackTrace();		
        					}
        				
        				}
        				
        			}
            	}
            	beandef.setPropertyValues(propertyValues);

            	if(!this.isExistBean(beanName)){     	
            		this.registerBeanDefinition(beanName, beandef);
            	}

            }
            
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

	
	//��bean����һ��ʵ��
	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {
		
		try {
			// set BeanClass for BeanDefinition
			
			Class<?> beanClass = beanDefinition.getBeanClass();
			// set Bean Instance for BeanDefinition
			Object bean = null;
			
			if(achieveAutowired(beanClass)==null){
				bean=beanClass.newInstance();
				List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
				for(PropertyValue propertyValue: fieldDefinitionList)
				{
					BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
				}
				
				beanDefinition.setBean(bean);			
				return beanDefinition;
			}else{
				bean=achieveAutowired(beanClass);
				beanDefinition.setBean(bean);
				return beanDefinition;
			}
		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public void addComponent(){
		String ROOT = System.getProperty("user.dir") + File.separator+ "src";	
		readFile(ROOT);	
	}
	
	//��������package�µĺ���Component��java�ļ�
	public void readFile(String filePath) {
		File readFile = new File(filePath);
		File[] files = readFile.listFiles();
		
		String fileName = null;
		for (File file : files) {
			fileName = file.getName();
			if (file.isFile()) {
				
				//�õ����µ�java�ļ�
				if (fileName.endsWith(".java")) {			
					try {
						
						String  str=filePath+File.separator+ fileName;
						String beanClassName=str.substring(26, str.length()-5).replace('\\', '.');
						Class<?> beanClass = Class.forName(beanClassName);
					
						//�ж��Ƿ���Componentע��,�����뵽beanDefinitionMap��
						if(beanClass.isAnnotationPresent(Component.class)){
		
							Field[] filedList=beanClass.getDeclaredFields();
							BeanDefinition beandef = new BeanDefinition();
							PropertyValues propertyValues = new PropertyValues();						
							String beanName=beanClass.getAnnotation(Component.class).value();
							
							for(Field filed:filedList){
								filed.setAccessible(true); //����Щ�����ǿ��Է��ʵ�			
								
								//ƥ���ֶβ��õ����ֶε�ֵ 
								if(filed.getType().getName().equals(java.lang.String.class.getName())){				
									Object value=filed.get(beanClass.newInstance());						
									propertyValues.AddPropertyValue(new PropertyValue(filed.getName(),value));
								}	
							}
							beandef.setBeanClass(beanClass);
							beandef.setBeanClassName(beanClassName);
							beandef.setPropertyValues(propertyValues);	
							
							this.registerBeanDefinition(beanName, beandef);
						}			
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
				}
			} else {
				//�������а��µ�java�ļ�
				readFile(filePath + File.separator + fileName);
			}
		}
	}
	
	//ʵ��ref
	public void  achieveRef(String beanName,Resource resource,int i){			
		try {						
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();			
			DocumentBuilder dbBuilder;				
			dbBuilder = dbFactory.newDocumentBuilder();				
			Document document = dbBuilder.parse(resource.getInputStream());		       
			NodeList beanList = document.getElementsByTagName("bean");
			
			for(int k = i ; k < beanList.getLength(); k++)
	            {
	            	Node bean = beanList.item(k);	 
	            	String beanName2 = bean.getAttributes().getNamedItem("id").getNodeValue();
	            	
	            	if(beanName.equals(beanName2)){	
	            		
	            		BeanDefinition beandef = new BeanDefinition();
		            	String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
	            		
		            	beandef.setBeanClassName(beanClassName);
	            		
		            	try{
							Class<?> beanClass = Class.forName(beanClassName);
							beandef.setBeanClass(beanClass);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	PropertyValues propertyValues = new PropertyValues();
		            	
		            	NodeList propertyList = bean.getChildNodes();
		            	
		            	for(int j = 0 ; j < propertyList.getLength(); j++){	
		            		Node property = propertyList.item(j);
		            		if (property instanceof Element) {
		        				Element ele = (Element) property;
		        				
		        				String name = ele.getAttribute("name");
		        				Class<?> type;
								try {
									type = beandef.getBeanClass().getDeclaredField(name).getType();
									Object value = ele.getAttribute("value");
			        				
									if(type == Integer.class)	        				
	        						{        					
	        							value = Integer.parseInt((String) value);      				
	        						}else if(type == String.class){        					
	        							value = String.valueOf((String) value);     			
	        						}
									
									propertyValues.AddPropertyValue(new PropertyValue(name,value));
			        				
								} catch (NoSuchFieldException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		        				    				
		        			}
		            	}
	            		
		            	beandef.setPropertyValues(propertyValues);
		            	this.registerBeanDefinition(beanName, beandef);
	            	}
	            }		
		} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block			
			e.printStackTrace();	
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//ʵ��ע��Autowired
	public Object achieveAutowired(Class<?> beanClass){	
		//�õ����캯���ĸ���
		Constructor[] constructorList=beanClass.getConstructors();

		//����ÿ�����캯��
		for(Constructor constructor: constructorList){
			
			//�ж��Ƿ���ע��
			if(constructor.isAnnotationPresent(Autowired.class)){
				//�������ͺ͸���
				Class[] paramTypes=constructor.getParameterTypes();
				//��Ӧ���Դ��ݵ�ֵ	
				Object[] params=new Object[paramTypes.length];
				
				for(int i=0;i<paramTypes.length;i++){
					//�õ�������
					String str=(String)paramTypes[i].getName().replace('.','/');
					String[] str2=str.split("/");
					String beanName=str2[str2.length-1];
					//�õ���Ӧobject
					if(this.isExistBean(beanName)){
						params[i]=this.getBean(beanName);
					}
				}
				
				try {
				    return constructor.newInstance(params);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}				
		}
		
		return null;	
	}
}