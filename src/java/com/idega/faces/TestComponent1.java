/*
 * Created on 20.2.2004 by  tryggvil in project com.project
 */
package com.idega.faces;

import javax.faces.component.UIComponent;

//import com.idega.block.login.presentation.Login;
//import com.idega.block.news.presentation.News;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.app.IWControlCenter;
import com.idega.presentation.text.Text;

/**
 * TestComponent1 //TODO: tryggvil Describe class
 * Copyright (C) idega software 2004
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TestComponent1 extends IWBaseComponent {
	

	private Table table;
	
	public TestComponent1(){
			this.table = new Table(2,3);
			add(this.table);
			this.table.setBorder(2);
			
			Text t1 = new Text("Hello from idegaWeb");
			t1.setFontColor("red");
			this.table.add(t1,2,2);
			//Text t2 = new Text("idegaWeb Text2");
			//t2.setFontColor("blue");	
			//table.add(t2,2,3);
			
			IWControlCenter cs = new IWControlCenter();
			this.table.add(cs,2,1);
			
			Image image = new Image("/smile/idegaweb/bundles/com.idega.core.bundle/resources/en.locale/login/header.jpg");
			this.table.add(image,1,1);
			
			/*
			News news = new News();
			news.setICObjectInstanceID(1);
			//news.setCategoryId(1);
			table.add(news,1,2);
			
			//Table table2 = new Table(10,10);
			//table2.setBorder(2);
			//table.add(table2,2,1);
			
			Login login = new Login();
			//news.setCategoryId(1);
			table.add(login,1,3);
			*/
			
	}
	
	
	public void addToTable(UIComponent component){
		this.table.add(component,2,3);
	}
	
}
