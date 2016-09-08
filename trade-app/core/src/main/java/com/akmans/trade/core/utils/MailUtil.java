package com.akmans.trade.core.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.akmans.trade.core.service.MessageService;

@Component
public class MailUtil {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MailUtil.class);

	@Autowired
	private Environment env;

	@Autowired
	private MessageService messageService;

	public void sendMail(String subject, String body) {
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", env.getRequiredProperty("mail.smtp.starttls.enable"));
		props.put("mail.smtp.auth", env.getRequiredProperty("mail.smtp.auth"));
		props.put("mail.smtp.host", env.getRequiredProperty("mail.smtp.host"));
		props.put("mail.smtp.port", env.getRequiredProperty("mail.smtp.port"));

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(env.getRequiredProperty("mail.username"),
						env.getRequiredProperty("mail.password"));
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(env.getRequiredProperty("mail.sender")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(env.getRequiredProperty("mail.receiver")));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			logger.error("Error sending mail.", e);
			throw new RuntimeException(e);
		}
	}
}
