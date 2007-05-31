package com.idega.util;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

public class CoreUtil {
	
	private static final BASE64Encoder ENCODER_BASE64 = new BASE64Encoder();
	private static final BASE64Decoder DECODER_BASE64 = new BASE64Decoder();
	
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
	
	public static void addJavaSciptForChooser(IWContext iwc) {
		AddResource adder = AddResourceFactory.getInstance(iwc);
		
		IWBundle iwb = getCoreBundle();
		
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, iwb.getVirtualPathWithFileNameString("javascript/ChooserHelper.js"));
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, "/dwr/engine.js");
		adder.addJavaScriptAtPosition(iwc, AddResource.HEADER_BEGIN, "/dwr/interface/ChooserService.js");
	}
	
	public static String getEncodedValue(String originalValue) {
		if (originalValue == null) {
			return null;
		}
		return ENCODER_BASE64.encode(originalValue.getBytes());
	}
	
	public static String getDecodedValue(String encodedValue) {
		if (encodedValue == null) {
			return null;
		}
		try {
			return new String(DECODER_BASE64.decodeBuffer(encodedValue));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
