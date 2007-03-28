package com.idega.business;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates spring managed beans interfaces for the purpose to let legacy non-spring objects, 
 * which were previously depending on IBO beans, lookup spring beans, instead of IBO beans, using their interface.</p>
 * 
 * <p><b>IMPORTANT:</b> if bean interface is annotated with this annotation, and IBOLookup.getSessionInstance method is used,
 * then <b>UnsupportedOperationException</b> will be thrown.</p>
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringBeanName {
	public abstract String value() default "foobar";
}