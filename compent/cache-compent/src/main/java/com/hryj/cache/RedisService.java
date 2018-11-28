package com.hryj.cache;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author 李道云
 * @className: RedisService
 * @description: redis缓存service
 * @create 2018/6/22 21:21
 **/
@Service
public class RedisService {

    /***==================================================database0-存放字典表数据=========================================================***/

    @Autowired
    @Qualifier("jedisPool")
    private JedisPool jedisPool;

    /**
     * 添加数据到redis缓存，会覆盖已存在的键
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param value 缓存value
     * @param exT 过期时间（单位秒）
     */
    public String put(String group_name, String key_name, String value, Integer exT) {
        if (value == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        String result = null;
        if (exT != null) {
            result = jedis.setex((group_name + "." + key_name),exT,value);
        } else {
            result = jedis.set((group_name + "." + key_name),value);
        }
        jedis.close();
        return result;
    }

    /**
     * 从reids缓存里获取数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public String get(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        String val = jedis.get(group_name + "." + key_name);
        jedis.close();
        return val;
    }

    /**
     * 更新缓存的过期时间
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param exT 过期时间（单位秒）
     */
    public void expire(String group_name, String key_name, Integer exT){
        if (group_name == null || key_name == null || exT ==null) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        jedis.expire((group_name + "." + key_name), exT);
        jedis.close();
    }

    /**
     * 从reids缓存里删除数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public void delete(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        jedis.del(group_name + "." + key_name);
        jedis.close();
    }

    /**
     * @author 李道云
     * @methodName: setnx
     * @methodDesc: 新增键值对防止覆盖原先值
     * @description: 设置成功，返回 1，设置失败，返回 0
     * @param: [group_name, key_name, value]
     * @return java.lang.Long
     * @create 2018-07-18 9:04
     **/
    public Long setnx(String group_name,String key_name,String value){
        Jedis jedis = jedisPool.getResource();
        Long i = jedis.setnx(group_name + "." + key_name,value);
        jedis.close();
        return i;
    }

    /**
     * @author 李道云
     * @methodName: existsKey
     * @methodDesc: 判断键是否存在
     * @description:
     * @param: [group_name, key_name]
     * @return boolean
     * @create 2018-07-18 9:09
     **/
    public boolean existsKey(String group_name, String key_name){
        Jedis jedis = jedisPool.getResource();
        boolean b = jedis.exists(group_name + "." + key_name);
        jedis.close();
        return b;
    }

    /**
     * 根据group_name获取key集合
     * @param group_name
     * @return
     */
    public Set<String> getKeysByGroupName(String group_name){
        if(StrUtil.isEmpty(group_name)){
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        Set<String> set = jedis.keys(group_name + "*");
        jedis.close();
        return set;
    }

    /***==================================================database1-存放用户登录数据=========================================================***/

    @Autowired
    @Qualifier("jedisPool1")
    private JedisPool jedisPool1;

    /**
     * 添加数据到redis缓存，会覆盖已存在的键
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param value 缓存value
     * @param exT 过期时间（单位秒）
     */
    public String put1(String group_name, String key_name, String value, Integer exT) {
        if (value == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool1.getResource();
        String result = null;
        if (exT != null) {
            result = jedis.setex((group_name + "." + key_name),exT,value);
        } else {
            result = jedis.set((group_name + "." + key_name),value);
        }
        jedis.close();
        return result;
    }

    /**
     * 从reids缓存里获取数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public String get1(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool1.getResource();
        String val = jedis.get(group_name + "." + key_name);
        jedis.close();
        return val;
    }

    /**
     * 更新缓存的过期时间
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param exT 过期时间（单位秒）
     */
    public void expire1(String group_name, String key_name, Integer exT){
        if (group_name == null || key_name == null || exT ==null) {
            return;
        }
        Jedis jedis = jedisPool1.getResource();
        jedis.expire((group_name + "." + key_name), exT);
        jedis.close();
    }

    /**
     * 从reids缓存里删除数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public void delete1(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return;
        }
        Jedis jedis = jedisPool1.getResource();
        jedis.del(group_name + "." + key_name);
        jedis.close();
    }

    /**
     * @author 李道云
     * @methodName: setnx
     * @methodDesc: 新增键值对防止覆盖原先值
     * @description: 设置成功，返回 1，设置失败，返回 0
     * @param: [group_name, key_name, value]
     * @return java.lang.Long
     * @create 2018-07-18 9:04
     **/
    public Long setnx1(String group_name,String key_name,String value){
        Jedis jedis = jedisPool1.getResource();
        Long i = jedis.setnx(group_name + "." + key_name,value);
        jedis.close();
        return i;
    }

    /**
     * @author 李道云
     * @methodName: existsKey
     * @methodDesc: 判断键是否存在
     * @description:
     * @param: [group_name, key_name]
     * @return boolean
     * @create 2018-07-18 9:09
     **/
    public boolean existsKey1(String group_name, String key_name){
        Jedis jedis = jedisPool1.getResource();
        boolean b = jedis.exists(group_name + "." + key_name);
        jedis.close();
        return b;
    }

    /**
     * 根据group_name获取key集合
     * @param group_name
     * @return
     */
    public Set<String> getKeysByGroupName1(String group_name){
        if(StrUtil.isEmpty(group_name)){
            return null;
        }
        Jedis jedis = jedisPool1.getResource();
        Set<String> set = jedis.keys(group_name + "*");
        jedis.close();
        return set;
    }

    /***==================================================database2-存放查询缓存数据=========================================================***/

    @Autowired
    @Qualifier("jedisPool2")
    private JedisPool jedisPool2;

    /**
     * 添加数据到redis缓存，会覆盖已存在的键
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param value 缓存value
     * @param exT 过期时间（单位秒）
     */
    public String put2(String group_name, String key_name, String value, Integer exT) {
        if (value == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool2.getResource();
        String result;
        if (exT != null && exT > 0) {
            result = jedis.setex((group_name + "." + key_name),exT,value);
        } else {
            result = jedis.set((group_name + "." + key_name),value);
        }
        jedis.close();
        return result;
    }

    /**
     * 从reids缓存里获取数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public String get2(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool2.getResource();
        String val = jedis.get(group_name + "." + key_name);
        jedis.close();
        return val;
    }

    /**
     * 更新缓存的过期时间
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @param exT 过期时间（单位秒）
     */
    public void expire2(String group_name, String key_name, Integer exT){
        if (group_name == null || key_name == null || exT ==null) {
            return;
        }
        Jedis jedis = jedisPool2.getResource();
        jedis.expire((group_name + "." + key_name), exT);
        jedis.close();
    }

    /**
     * 从reids缓存里删除数据
     * @param group_name 缓存分组名
     * @param key_name 缓存key
     * @return
     */
    public void delete2(String group_name, String key_name) {
        if (group_name == null || key_name == null) {
            return;
        }
        Jedis jedis = jedisPool2.getResource();
        jedis.del(group_name + "." + key_name);
        jedis.close();
    }

    /**
     * @author 李道云
     * @methodName: setnx
     * @methodDesc: 新增键值对防止覆盖原先值
     * @description: 设置成功，返回 1，设置失败，返回 0
     * @param: [group_name, key_name, value]
     * @return java.lang.Long
     * @create 2018-07-18 9:04
     **/
    public Long setnx2(String group_name,String key_name,String value){
        Jedis jedis = jedisPool2.getResource();
        Long i = jedis.setnx(group_name + "." + key_name,value);
        jedis.close();
        return i;
    }

    /**
     * @author 李道云
     * @methodName: existsKey
     * @methodDesc: 判断键是否存在
     * @description:
     * @param: [group_name, key_name]
     * @return boolean
     * @create 2018-07-18 9:09
     **/
    public boolean existsKey2(String group_name, String key_name){
        Jedis jedis = jedisPool2.getResource();
        boolean b = jedis.exists(group_name + "." + key_name);
        jedis.close();
        return b;
    }

    /**
     * 根据group_name获取key集合
     * @param group_name
     * @return
     */
    public Set<String> getKeysByGroupName2(String group_name){
        if(StrUtil.isEmpty(group_name)){
            return null;
        }
        Jedis jedis = jedisPool2.getResource();
        Set<String> set = jedis.keys(group_name + "*");
        jedis.close();
        return set;
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 向队列里插入数据
     * @param:
     * @return
     * @create 2018-09-19 10:55
     **/
    public void pushValue(String value,String group_name, String key_name){
        if (group_name == null || key_name == null) {
            return;
        }
        Jedis jedis = jedisPool2.getResource();
        jedis.lpush(group_name + "." + key_name,value);
        jedis.close();
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 向队列里插入数据
     * @param:
     * @return
     * @create 2018-09-19 10:55
     **/
    public void pushAll(List<String> values,String group_name, String key_name){
        if (group_name == null || key_name == null) {
            return;
        }
        values.forEach((String value)-> pushValue(value,group_name,key_name));
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据key获取队列list
     * @param: len:获取list长度,可不传，默认为-1
     * @return
     * @create 2018-09-19 10:58
     **/
    public List<String> getListByKey(String group_name, String key_name,Integer len){
        if (group_name == null || key_name == null) {
            return null;
        }
        if(len==null||len==0){
            len=-1;
        }
        Jedis jedis = jedisPool2.getResource();
        //-1表示全部
        List<String> values = jedis.lrange(group_name + "." + key_name, 0, len);
        jedis.close();
        return values;
    }



    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 存储序列化数据,并返回
     * @param:
     * @return
     * @create 2018-09-19 11:32
     **/
    public ArrayList setSerialization(ArrayList values, String group_name, String key_name){
        if (group_name == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool2.getResource();
        jedis.set((group_name + "." + key_name).getBytes(),SerializationUtils.serialize(values));
        jedis.close();
        return getSerialization(group_name,key_name);
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 反回反序列化数据
     * @param:
     * @return
     * @create 2018-09-19 11:32
     **/
    public ArrayList getSerialization(String group_name, String key_name){
        if (group_name == null || key_name == null) {
            return null;
        }
        Jedis jedis = jedisPool2.getResource();
        byte[] bytes = jedis.get((group_name + "." + key_name).getBytes());
        jedis.close();
        ArrayList arrayList = SerializationUtils.deserialize(bytes);
        return arrayList;
    }
}
