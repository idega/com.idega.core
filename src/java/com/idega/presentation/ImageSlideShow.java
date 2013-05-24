package com.idega.presentation;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.text.Link;
import com.idega.util.CoreConstants;

public class ImageSlideShow extends Block {

	public static int TOP = 1;
	public static int BOTTOM = 2;
	public static int OUTER = 4;
	public static int INNER = 8;
	private ICFile fileFolder;
	private List listOfFiles = null;
	private int width;
	private int height;
	private int buttonAlign = BOTTOM + OUTER;
	private int fileId = -1;
	private int delay = 0;
	private String alt;
	private boolean showButtons = true;
	private PresentationObject leftObject, rightObject;

	@Override
	public void main(IWContext iwc) {
		IWBundle iwb = getBundle(iwc);
		if (this.leftObject == null) {
			this.leftObject = iwb.getImage("arrowleft.gif");
		}
		if (this.rightObject == null) {
			this.rightObject = iwb.getImage("arrowright.gif");
		}
		if (this.fileFolder == null && this.fileId > 1) {
			try {
				ICFileHome fileHome = (ICFileHome) com.idega.data.IDOLookup.getHome(ICFile.class);
				this.fileFolder = fileHome.findByPrimaryKey(new Integer(this.fileId));
			}
			catch (Exception ex) {
			}
		}
		else if (this.listOfFiles != null && !this.listOfFiles.isEmpty()) {
			this.fileFolder = (ICFile) this.listOfFiles.get(0);
		}
		if (this.fileFolder != null) {
			Table T = new Table(2, 2);
			String name = "";
			Vector urls = new Vector();
			ICFile fileImage = null;
			int buttonRow = 2;
			int imageRow = 1;
			if ((this.buttonAlign & TOP) != 0) {
				imageRow = 2;
				buttonRow = 1;
			}
			// iterator init
			Iterator iter = null;
			int size = 1;
			int folderSize = this.fileFolder.getChildCount();
			if (this.fileFolder.getChildCount() > 0 && this.fileFolder.getChildrenIterator() != null) {
				iter = this.fileFolder.getChildrenIterator();
				size += folderSize;
			}
			else if (this.listOfFiles != null) {
				iter = this.listOfFiles.iterator();
				size = this.listOfFiles.size();
			}
			// iterator work
			Image image = new Image();
			name = "p_" + this.fileFolder.getPrimaryKey().toString();
			try {
				if (!this.fileFolder.isFolder()) {
					fileImage = this.fileFolder;
				}
				else if (iter != null && iter.hasNext()) {
					fileImage = (ICFile) iter.next();
				}
				if (fileImage != null) {
					image = new Image(fileImage);
					if (image != null) {
						if (this.width > 0) {
							image.setWidth(this.width);
						}
						if (this.height > 0) {
							image.setHeight(this.height);
						}
						if (this.alt != null) {
							image.setAlt(this.alt);
						}
					}
					try {
						String url = getICFileSystem(iwc).getFileURI(fileImage);
						urls.add(url);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					image.setName(name);
					T.add(image, 1, imageRow);
				}
			}
			catch (SQLException sql) {
			}
			if (iter != null && iter.hasNext()) {
				while (iter.hasNext()) {
					fileImage = (ICFile) iter.next();
					try {
						String url = getICFileSystem(iwc).getFileURI(fileImage);
						if (!urls.contains(url)) {
							urls.add(url);
						}
					}
					catch (Exception ex) {
						ex.printStackTrace(System.err);
					}
				}
				// layout
				T.mergeCells(1, imageRow, 2, imageRow);
				if (size > 1) {
					if (getParentPage() != null) {
						getParentPage().getAssociatedScript().addFunction("slide" + name, getSlideScript(name, urls));
					}
					if (this.showButtons) {
						T.add(getLeftLink(name), 1, buttonRow);
						T.add(getRightLink(name), 2, buttonRow);
						// T.add(getLeftButton(name),1,buttonRow);
						// T.add(getRightButton(name),2,buttonRow);
						if ((this.buttonAlign & INNER) != 0) {
							T.setAlignment(1, buttonRow, "right");
							T.setAlignment(2, buttonRow, "left");
						}
						else if ((this.buttonAlign & OUTER) != 0) {
							T.setAlignment(1, buttonRow, "left");
							T.setAlignment(2, buttonRow, "right");
						}
					}
				}
				else {
					T.mergeCells(1, 2, 2, 2);
				}
				T.setCellpadding(0);
				T.setCellspacing(0);
			}
			else {
				T.mergeCells(1, 2, 2, 2);
			}
			add(T);
			if (this.delay > 0) {
				add(getDelayScript(name));
			}
		}
	}

	private Link getLeftLink(String imageName) {
		return getLink(imageName, -1, this.leftObject);
	}

	private Link getRightLink(String imageName) {
		return getLink(imageName, 1, this.rightObject);
	}

	private Link getLink(String imageName, int step, PresentationObject object) {
		Link link = new Link(object);
		link.setURL(CoreConstants.HASH);
		link.setOnClick(getCallingScript(imageName, step));
		return link;
	}

	public void setFileFolder(ICFile imagefile) {
		this.fileFolder = imagefile;
	}

	public void setFileId(int iImageFileId) {
		this.fileId = iImageFileId;
	}

	public void setFiles(List listOfImageFiles) {
		this.listOfFiles = listOfImageFiles;
	}

	public void setDelay(int seconds) {
		if (seconds > 0) {
			this.delay = 1000 * seconds;
		}
	}

	@Override
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setHeight(String height) {
		this.height = Integer.parseInt(height);
	}

	public void setHeight(int height) {
		this.width = height;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setLeftImage(Image leftImage) {
		this.leftObject = leftImage;
	}

	public void setRightImage(Image rightImage) {
		this.rightObject = rightImage;
	}

	public void setShowButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}

	public void setButtonsInnerAlignment(boolean innerAlign) {
		if (innerAlign) {
			this.buttonAlign |= INNER;
		}
		else {
			setButtonsOuterAlignment(true);
		}
	}

	public void setButtonsOuterAlignment(boolean outerAlign) {
		if (outerAlign) {
			this.buttonAlign |= OUTER;
		}
		else {
			setButtonsInnerAlignment(true);
		}
	}

	public void setButtonsTopAlignment(boolean topAlign) {
		if (topAlign) {
			this.buttonAlign |= TOP;
		}
		else {
			setButtonsBottomAlignment(true);
		}
	}

	public void setButtonsBottomAlignment(boolean bottomAlign) {
		if (bottomAlign) {
			this.buttonAlign |= TOP;
		}
		else {
			setButtonsTopAlignment(true);
		}
	}

	private String getCallingScript(String name, int step) {
		return "javascript:slide_" + name + "('" + step + "');";
	}

	private Script getDelayScript(String name) {
		StringBuffer addPics = new StringBuffer();
		addPics.append("setInterval('").append(getCallingScript(name, 1)).append("',").append(this.delay).append(");");
		Script s = new Script();
		s.addFunction(name + "load", addPics.toString());
		return s;
	}

	private String getSlideScript(String name, List urls) {
		String sCurrent = "current_" + name;
		String sPicArray = "picsources" + name;
		String sPreloadArray = "picPreload" + name;
		String sSlide = "slide_" + name;
		StringBuffer addPics = new StringBuffer();
		// addPics.append("var ").append(sDelay).append(" = 0;\n");
		addPics.append("var ").append(sCurrent).append(" = ").append(this.delay).append(";\n");
		addPics.append("var ").append(sPicArray).append(" = new Array(");
		int size = urls.size();
		for (int i = 0; i < size; i++) {
			String url = (String) urls.get(i);
			// if not last one
			if (i != (size - 1)) {
				addPics.append("\"").append(url).append("\",");
			}
			else {
				addPics.append("\"").append(url).append("\");\n");
			}

		}

		addPics.append("var ").append(sPreloadArray).append(" = new Array();\n").append("for (i=0;i<").append(sPicArray).append(".length;i++){\n").append("\t").append(sPreloadArray).append("[i]=new Image();\n").append("\t").append(sPreloadArray).append("[i].src=").append(sPicArray).append("[i];\n}\n");

		addPics.append("function ").append(sSlide).append("(val) {\n\t").append(sCurrent).append(" =").append(sCurrent).append("+parseInt(val);\n").append("\t").append("if(").append(sCurrent).append(">=").append(sPicArray).append(".length){\n").append("\t").append(sCurrent).append("=0;\n\t}").append("else if(").append(sCurrent).append("<").append("0){\n").append("\t\t").append(sCurrent).append("=").append(sPicArray).append(".length-1;\n\t}\n");

		addPics.append("\tdocument.images.").append(name).append(".src = ");
		addPics.append(sPreloadArray).append("[").append(sCurrent).append("].src;\n");
		addPics.append("}\n");
		return addPics.toString();
	}

	@Override
	public Object clonePermissionChecked(IWUserContext iwc) {
		ImageSlideShow obj = null;
		try {
			obj = (ImageSlideShow) super.clone();
		}
		catch (Exception ex) {
		}
		return obj;
	}
} // class ImageRotater
