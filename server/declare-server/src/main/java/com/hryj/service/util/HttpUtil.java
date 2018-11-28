
/**
 * 
 * HttpUtil - 工具类 
 * ============================================================================
 * 版权所有 2013-2018
 * 网站地址:
 * ============================================================================
 * 
 */
package com.hryj.service.util;


import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * HttpUtil - 工具类 
 * 
 * 
 * @author		BF
 * 
 * @version     1.0
 * 
 */
public class HttpUtil {

	private HttpUtil(){
	}
	/************************************************************
	 * 
	 * 与服务器创建http协议，返回数据
	 * 
	 * @param map
	 *            参数
	 * @param urlAddr
	 *            URL 请求地址
	 * @param encode
	 *            UTF-8 编码
	 * @return 结果
	 ***********************************************************/
	public static String httppost(Map<String,String> map, String urlAddr,String encode) {
		try {
			URL url = new URL(urlAddr);
			StringBuffer buffer = new StringBuffer();
			
			try {
				for(String keys:map.keySet()){
					buffer.append(keys).append("=").append(URLEncoder.encode(map.get(keys), encode)).append("&");
				}
			} catch (UnsupportedEncodingException e1) {
			}
			
			HttpURLConnection httpURLConnection = null;
			OutputStream outputStream = null;
			try {
				if (null != url) {
					httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setConnectTimeout(6000);
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setInstanceFollowRedirects(true);
					httpURLConnection.setUseCaches(false);
					httpURLConnection.setRequestProperty("Accept","application/json");
					httpURLConnection.setRequestProperty("Accept-Charset", encode);
					httpURLConnection.setRequestProperty("Charset", encode);
					httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
					String buf = buffer.substring(0, buffer.length() - 1).toString();
					byte[] mydata = buf.getBytes();
					httpURLConnection.setRequestProperty("Content-Length",String.valueOf(mydata.length));
					outputStream = httpURLConnection.getOutputStream();
					outputStream.write(mydata, 0, mydata.length);
					outputStream.close();
					int responseCode = httpURLConnection.getResponseCode();
					if (responseCode == 200) {
						String result = changeInputStream(httpURLConnection.getInputStream(), encode);
						httpURLConnection.disconnect();
						return result;
					}
				}
			} catch (IOException e) {
				//e.printStackTrace();
				return "";
			}
		} catch (MalformedURLException e) {
		}
		return "";
	}

	/**********************************************************************
	 * 
	 * 获得content流转换成字符串
	 * 
	 * @param inputStream
	 *            输入流
	 * @param encode
	 *            编码
	 * @return 结果
	 *********************************************************************/
	private static  String changeInputStream(InputStream inputStream,String encode) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (null != inputStream) {
			byte[] buff = new byte[1024];
			int len = 0;
			try {
				while ((len = inputStream.read(buff)) != -1) {
					out.write(buff, 0, len);
				}
				byte[] result = out.toByteArray();
				if(result.length > 0)
				{
					return  new String(out.toByteArray(), encode);	
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(null != out) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "";
	}

	/**
	 * 根据xml推送属地海关
	 *
	 * @param xml
	 * 			报文
	 * @return
	 */
	public static Map<String, String> postCustom(String xml){
		Map<String, String> paramMap = Maps.newHashMap();
		String base64Xml = encode(xml);
		paramMap.put("data", base64Xml);
		String result = httppost(paramMap, "http://218.70.16.172:8080/recvpost","UTF-8");
		paramMap.clear();
		if(StringUtils.isEmpty(result) || !"true".equals(result.toLowerCase())){
			paramMap.put("status", "2");
			paramMap.put("msg", "推送属地海关推送失败");
			paramMap.put("result", "");
			return paramMap;
		}
		paramMap.put("status", "1");
		paramMap.put("msg", "推送属地海关成功");
		paramMap.put("result", result);
		return paramMap;
	}

	/**
	 * 根据xml推送给商家
	 *
	 * @param xml
	 * 			报文
	 * @return
	 */
	public static String postNotify(String url, String xml){
		Map<String, String> paramMap = Maps.newHashMap();
		String base64Xml = encode(xml);
		paramMap.put("data", base64Xml);
		String result = httppost(paramMap, url,"UTF-8");
		return result;
	}

	/**
	 * 编码
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String str) {
		try {
			byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes("utf-8"));
			return new String(bytes, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 解码
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String str){
		try {
			byte[] bytes = str.getBytes("utf-8");
			byte[] convertBytes = org.apache.commons.codec.binary.Base64.decodeBase64(bytes);
			return new String(convertBytes, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}
}
