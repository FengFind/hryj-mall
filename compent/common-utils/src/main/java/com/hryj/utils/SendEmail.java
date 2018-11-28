package com.hryj.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author 李道云
 * @className: SendEmail
 * @description: 发送邮件
 * @create 2018/8/7 22:10
 **/
@Component
public class SendEmail {

    private static String SERVERNAME;

    private static boolean SENDFLAG;

    @Value("${spring.application.name}")
    public void setServerName(String serverName) {
        SERVERNAME = serverName;
    }

    @Value("${email.send.exception}")
    public void setSendFlag(boolean sendFlag) {
        SENDFLAG = sendFlag;
    }

    /**
     * 发送邮件
     * @param subject
     * @param message
     */
    public static void sendEmail(String subject,String message){
        if(!SENDFLAG){
            return;
        }
        String ip = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException e) {}
        //获取邮件发送器
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setProtocol("smtp");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("286177441@qq.com");
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.qq.com");
        props.setProperty("mail.smtp.auth", "true");
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("286177441@qq.com","hlsqyfktzrpncafd");
            }
        });
        javaMailSender.setSession(session);
        //设置邮件信息
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("286177441@qq.com");
        mainMessage.setTo("524079605@qq.com");
        mainMessage.setSubject(SERVERNAME + ":" + ip + ":" + subject);
        mainMessage.setText(message);
        javaMailSender.send(mainMessage);
    }
}
