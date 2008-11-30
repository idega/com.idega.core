package com.idega.business;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This should be used ONLY when gluing old ibo with spring. See LoginSession
 * 
 * 
 * <p>Annotates spring managed beans interfaces for the purpose to let legacy non-spring objects, 
 * which were previously depending on IBO beans, lookup spring beans, instead of IBO beans, using their interface.</p>
 * 
 * <p><b>IMPORTANT:</b> if bean interface is annotated with this annotation, and IBOLookup.getSessionInstance method is used,
 * then <b>UnsupportedOperationException</b> will be thrown.</p>
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/11/30 08:05:22 $ by $Author: civilis $
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringBeanName {
	public abstract String value() default "foobar";
}