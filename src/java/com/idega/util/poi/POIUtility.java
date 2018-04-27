/*
 * Created on 5.2.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.util.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.faces.component.UIComponent;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.Table;
import com.idega.presentation.Table2;
import com.idega.presentation.TableBodyRowGroup;
import com.idega.presentation.TableCell;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.text.Text;
import com.idega.util.CoreConstants;
import com.idega.util.text.TextSoap;

/**
 * Title:		POIUtility
 * Description: A utility class of facilitate use of idegaWeb objects and POI
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gimmi@idega.is">Grimur Jonsson</a><br>
 * @version		1.0
 */
public class POIUtility {

	/**
	 * Creates an excel document from Table. Currently this only
	 * supports table cells with Text objects.
	 * @param table
	 * @param fileName
	 * @param sheetName
	 * @return Returns True if file creation was a success, otherwise False.
	 */
	public static File createFileFromTable(Table2 table, String fileName, String sheetName) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(TextSoap.encodeToValidExcelSheetName(sheetName));

		Collection<UIComponent> hRows = table.createHeaderRowGroup().getChildren();
		int row = addTableRows(sheet, hRows, 0);
		Collection<UIComponent> bRows = table.getBodyRowGroups();
		Iterator<UIComponent> brIter = bRows.iterator();
		while (brIter.hasNext()) {
			TableBodyRowGroup bRow = (TableBodyRowGroup) brIter.next();
			row = addTableRows(sheet, bRow.getChildren(), row);
		}
		Collection<UIComponent> fRows = table.createFooterRowGroup().getChildren();
		row = addTableRows(sheet, fRows, row);

		try {
			if (bRows != null) {
				UIComponent body = bRows.iterator().next();
				if (body instanceof TableBodyRowGroup) {
					TableBodyRowGroup tableBody = (TableBodyRowGroup) body;
					TableRow bodyRow = tableBody.getRow(0);
					if (bodyRow != null) {
						Collection<UIComponent> cells = bodyRow.getCells();
						if (cells != null) {
							for (int i = 0; i < cells.size(); i++) {
								sheet.autoSizeColumn(i);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(POIUtility.class.getName()).log(Level.WARNING, "Error auto sizing columns", e);
		}

		// Write the output to a file
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(fileName);
			wb.write(fileOut);
			fileOut.close();

			File file = new File(fileName);

			return file;
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		return null;
	}

	private static int addTableRows(HSSFSheet sheet, Collection<UIComponent> hRows, int startRow) {
		Iterator<UIComponent> rowsIter = hRows.iterator();
		Pattern sp = Pattern.compile(Text.NON_BREAKING_SPACE, Pattern.CASE_INSENSITIVE);
		Pattern br = Pattern.compile(Text.BREAK, Pattern.CASE_INSENSITIVE);
		Text obj = null;
		String text;
		while (rowsIter.hasNext()) {
			TableRow tRow = (TableRow) rowsIter.next();
			HSSFRow row = sheet.createRow(startRow);
			sheet.setRowSumsBelow(true);
			Collection<UIComponent> hCells = tRow.getCells();
			Iterator<UIComponent> hIter = hCells.iterator();
			int cellCounter = 0;
			while (hIter.hasNext()) {
				TableCell2 tCell = (TableCell2) hIter.next();
				if (tCell.getChildCount() == 1) {
					obj = (Text) tCell.getChildren().iterator().next();
				}
				if (obj != null) {
					text = obj.toString();
					if (text == null) {
						text = CoreConstants.EMPTY;
					}
					text = sp.matcher(text).replaceAll(" ");
					text = br.matcher(text).replaceAll("\n");
					row.createCell(cellCounter).setCellValue(text);
				}
				cellCounter++;
			}
			startRow++;
		}
		return startRow;
	}

	public static File createFileFromTable(Table table, String fileName, String sheetName) {
		int rows = table.getRows();
		int cols = table.getColumns();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(TextSoap.encodeToValidExcelSheetName(sheetName));

		Text obj;
		String text = null;

		Pattern sp = Pattern.compile(Text.NON_BREAKING_SPACE, Pattern.CASE_INSENSITIVE);
		Pattern br = Pattern.compile(Text.BREAK, Pattern.CASE_INSENSITIVE);

		for (int x = 1; x <= rows; x++) { // has to start on 1, because getCellAt does (x-1, y-1)
			HSSFRow row = sheet.createRow((x-1));
			sheet.setRowSumsBelow(true);
			for (int y = 1; y <= cols; y++) {// has to start on 1, because getCellAt does (x-1, y-1)
				TableCell cell = table.getCellAt(y, x);
				if (cell.getChildCount() > 1) {
					List<UIComponent> children = cell.getChildren();
					Iterator<UIComponent> it = children.iterator();
					boolean firstText = true;
					StringBuffer buffer = new StringBuffer(CoreConstants.EMPTY);
					while (it.hasNext()) {
						Object element = it.next();
						if (element.getClass() == Text.class) {
							if (firstText) {
								firstText = false;
							} else {
								buffer.append(", ");
							}
							buffer.append(((Text)element).getText());
						}
					}
					text = buffer.toString();
				} else {
					obj = (Text) table.getCellAt(y, x).getContainedObject(Text.class);
					if (obj != null) {
						text = obj.toString();
					}
				}

				if (text == null) {
					text = CoreConstants.EMPTY;
				}

				text = sp.matcher(text).replaceAll(" ");
				text = br.matcher(text).replaceAll("\n");
				row.createCell((y-1)).setCellValue(text);
			}
		}
		// Write the output to a file
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(fileName);
			wb.write(fileOut);
			fileOut.close();

			File file = new File(fileName);

			return file;
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		return null;
	}

	public static ICFile createICFileFromTable(Table table, String fileName, String sheetName) {
		try {
			File file = createFileFromTable(table, fileName, sheetName);
			InputStream inStream = new FileInputStream(file);
			ICFile icFile = ((ICFileHome) IDOLookup.getHome(ICFile.class)).create();
			icFile.setFileValue(inStream);
			icFile.setMimeType("application/vnd.ms-excel");
			icFile.setName(fileName);
			icFile.store();

			return icFile;
		} catch (FileNotFoundException e) {
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}

		return null;
	}
}
