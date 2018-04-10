package com.mannu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class PranMailService {
	private Connection conn;

	public static void main(String[] args) {
		PranMailService pm=new PranMailService();
	}
	public PranMailService() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn=DriverManager.getConnection("jdbc:sqlserver://192.168.84.90;user=sa;password=Karvy@123;database=management");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement up=conn.prepareStatement("update PranRejection set MailStatus='Not Send' where EmailId=''");
			 up.execute();
			 up.close();
			PreparedStatement ps=conn.prepareStatement("select InwardNo,FullName,AppAddress,RejDescription,AckNo,popno,Office,DTOAddress,EmailId,PaoRejNo from PranRejection where PrintStatus='Done' and MailStatus=''");
			ResultSet rs=ps.executeQuery();
			while (rs.next()) {
				
				String popde=null;
				 if(rs.getString(6).equals("")) {
					 popde="";
				 } else {
					 popde="With POPS Receipt No: "+rs.getString(6);
				 }			
				 String dd1="";
				 if(rs.getString(7)==null) {
					 dd1="";
				 } else {
					 dd1=rs.getString(7);
				 }
				Properties prop=new Properties();
				prop.put("mail.smtp.port", "25");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.smtp.ssl.trust", "srv-mail-ch5.karvy.com");
				Session session = Session.getInstance(prop,new javax.mail.Authenticator() {
					protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
						return new javax.mail.PasswordAuthentication("thelp.tin@karvy.com","Karvy@123");
					}
				});
				 StringBuilder email=new StringBuilder();
				 email.append("<html><head><style>th,td{padding: 5px;font-size:14px;} table, th, td {    border: 1px solid black;    border-collapse: collapse;} th,td{ text-align:center; } caption{ text-align:left;font-size:15px;font-style: italic; } p{font-size:14px;font-family: Calibri, Candara, Segoe, 'Segoe UI', Optima, Comic Sans MS, sans-serif;}</style></head><body>");
				 
				 email.append("<div>Dear Sir/Madam,</div>");
				 email.append("<p></p>");
				 email.append("<div>We thank you for making an application for registering in National Pension System. However "
				 		+ ", we regret to inform you that your application could not be processed successfully due to the following reason (S): <b>"
				 		+rs.getString(4)+"</b></div>");
				 email.append("<p></p>");
				 email.append("<div>Kindly note that the Acknowledgement Number for the application is <b>"+rs.getString(5)+"</b> "+popde+". The form was "
				 		+ "submitted by <b>"+dd1+"</b> having registration number <b>"+rs.getString(10)+"</b> with Central Recordkeeping Agency (CRA)."
				 				+" You are requested to contact your nodal Office for rectification of discrepancies/re-submission of form.</div>");
				 email.append("<p></p>");
				 email.append("<div><b>The contact details of the Nodal Officer are as below:</b></div>");
				 email.append("<p></p>");
				 email.append("<div>Nodal Officer Name : "+dd1+"</div>");
				 String[] nodofad=rs.getString(8).split("\\^");
				 email.append("<div>Address Line 1     : "+nodofad[0]+"</div>");
				 email.append("<div>Address Line 2     : "+nodofad[1]+"</div>");
				 email.append("<div>Address Line 3     : "+nodofad[2]+"</div>");
				 email.append("<div>Address Line 4     : "+nodofad[3]+"</div>");
				 email.append("<div>Address Line 5     : "+nodofad[4]+"</div>");
				 email.append("<div>Address Line 6     : "+nodofad[5]+"</div>");
				 email.append("<p></p>");
				 email.append("<div>We have simultaneously send this notification to the Nodal Office. However, this notification is send to you as well "
				 		+ "so that you can get in touch with the office and take necessary action.</div>");
				 email.append("<p></p>");
				 email.append("<div>On behalf of Central Record Keeping Agency (NPS),</div>");
				 email.append("<p></p>");
				 email.append("<div>Karvy Data Management Services Ltd. - CRA FC</div>");
				 email.append("<div>Plot No: 25,26,27 Near Image Hospital,</div>");
				 email.append("<div>Vittal Rao Nagar, Madhapur, Hyderabad,</div>");
				 email.append("<div>Telangana - 500081.</div>");
				 email.append("<div>Ph No. 040 - 66282809 / 040 - 66282810</div>");
				 email.append("<br></br>");
				 email.append("<br></br>");
				 email.append("<div>*** This is an automatically generated email, please do not reply ***</div>");
				 MimeMessage msg = new MimeMessage(session);
				 msg.setFrom(new InternetAddress("help.tin@karvy.com"));
				 msg.setSubject("Application rejection intimation from NPS");
				 //System.out.println("inw: "+rs.getString(1));
				 msg.addRecipient(Message.RecipientType.TO, new InternetAddress(rs.getString(9).toLowerCase()));
					 msg.addRecipient(Message.RecipientType.CC, new InternetAddress("crarejections@karvy.com"));
					 PreparedStatement up1=conn.prepareStatement("update PranRejection set MailStatus='Send' where InwardNo="+rs.getString(1));
					 up1.execute();
					 up1.close();
				 Multipart multipart = new MimeMultipart();
			        BodyPart htmlBodyPart = new MimeBodyPart();
			        htmlBodyPart.setContent(email.toString() , "text/html"); 
			        multipart.addBodyPart(htmlBodyPart);
			        msg.setContent(multipart);
			        Transport transport = session.getTransport("smtp");
			        transport.connect("srv-mail-ch5.karvy.com", 25,"help.tin@karvy.com","Karvy@123");
			        transport.sendMessage(msg, msg.getAllRecipients());
			        transport.close();			 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
