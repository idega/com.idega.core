/**
 * 
 */
package com.idega.core.accesscontrol.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * Implementation of the JAAS CallbackHandler used by the idegaWeb Authentication mechanism.
 * </p>
 *  Last modified: $Date: 2007/01/22 08:10:29 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class IWCallbackHandler implements CallbackHandler {
		
	private HttpServletRequest request = null;
	
	public IWCallbackHandler(HttpServletRequest request) {
		this.request = request; 
	}
		
	public void handle(Callback[] callbacks) {
		for (int i = 0; i < callbacks.length; i++) {
			Callback callback = callbacks[i];
			if (callback instanceof PasswordCallback) {
				// prompt the user for sensitive information
				PasswordCallback pc = (PasswordCallback)callback;
				String prompt = pc.getPrompt();
				String password = this.request.getParameter(prompt);
				pc.setPassword((password == null) ? null : password.toCharArray());
			}
			else if (callback instanceof NameCallback) {
				NameCallback nc = (NameCallback) callback;
				String prompt = nc.getPrompt();
				String name = this.request.getParameter(prompt);
				nc.setName(name);
			}
		}
	}
}
