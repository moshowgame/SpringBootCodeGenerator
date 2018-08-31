package com.softdev.system.generator.util;


import cn.hutool.core.util.ReUtil;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

public class EmailUtil {
    public static final String smtpHost = "smtp.qq.com"; //requires valid smtp host
    public static final String fromEmail = "804461110@qq.com"; //requires valid gmail id
    public static final String password = "cyvzavufhuhxbcgi"; // correct password for gmail id

    /**
     * Utility method to send simple HTML email
     * @param toEmail
     * @param subject
     * @param body
     */
    public static void sendEmail(String toEmail, String subject, String body){
        try
        {

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost); //SMTP Host
            props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
            props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
            props.put("mail.smtp.port", "465"); //SMTP Port

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            };
            Session session = Session.getInstance(props, auth);

            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, "iGMAT-ADMIN"));

            //msg.setReplyTo(InternetAddress.parse("moshowgame@126.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            Transport.send(msg);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     Outgoing Mail (SMTP) Server
     requires TLS or SSL: smtp.gmail.com (use authentication)
     Use Authentication: Yes
     Port for TLS/STARTTLS: 587
     */
    public static void main(String[] args) {
       /* System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "465"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS*/
      //EmailUtil.sendCheckCode("725302013@qq.com", RandomUtil.randomNumbers(6));
        boolean isEmail = ReUtil.isMatch("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?", "moshow@126.cc");
        System.out.println(isEmail);
    }

    public static void sendCheckCode(String toEmail,String checkCode){
        EmailUtil.sendEmail(toEmail,"iGmat验证码", "您正在进行iGmat账号相关操作，请输入验证码："+checkCode+"。如非本人操作，请忽略该信息。");

    }

    public static void sendActiveEmail(String toEmail,String checkCode,HttpServletRequest request){

        EmailUtil.sendEmail(toEmail,"iGmat激活邮件", "您正在进行iGmat账号认证，请点击该链接激活："+BasePath.getBasePath(request)+"user/active/"+toEmail+"/"+checkCode+" 。如非本人操作，请忽略该信息。");

    }
}
