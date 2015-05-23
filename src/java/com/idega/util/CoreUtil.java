package com.idega.util;

import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;

public class CoreUtil {
	
	private static final Logger LOGGER = Logger.getLogger(CoreUtil.class.getName());
	
	public static IWBundle getCoreBundle() {
		return IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER);
	}
	
	public static IWContext getIWContext() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context == null) {
				return null;
			}
			return IWContext.getIWContext(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean sendExceptionNotification(HttpServletRequest request, User loggedInUser, Throwable exception) {
		return sendExceptionNotification(request, loggedInUser, null, exception);
	}

	public static boolean sendExceptionNotification(HttpServletRequest request, User loggedInUser, final String message, final Throwable exception) {
		return sendExceptionNotification(request, loggedInUser, message, exception, new File[0]);
	}

	public static boolean sendExceptionNotification(HttpServletRequest request, User loggedInUser, final String message, final Throwable exception, final File[] attachments) {
    	String serverName = null;
    	String requestUri = null;
		if (request != null) {
	    	serverName = request.getServerName();
	    	requestUri = request.getRequestURI();

	    	serverName = StringUtil.isEmpty(serverName) ? "unknown" : serverName;
	    	requestUri = StringUtil.isEmpty(requestUri) ? "unknown" : requestUri;
		}

		String user = null;
    	try {
    		user = loggedInUser == null ? null : (loggedInUser.getName() + ", user ID: " + loggedInUser.getId());
    	} catch (Exception e) {}
    	user = StringUtil.isEmpty(user) ? "not logged in" : user;

		final String server = serverName;
		final String requestedUri = requestUri;
		final String userFullName = user;
		Thread sender = new Thread(new Runnable() {

			public void run() {
				IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();

		    	Writer writer = null;
		    	PrintWriter printWriter = null;
		    	StringBuffer notification = null;
		    	try {
		    		if (exception != null) {
			    		writer = new StringWriter();
			    		printWriter = new PrintWriter(writer);
			    		exception.printStackTrace(printWriter);
		    		}

		    		notification = new StringBuffer("Requested uri: ").append(requestedUri).append("\n");
		    		notification.append("User: ").append(userFullName).append("\n");
		    		if (!StringUtil.isEmpty(message))
		    			notification.append("Message: ").append(message).append("\n");
		    		notification.append("Stack trace:\n").append(writer == null ? "Unavailable" : writer.toString());

		    		String receiver = settings.getProperty("exception_report_receiver", "abuse@idega.com");
		    		String subject = "EXCEPTION: on ePlatform, server: " + server;
		    		String text = notification.toString();
		    		if (EmailValidator.getInstance().validateEmail(receiver)) {
		    			SendMail.send(
		    					"idegaweb@idega.com",
		    					receiver,
		    					null,
		    					null,
		    					null,
		    					null,
		    					subject,
		    					text,
		    					false,
		    					true,
		    					attachments
		    			);
		    		} else {
		    			LOGGER.warning(subject + "\n" + text);
		    		}
		        } catch(Exception e) {
		        	LOGGER.log(Level.WARNING, "Error sending notification: " + notification, e);
		        } finally {
		        	close(writer);
		        	close(printWriter);
		        }
			}
		});
		sender.start();

    	return true;
	}

	private static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {}
	}
	
}