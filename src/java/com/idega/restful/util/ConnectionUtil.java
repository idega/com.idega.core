package com.idega.restful.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.util.CoreConstants;

public class ConnectionUtil {

	private static final Logger LOGGER = Logger.getLogger(ConnectionUtil.class.getName());
	
	private static final ConnectionUtil instance = new ConnectionUtil();
	
	private ConnectionUtil() {};
	
	public static final ConnectionUtil getInstance() {
		return instance;
	}
	
	public AdvancedProperty getResponseFromREST(
			String uri,
			String type,
			String method,
			String data,
			List headerParams,
			List pathParams,
			List queryParams
	) {
		try {
			byte[] postData = data.getBytes(CoreConstants.ENCODING_UTF8);
			int postDataLength = postData.length;
			String request = uri;
			URL url = new URL(request);
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput(true);
	        conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod(method);
			conn.setRequestProperty("Content-Type", type); 
			conn.setRequestProperty("charset", CoreConstants.ENCODING_UTF8);
			conn.setRequestProperty("Content-Length", String.valueOf(postDataLength));
			conn.setUseCaches(false);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			wr.close();
			
	        int httpResult = conn.getResponseCode();
	        if (httpResult == HttpURLConnection.HTTP_OK) {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), CoreConstants.ENCODING_UTF8));  
	 
	            String line = null;
	            StringBuilder sb = new StringBuilder();  
	            while ((line = br.readLine()) != null) {  
	            	sb.append(line).append(CoreConstants.NEWLINE);  
	            }
	            br.close(); 
	            
	            return new AdvancedProperty(httpResult, sb.toString());
	        }
	        
	        String errorResponse = conn.getResponseMessage();
	        LOGGER.warning("HTTP code: " + httpResult + ", response: " + errorResponse + ": error calling RESTful WS at " + uri + ", type " + type + ", method " + method +
	        		". Header params: " + headerParams + ", path params: " + pathParams + ", data: " + data + ", query params: " + queryParams);
	        return new AdvancedProperty(httpResult, errorResponse);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error calling RESTful WS at " + uri + ", type " + type + ", method " + method + ". Header params: " + headerParams + ", path params: " +
					pathParams + ", data: " + data + ", query params: " + queryParams, e);
		}
		return null;
	}
	
} 