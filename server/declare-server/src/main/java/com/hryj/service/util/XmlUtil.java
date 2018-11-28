package com.hryj.service.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

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
}
