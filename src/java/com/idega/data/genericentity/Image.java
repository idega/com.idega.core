package com.idega.data.genericentity;

import java.sql.*;
import com.idega.data.*;


public class Image extends GenericEntity{

	public Image(){
		super();
	}

	public Image(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("content_type","Image type",true,true,"java.lang.String");
		addAttribute("image_value","The image",false,false,"java.sql.Blob");
		addAttribute("image_name","Image name",true,true,"java.lang.String");
		addAttribute("date_added","Date added or changed",true,true,"java.sql.Timestamp");
		addAttribute("from_file","Image from file?",true,true,"java.lang.Boolean");

                //tillaga um viðbætur - gummi
/*                addAttribute("attribute","Attirbute",true,true,"java.lang.String");
		addAttribute("attribute_value","Attribute value",true,true,"java.lang.String");
*/
                //added november by eiki, idega iceland
           /*     addAttribute("image_text","Image text",true,true,"java.lang.String");
                addAttribute("image_link","Image link",true,true,"java.lang.String");
                addAttribute("image_link_owner","Which has a link the image/text/both/none?",true,true,"java.lang.String");
                addAttribute("width","Image width",true,true,"java.lang.String");
                addAttribute("height","Image height",true,true,"java.lang.String");
                addAttribute("parent_id","Image parent",true,true,"java.lang.Integer");*/


	}

	public String getEntityName(){
		return "image";
	}

         public void setDefaultValues() {
        //  this.setParentId(-1);
          this.setFromFile("N");
       //   this.setImageLinkOwner("none");
          this.setDate(new com.idega.util.idegaTimestamp().getTimestampRightNow());
        }

	public String getContentType(){
		return (String) getColumnValue("content_type");
	}

	public void setContentType(String contentType){
		setColumn("content_type", contentType);
	}

	public Blob getImageValue(){
		return (Blob) getColumnValue("image_value");
	}

	public void setImageValue(Blob imageValue){
		setColumn("image_value", imageValue);
	}

	public String getName(){
          return getImageName();
	}

        public String getImageName(){
          return (String) getColumnValue("image_name");
        }

         public void setImageName(String name){
          setColumn("image_name", name);
        }

	public void setName(String name){
          setImageName(name);
	}

        public String getFromFile(){
          return (String) getColumnValue("from_file");
        }

        public void setFromFile(String fromFile){
          setColumn("from_file", fromFile);
        }

	public Timestamp getDateAdded(){
          return (Timestamp) getColumnValue("date_added");
	}

        public Timestamp getDate(){
          return  getDateAdded();
	}

	public void setDateAdded(Timestamp dateAdded){
		setColumn("date_added", dateAdded);
	}

        public void setDate(Timestamp dateAdded){
          setDateAdded(dateAdded);
	}

        public String getText(){
         return getImageText();
        }
        public String getImageText(){
		return (String) getColumnValue("image_text");
	}

	public void setImageText(String imageText){
		setColumn("image_text", imageText);
	}

        public String getImageLink(){
		return (String) getColumnValue("image_link");
	}

        public String getLink(){
		return getImageLink();
	}

	public void setImageLink(String imageLink){
		setColumn("image_link", imageLink);
	}

        public String getWidth(){
		return (String) getColumnValue("width");
	}

	public void setWidth(String imageWidth){
		setColumn("width", imageWidth);
	}

        public String getHeight(){
		return (String) getColumnValue("height");
	}

	public void setHeight(String imageHeight){
		setColumn("height", imageHeight);
	}

        public String getImageLinkOwner(){
		return (String) getColumnValue("image_link_owner");
	}

        /*
        * possible option image/text/both/none
        */
	public void setImageLinkOwner(String imageLinkOwner){
		setColumn("image_link_owner", imageLinkOwner);
	}

        public void setParentId(int parent_id) {
          setColumn("parent_id",new Integer(parent_id));
        }

        public int getParentId() {
          return getIntColumnValue("parent_id");
        }


}
