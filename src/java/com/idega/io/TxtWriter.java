/*
 * Created on May 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.io;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Row;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class TxtWriter extends DocWriter implements DocListener {
	/**
	 * Constructs a <CODE>TxtWriter</CODE>.
	 *
	 * @param doc     The <CODE>Document</CODE> that has to be written as TXT
	 * @param os      The <CODE>OutputStream</CODE> the writer has to write to.
	 */
	protected TxtWriter(Document doc, OutputStream os) {
		super(doc, os);
		document.addDocListener(this);
	}
	public static TxtWriter getInstance(Document document, OutputStream os) {
		return new TxtWriter(document, os);
	}
	/**
	 * Signals that an <CODE>Element</CODE> was added to the <CODE>Document</CODE>.
	 *
	 * @return  <CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.
	 * @throws  DocumentException when a document isn't open yet, or has been closed
	 */
	public boolean add(Element element) throws DocumentException {
		if (pause) {
			return false;
		}
		try {
			switch (element.type()) {
				case Element.HEADER :
				case Element.SUBJECT :
				case Element.KEYWORDS :
				case Element.AUTHOR :
				case Element.TITLE :
				case Element.CREATOR :
				case Element.PRODUCER :
				case Element.CREATIONDATE :
				default :
					write(element);
					return true;
			}
		}
		catch (IOException ioe) {
			throw new ExceptionConverter(ioe);
		}
	}
	/**
	 * Writes the TXT representation of an element.
	 *
	 * @param   element     the element
	 * @param   indent      the indentation
	 */
	protected void write(Element element) throws IOException {
		switch (element.type()) {
			case Element.CHUNK :
				{
					Chunk chunk = (Chunk) element;
					// if the chunk contains an image, return the image representation
					if (chunk.getImage() != null) {
						return;
					}
					if (chunk.isEmpty())
						return;
					HashMap attributes = chunk.getAttributes();
					if (attributes != null && attributes.get(Chunk.NEWPAGE) != null) {
						return;
					}
					// contents
					write((chunk.content()));
					return;
				}
			case Element.PHRASE :
				{
					Phrase phrase = (Phrase) element;
					for (Iterator i = phrase.iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					return;
				}
			case Element.ANCHOR :
				{
					Anchor anchor = (Anchor) element;
					/*
					if (anchor.name() != null) {
						write( anchor.name());
					}
					if (anchor.reference() != null) {
						write( anchor.reference());
					}
					*/
					// contents
					for (Iterator i = anchor.iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					// end tag
					return;
				}
			case Element.PARAGRAPH :
				{
					Paragraph paragraph = (Paragraph) element;
					// contents
					for (Iterator i = paragraph.iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					return;
				}
			case Element.SECTION :
			case Element.CHAPTER :
				{
					Section section = (Section) element;
					if (section.title() != null) {
						for (Iterator i = section.title().iterator(); i.hasNext();) {
							write((Element) i.next());
						}
					}
					for (Iterator i = section.iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					return;
				}
			case Element.LIST :
				{
					List list = (List) element;
					// start tag
					for (Iterator i = list.getItems().iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					// end tag
					return;
				}
			case Element.LISTITEM :
				{
					ListItem listItem = (ListItem) element;
					for (Iterator i = listItem.iterator(); i.hasNext();) {
						write((Element) i.next());
					}
					return;
				}
			case Element.CELL :
				{
					Cell cell = (Cell) element;
					// start tag
					if (cell.isEmpty()) {
						os.write(TAB);
					}
					else {
						for (Iterator i = cell.getElements(); i.hasNext();) {
							write((Element) i.next());
						}
					}
					return;
				}
			case Element.ROW :
				{
					Row row = (Row) element;
					// start tag
					Element cell;
					for (int i = 0; i < row.columns(); i++) {
						if ((cell = (Element) row.getCell(i)) != null) {
							write(cell);
						}
					}
					return;
				}
			case Element.TABLE :
				{
					Table table = (Table) element;
					table.complete();
					// start tag
					Row row;
					for (Iterator iterator = table.iterator(); iterator.hasNext();) {
						row = (Row) iterator.next();
						write(row);
					}
					return;
				}
			case Element.ANNOTATION :
				return;
			case Element.GIF :
			case Element.JPEG :
			case Element.PNG :
				return;
			default :
				return;
		}
	}
	/**
	 * Signals that a <CODE>String</CODE> was added to the <CODE>Document</CODE>.
	 *
	 * @return  <CODE>true</CODE> if the string was added, <CODE>false</CODE> if not.
	 * @throws  DocumentException when a document isn't open yet, or has been closed
	 */
	public boolean add(String string) throws DocumentException {
		if (pause) {
			return false;
		}
		try {
			write(string);
			return true;
		}
		catch (IOException ioe) {
			throw new ExceptionConverter(ioe);
		}
	}
}
