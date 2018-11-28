package com.hryj.file;

import com.aliyun.oss.OSSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 李道云
 * @className: AliYunOSS
 * @description: 阿里云OSS对象存储
 * @create 2018/7/3 22:11
 **/
@Slf4j
@Configuration
public class AliYunOSS {

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.domain}")
    private String domain;

    @Value("${oss.backetName}")
    private String backetName;

    public OSSClient getOSSClient(){
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * @author 李道云
     * @methodName: uploadFile
     * @methodDesc: 上传文件
     * @description:
     * @param: [orgFileName,inputStream]
     * @return java.lang.String
     * @create 2018-07-04 10:30
     **/
    public String uploadFile(String orgFileName, InputStream inputStream){
        OSSClient ossClient = getOSSClient();
        String fileType = orgFileName.substring(orgFileName.lastIndexOf("."));
        String fileName = new SimpleDateFormat("yyyyMMddHHmmSSS").format(new Date()) + fileType;
        ossClient.putObject(backetName, fileName, inputStream);
        String filePath = "https://" + domain + "/" + fileName;
        ossClient.shutdown();
        log.info("上传文件:filePath={}",filePath);
        return filePath;
    }

    /**
     * @author 李道云
     * @methodName: deleteFile
     * @methodDesc: 删除文件
     * @description:
     * @param: [filePath]
     * @return void
     * @create 2018-07-04 13:49
     **/
    public void deleteFile(String filePath){
        OSSClient ossClient = getOSSClient();
        log.info("删除文件:filePath={}",filePath);
        String objectName = filePath.substring(filePath.lastIndexOf("/")+1);
        ossClient.deleteObject(backetName, objectName);
        ossClient.shutdown();
    }
}
