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
import javax.faces.context.FacesContext;
import javax.faces.view.Location;
import javax.faces.view.facelets.CompositeFaceletHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.apache.myfaces.view.facelets.FaceletFactory;
import org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguageStrategy;
import org.apache.myfaces.view.facelets.compiler.Compiler;
import org.apache.myfaces.view.facelets.el.VariableMapperWrapper;
import org.apache.myfaces.view.facelets.tag.TagAttributeImpl;
import org.apache.myfaces.view.facelets.tag.TagAttributesImpl;
import org.apache.myfaces.view.facelets.tag.ui.ParamHandler;


/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2007/11/22 16:41:39 $ by $Author: civilis $
 *
 */
public class FaceletFactoryFactory {
	
	/**
	 * Method should be called only, when current FacesContext is available
	 * @return FaceletFactory
	 */
	public synchronized FaceletFactory createFaceletFactory() {
		return createFaceletFactory(null);
	}
	
	/**
	 * Method should be called only, when current FacesContext is available
	 * @return FaceletFactory, new each time, contrary to when not passing any parameters
	 */
	public synchronized FaceletFactory createFaceletFactory(List<UIParameter> params) {
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		IWFaceletViewDeclarationLanguage language = new IWFaceletViewDeclarationLanguage(facesContext, new FaceletViewDeclarationLanguageStrategy());
        Compiler compiler = language.getNewCompiler(facesContext);
		if(params != null && params.size()>0){
			compiler = new IWCompiler(compiler, params);
		} 
		
		return language.getNewFaceletFactory(facesContext, compiler);
    }

	public class IWCompiler extends Compiler {
		
		private Compiler compiler;
		private List<UIParameter> params;
		
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
			final FaceletHandler wrapper = new IWIncludeHandler(compiler, src, alias, paramHandlers);
			return wrapper;
		}

		@Override
		protected FaceletHandler doCompileViewMetadata(URL src, String alias)
				throws IOException, FaceletException, ELException,
				FacesException {
			return compiler.compileViewMetadata(src, alias);
		}

		@Override
		protected FaceletHandler doCompileCompositeComponentMetadata(URL src,
				String alias) throws IOException, FaceletException,
				ELException, FacesException {
			return compiler.compileCompositeComponentMetadata(src, alias);
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
			attributes[0] = new TagAttributeImpl(location,ns,"name","name",name);
			attributes[1] = new TagAttributeImpl(location,ns,"value","value",value);
			theTag = new Tag(location, ns, "param", "ui:param", new TagAttributesImpl(attributes));
		}
		
		public FaceletHandler getNextHandler() {
			return this.nextHandler;
		}

		public Tag getTag() {
			return theTag;
		}

		public String getTagId() {
			return tagId;
		}
	}
	
	
	private class FakeTagConfig implements TagConfig {
		

		private final String tagId = "_" + UUID.randomUUID().toString();
		private FaceletHandler nextHandler;
		private Tag theTag = new Tag(new Location("iwInclude", -1, -1), "", "include", "iw:include", new TagAttributesImpl(new TagAttribute[0]));
		
		public FakeTagConfig(FaceletHandler nextHandler) {
			this.nextHandler = nextHandler;
		}

		public FaceletHandler getNextHandler() {
			return this.nextHandler;
		}

		public Tag getTag() {
			return theTag;
		}

		public String getTagId() {
			return tagId;
		}
	}
	
	public class IWIncludeHandler extends TagHandler {
		
		private Compiler compiler;
		private String alias;
		private URL src;
		
		public IWIncludeHandler(Compiler compiler, URL src, String alias, FaceletHandler paramHandlers ){
			super(new FakeTagConfig(paramHandlers));
			this.compiler = compiler;
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
	            FaceletHandler root = compiler.compile(src, alias);
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