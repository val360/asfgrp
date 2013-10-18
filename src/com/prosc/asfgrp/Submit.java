package com.prosc.asfgrp;

import com.prosc.io.IOUtils;
import com.prosc.servlet.ServletUtil;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;

import javax.servlet.ServletContext;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: val Date: 7/2/12 Time: 1:45 PM
 */
public class Submit extends WebPage {
	private static final Logger log = Logger.getLogger(Submit.class.getName());

	public Submit() {
		final TextField<String> studentFirstName = new TextField<String>("studentNameFirst", new Model<String>());
		final TextField<String> studentLastName = new TextField<String>("studentNameLast", new Model<String>());
		final TextField<String> officerFirstName = new TextField<String>("officerNameFirst", new Model<String>());
		final TextField<String> officerLastName = new TextField<String>("officerNameLast", new Model<String>());
		final TextField<String> email = new TextField<String>("email", new Model<String>());
		final TextField<String> campus = new TextField<String>("campus", new Model<String>());
		final TextArea<String> notes = new TextArea<String>("notes", new Model<String>());
		final FileUploadField fileUpload = new FileUploadField("file");
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		Form form = new Form("form") {
			@Override
			protected void onSubmit() {
				try {
					//process file uploads
					//Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "asfgrp-uploads");
					//if(!uploadFolder.exists() && !uploadFolder.mkdirs()) // Ensure folder exists
					//{
					//	throw new IOException("could not create folder " + uploadFolder.getAbsolutePath());
					//}
					ServletContext context = WebApplication.get().getServletContext();
					UUID uuid = UUID.randomUUID();
					String path = null;
					final FileUpload upload = fileUpload.getFileUpload();
					if(upload != null) {
						if( !upload.getClientFileName().endsWith(".pdf") ) {
							error("The upload file must be a PDF, the file you provided \"" + upload.getClientFileName() + "\" does not end in .pdf");
							return;
						}
						String scbase = ServletUtil.configValueForKey(context, "scbase");
						String scuploadbase = ServletUtil.configValueForKey(context, "scuploadbase");
						final Calendar cal = Calendar.getInstance();
						path = scuploadbase + "/" + cal.get(Calendar.YEAR) + "/" + ( cal.get(Calendar.MONTH) + 1 );
						final URL url = new URL(scbase + "/" + path + "/" + uuid.toString() + "/" + upload.getClientFileName());
						HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
						urlConnection.setDoOutput(true);
						urlConnection.setRequestMethod("PUT");
						urlConnection.setFixedLengthStreamingMode((int)upload.getSize());
						final OutputStream outputStream = urlConnection.getOutputStream();
						try {
							IOUtils.writeInputToOutput(upload.getInputStream(), outputStream, 1024);
							if( urlConnection.getResponseCode() >= 300 ) throw new RuntimeException(urlConnection.getResponseMessage() + ":" + urlConnection.getResponseCode());
							//else if( urlConnection.getResponseCode() >= 300 ) log.info(urlConnection.getResponseMessage() + ":" + urlConnection.getResponseCode());
						} finally {
							outputStream.close();
							upload.closeStreams();
						}
					}
					log.config("writing form data to db.");
					Connection c = DbConnectionManager.getConnection(context);
					String layout = ServletUtil.configValueForKey(context, "layout");
					String sql = "INSERT INTO \"" + layout + "\" (" + "\"student name first\",\"student name last\",\"officer name first\",\"officer name last\"," +
							"\"email\",\"campus\",\"notes\",\"uid\",\"sc_relative\") VALUES (?,?,?,?,?,?,?,?,?)";
					final PreparedStatement statement = c.prepareStatement(sql);
					statement.setString(1, studentFirstName.getValue());
					statement.setString(2, studentLastName.getValue());
					statement.setString(3, officerFirstName.getValue());
					statement.setString(4, officerLastName.getValue());
					statement.setString(5, email.getValue());
					statement.setString(6, campus.getValue());
					statement.setString(7, notes.getValue());
					statement.setString(8, uuid.toString());
					statement.setString(9, path);
					statement.execute();
					log.config("Added " + statement.getUpdateCount() + " row in Application with uuid " + uuid.toString());
					setResponsePage(ThankYou.class);
				} catch( Exception e ) {
					throw new RuntimeException(e);
				}
			}
		};
		form.setMultiPart(true);
		form.add(studentFirstName.setRequired(true));
		form.add(studentLastName.setRequired(true));
		form.add(email.setRequired(true));
		form.add(officerFirstName.setRequired(true));
		form.add(officerLastName.setRequired(true));
		form.add(campus.setRequired(true));
		form.add(notes);
		form.add(fileUpload.setRequired(true));
		add(form);
	}
}
