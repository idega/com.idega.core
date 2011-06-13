package com.idega.facelets.ui;

import javax.faces.context.FacesContext;

import org.apache.myfaces.view.ViewDeclarationLanguageStrategy;
import org.apache.myfaces.view.facelets.FaceletFactory;
import org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguage;
import org.apache.myfaces.view.facelets.compiler.Compiler;

public class IWFaceletViewDeclarationLanguage extends FaceletViewDeclarationLanguage {

	public IWFaceletViewDeclarationLanguage(FacesContext context) {
		super(context);
	}
	
	public IWFaceletViewDeclarationLanguage(FacesContext context, ViewDeclarationLanguageStrategy strategy) {
		super(context, strategy);
	}

	public Compiler getNewCompiler(FacesContext context){
		return createCompiler(context);
	}
	
	public FaceletFactory getNewFaceletFactory(FacesContext context, Compiler compiler){
		return createFaceletFactory(context, compiler);
	}

}
