package com.baidu.perf.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import com.baidu.perf.utils.FileIOUtil;
import com.baidu.perf.utils.MyAuthenticator;
import com.baidu.perf.utils.TimeUtil;
import com.google.gson.Gson;
import com.sun.mail.util.MailSSLSocketFactory;

public class SendEmail {

	private static JavaMailSenderImpl senderImpl;
	private static MimeMessage mailMessage = null;
	private static MimeMessageHelper messageHelper;

	public static boolean init() {
         boolean b=false;
        senderImpl = new JavaMailSenderImpl();
		senderImpl.setHost("email.baidu.com");
		senderImpl.setPort(587);//SMTP服务器（端口465或587）
		senderImpl.setProtocol("smtp");
		try {
			// 建立邮件消息,发送简单邮件和html邮件的区别
			// 注意这里的boolean,等于真的时候才能嵌套图片，在构建MimeMessageHelper时候，所给定的值是true表示启用，
			// multipart模式 为true时发送附件 可以设置html格式
			mailMessage = senderImpl.createMimeMessage();
			messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
			// 发件箱
			messageHelper.setFrom("crm_mail@baidu-mgame.com");
			senderImpl.setUsername("crm_mail");
			senderImpl.setPassword("qwe123!@#");
			Properties props = new Properties();
			//一次给很多人发信，如果其中一个地址无效，Invalid Address，那么就一封信都不会发   mail.smtp.sendpartial属性设置为true就可以
			props.put("mail.smtp.sendpartial", "true");
			props.put("mail.transport.protocol", "smtps"); 
			props.put("mail.smtp.starttls.enable", "true");
			//I/O连接超时时间，单位为毫秒，默认为永不超时
			//prop.put("mail.smtp.timeout", "6000000");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.debug", "true");
			props.put("mail.debug", "true");
			props.put("mail.smtp.ssl.enable", "false");
	        props.put("mail.smtp.socketFactory.fallback", "false");
			
			props.put("mail.smtp.ssl.checkserveridentity", "false");
			props.put("mail.smtp.ssl.trust", "email.baidu.com");
	        MailSSLSocketFactory sf = new MailSSLSocketFactory();  
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.socketFactory", sf);
			
			MyAuthenticator auth = new MyAuthenticator("crm_mail", "qwe123!@#");  
	        Session session = Session.getInstance(props, auth);
	        senderImpl.setJavaMailProperties(props);
	        senderImpl.setSession(session);
			session.setDebug(true);
			b=true;
		} catch (Exception e) {
			b=false;
		}
       return b;
	}

	public static boolean send(String subject, String content, String sendAddress,
			String ccAddress) {
		return send(subject, content, sendAddress, ccAddress, null);
	}

	public static boolean send(String subject, String content, String sendAddress,
			String ccAddress, String files) {
		boolean b=false;
		try {
			b=init();
			if(b){
				messageHelper.setSubject(subject);
				// true 表示启动HTML格式的邮件
				messageHelper.setText(content, true);
				if (null != files) {
					String[] fs = files.split(";");
					String filename = "";
					for (String filepath : fs) {
						if (filepath.trim().length() > 0) {
							FileSystemResource file = new FileSystemResource(
									new File(filepath));
							// 这里的方法调用和插入图片是不同的。
							if (filepath.contains("/")) {
								filename = filepath.substring(filepath
										.lastIndexOf("/") + 1);
							} else {
								filename = filepath.substring(filepath
										.lastIndexOf("\\") + 1);
							}
							messageHelper.addAttachment(filename, file);
						}
					}
				}
				
				new InternetAddress();
				InternetAddress[] internetAddressTo = InternetAddress.parse(sendAddress.replaceAll(";", ","));
				mailMessage.setRecipients(Message.RecipientType.TO,internetAddressTo); //收件人    
				//抄送
				if (!StringUtils.isEmpty(ccAddress)) {
					new InternetAddress();
					 InternetAddress[] internetAddressCC = InternetAddress.parse(ccAddress.replaceAll(";", ","));
					 mailMessage.setRecipients(Message.RecipientType.CC, internetAddressCC);
				}
				mailMessage.setRecipients(Message.RecipientType.BCC, "wangbiao@baidu-mgame.com");
				senderImpl.send(mailMessage);
	            b=true;
			}
		} catch (Exception e) {
			b=false;
		}
		return b;
	}

	public static boolean send(String subject, String content, String sendAddress,
			String[] ccAddress, String[] files) {
		if (null == ccAddress || ccAddress.length == 0) {
			ccAddress = new String[] { sendAddress };
		}
		if (null == files || files.length == 0) {
			return  send(subject, content, sendAddress, ccAddress);
		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : ccAddress) {
				sb.append(s).append(";");
			}
			StringBuilder filesstr = new StringBuilder();
			for (String file : files) {
				filesstr.append(file).append(";");
			}
			return  send(subject, content, sendAddress,
					sb.substring(0, sb.length() - 1),
					filesstr.substring(0, filesstr.length() - 1));
		}

	}

	public static boolean send(String subject, String content, String sendAddress,
			String[] ccAddress) {
		StringBuilder sb = new StringBuilder();
		for (String s : ccAddress) {
			sb.append(s).append(";");
		}
		return send(subject, content, sendAddress, sb.substring(0, sb.length() - 1));
	}
	
	@SuppressWarnings("unused")
	private static final String TEST_PATH = "E:\\Perf4jAnalysis(Linux)\\conf\\sendEmail.conf";
	private static final String ONLINE_PATH = "conf/sendEmail.conf";		
	
	public static void main(String[] args) {
		String subject = args[0];
		boolean contentFlag = Boolean.valueOf(args[1]);
		
    	List<String> confList = new LinkedList<String>();
		try {
			confList = FileIOUtil.getFileContextBIO(ONLINE_PATH, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (null == confList || confList.size() == 0) {
            return;
        }
        Map<String, String> confMap = new HashMap<String, String>();
        for (String line : confList) {
            if (null == line || line.trim().length() == 0 || line.trim().startsWith("#")) {
                continue;
            }
            String[] conf = line.split("=");
            if (null == conf || conf.length != 2) {
                continue;
            }

            confMap.put(conf[0].trim(), conf[1].trim());
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(confMap);
        SendMailParam confParam = gson.fromJson(jsonStr, SendMailParam.class);
        String yesterday = TimeUtil.getDateStrShift(-1);
        String yesterdayNoSplit = yesterday.replaceAll("-", "");
		String[] serverList = confParam.getService_list().split(",");
        String[] files = new String[serverList.length];
        for(int i=0; i<serverList.length; ++i){
        	files[i] = serverList[i] + yesterdayNoSplit + ".txt";
        }
        String sendAddress = confParam.getSendAddress();
        String[] ccAddress = confParam.getCcAddress().split(",");
        StringBuilder content = new StringBuilder(args[2]);
        if(contentFlag){
        	for(String fileName : files){
	        	File file = new File(fileName);
	            BufferedReader reader = null;
	            try {
	                reader = new BufferedReader(new FileReader(file));
	                String tempString = null;
	                while ((tempString = reader.readLine()) != null) {
	                	content.append(tempString).append("<br>");
	                }
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                if (reader != null) {
	                    try {
	                        reader.close();
	                    } catch (IOException e1) {
	                    }
	                }
	            }
        	}
        } 
		send(subject, content.toString(), sendAddress, ccAddress, files);
	}
}