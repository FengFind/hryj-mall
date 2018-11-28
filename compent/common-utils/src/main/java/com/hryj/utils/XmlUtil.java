package com.hryj.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 白飞
 * @className: XmlUtil
 * @description:
 * @create 2018/9/27 13:27
 **/
public class XmlUtil {

    /**
     * JAXBContext 由XML字符串转换为对象
     *
     * @param xml
     *            XML字符串
     *
     * @param entity
     *            返回的实体
     *
     * @return 实体对象
     */
    public static <T> T xmlToObject(String xml,Class<T> entity) {
        try {
            JAXBContext context = JAXBContext.newInstance(entity);
            Unmarshaller unmarshal = context.createUnmarshaller();
            StringReader stringReader = new StringReader(xml);
            @SuppressWarnings("unchecked")
            T object = (T) unmarshal.unmarshal(stringReader);
            return  object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JAXBContext 由实体转换为xml
     *
     * @param object
     * 				根节点对象
     *
     * @return xml字符串
     */
    public static <T> String objectToXml(T object) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(object.getClass());
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            mar.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> objectToMap(Object obj){
        if(obj == null){
            return null;
        }
        try {
            Map<String, Object> map = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter!=null ? getter.invoke(obj) : null;
                map.put(key, value);
            }
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }

}
