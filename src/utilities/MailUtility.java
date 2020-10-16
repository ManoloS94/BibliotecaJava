package utilities;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtility {

	private String username;
	private String password;
	
	public MailUtility() {

		username = "manuelsantosdaw@gmail.com";
		password = "manuel.01Sc";
	}
	
	public MailUtility(String usuario, String contrasenia) {

		username = usuario;
		password = contrasenia;
	}
	
	public void enviarCorreo(String to, String subject, String content) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(content, "text/html");

			Transport.send(message);

		} catch (MessagingException e) {
			System.out.println("No se pudo enviar el correo");
			System.out.println("Error: "+e);
		}
	}

}
