package com.hryj.cache;

import java.io.*;

/**
 * @author 李道云
 * @className: SerializeUtil
 * @description: 对象序列化工具类
 * @create 2018/6/22 21:22
 **/
public class SerializeUtil {

    /**
     * 序列化对象
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) {
        byte[] bytes = null;
        ByteArrayOutputStream obj = null;
        ObjectOutputStream out = null;
        try {
            obj = new ByteArrayOutputStream();
            out = new ObjectOutputStream(obj);
            out.writeObject(object);
            bytes = obj.toByteArray();
            out.close();
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (obj != null) {
                    obj.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bin = null;
        ObjectInputStream obin = null;
        try {
            bin = new ByteArrayInputStream(bytes);
            obin = new ObjectInputStream(bin);
            obj = obin.readObject();
            obin.close();
            bin.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (obin != null) {
                    obin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }
}
