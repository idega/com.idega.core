package com.idega.facelets.ui;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.facelets.tag.CompositeFaceletHandler;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.ui.ParamHandler;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2007/11/22 16:41:39 $ by $Author: civilis $
 *
 */
public class FaceletFactoryFactory {
	
	private Compiler compiler;
	private ResourceResolver resourceResolver;
	private FaceletFactory faceletFactory;
	
	/**
	 * Method should be called only, when current FacesContext is available
	 * @return FaceletFactory
	 */
	public synchronized FaceletFactory createFaceletFactory() {
		
		if(faceletFactory == null) {
			
	        long refreshPeriod = FaceletViewHandler.DEFAULT_REFRESH_PERIOD;
	        faceletFactory = new DefaultFaceletFactory(getCompiler(), getResourceResolver(), refreshPeriod);
		}
		
		return faceletFactory;
	}
	
	/**
	 * Method should be called only, when current FacesContext is available
	 * @return FaceletFactory, new each time, contrary to when not passing any parameters
	 */
	public synchronized FaceletFactory createFaceletFactory(List<UIParameter> params) {
		
		if(params == null || params.size()<1){
			return createFaceletFactory();
		} else {
	        long refreshPeriod = FaceletViewHandler.DEFAULT_REFRESH_PERIOD;
	        faceletFactory = new DefaultFaceletFactory(new IWCompiler(getCompiler(), params), getResourceResolver(), refreshPeriod);
			return faceletFactory;
		}
    }

	public Compiler getCompiler() {
		return compiler;
	}

	public void setCompiler(Compiler compiler) {
		this.compiler = compiler;
	}

	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	public void setResourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}
	
	public class IWCompiler extends Compiler {
		
		Compiler compiler;
		List<UIParameter> params;
		
		public IWCompiler(Compiler compiler, List<UIParameter> params){
			this.compiler = compiler;
			this.params = params;
		}

		@Override
		protected FaceletHandler doCompile(URL src, String alias)
				throws IOException, FaceletException, ELException,
				FacesException {
//			final FaceletHandler nextHandler = compiler.compile(src, alias);
			final FaceletHandler leaf = new FaceletHandler() {
				public void apply(FaceletContext ctx, UIComponent parent)
						throws IOException {
				}
		
				public String toString() {
					return "FaceletHandler Tail";
				}
			};
			
			ArrayList<FaceletHandler> paramHandlersList = new ArrayList<FaceletHandler>();
			for(UIParameter p : params){
				ValueExpression nameExp = p.getValueExpression("name");
				String name = null;
				if(nameExp != null){
					name = nameExp.getExpressionString();
				} else{
					name = p.getName();
				}
				ValueExpression valueExp = p.getValueExpression("value");
				String value = null;
				if(valueExp != null){
					value = valueExp.getExpressionString();
				} else{
					try {
						value = (String)p.getValue();
					} catch (ClassCastException e) {
						getLogger().log(Level.WARNING, "A non-String value can only be passed as an expression to an included facelet.", e);
					}
				}
				ParamConfig conf = new ParamConfig(leaf,name,value);
				paramHandlersList.add(new ParamHandler(conf));
			}
			
			final CompositeFaceletHandler paramHandlers = new CompositeFaceletHandler((FaceletHandler[])paramHandlersList.toArray(new FaceletHandler[paramHandlersList.size()]));
			final FaceletHandler wrapper = new IWIncludeHandler(src, alias, paramHandlers);
			return wrapper;
		}
		
	}
	
	private class ParamConfig implements TagConfig {
		private final String tagId = "_" + UUID.randomUUID().toString();
		private FaceletHandler nextHandler;
		private Tag theTag;
		private Location location = new Location("iwInclude", -1, -1);
		private String ns = ""; //"http://java.sun.com/jsf/facelets";
		
		public ParamConfig(FaceletHandler nextHandler,String name, String value) {
			this.nextHandler = nextHandler;
			TagAttribute attributes[] = new TagAttribute[2];
			attributes[0] = new TagAttribute(location,ns,"name","name",name);
			attributes[1] = new TagAttribute(location,ns,"value","value",value);
			theTag = new Tag(location, ns, "param", "ui:param", new TagAttributes(attributes));
		}
		
		@Override
		public FaceletHandler getNextHandler() {
			return this.nextHandler;
		}

		@Override
		public Tag getTag() {
			return theTag;
		}

		@Override
		public String getTagId() {
			return tagId;
		}
	}
	
	
	private class FakeTagConfig implements TagConfig {
		

		private final String tagId = "_" + UUID.randomUUID().toString();
		private FaceletHandler nextHandler;
		private Tag theTag = new Tag(new Location("iwInclude", -1, -1), "", "include", "iw:include", new TagAttributes(new TagAttribute[0]));
		
		public FakeTagConfig(FaceletHandler nextHandler) {
			this.nextHandler = nextHandler;
		}

		@Override
		public FaceletHandler getNextHandler() {
			return this.nextHandler;
		}

		@Override
		public Tag getTag() {
			return theTag;
		}

		@Override
		public String getTagId() {
			return tagId;
		}
	}
	
	public class IWIncludeHandler extends TagHandler {
		
		private String alias;
		private URL src;
		
		public IWIncludeHandler(URL src, String alias, FaceletHandler paramHandlers ){
			super(new FakeTagConfig(paramHandlers));
			this.alias=alias;
			this.src = src;
		}
		
		
		public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
	        VariableMapper orig = ctx.getVariableMapper();
	        ctx.setVariableMapper(new VariableMapperWrapper(orig));
	        try {
	        	//apply ui:param
	            this.nextHandler.apply(ctx, null);
	            //include facelet
	            FaceletHandler root = getCompiler().compile(src, alias);
	            root.apply(ctx, parent);
//	            ctx.includeFacelet(parent, alias);
	        } finally {
	            ctx.setVariableMapper(orig);
	        }
		}		
	}
	
	protected Logger getLogger(){
		return Logger.getLogger(getClass().getName());
	}	
	
}