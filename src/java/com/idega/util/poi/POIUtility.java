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
import java.util.regex.Pattern;

import javax.ejb.CreateException;

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
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.text.Text;
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
//		int rows = table.getRows();
//		int cols = table.getColumns();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(TextSoap.encodeToValidExcelSheetName(sheetName));

		
		
		Collection hRows = table.createHeaderRowGroup().getChildren();
		int row = addTableRows(sheet, hRows, 0);
		Collection bRows = table.getBodyRowGroups();
		Iterator brIter = bRows.iterator();
		while (brIter.hasNext()) {
			TableBodyRowGroup bRow = (TableBodyRowGroup) brIter.next();
			row = addTableRows(sheet, bRow.getChildren(), row);
		}
		Collection fRows = table.createFooterRowGroup().getChildren();
		row = addTableRows(sheet, fRows, row);

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

	private static int addTableRows(HSSFSheet sheet, Collection hRows, int startRow) {
		Iterator rowsIter = hRows.iterator();
		Pattern sp = Pattern.compile(Text.NON_BREAKING_SPACE, Pattern.CASE_INSENSITIVE);
		Pattern br = Pattern.compile(Text.BREAK, Pattern.CASE_INSENSITIVE);
		Text obj = null;
		String text;
		while (rowsIter.hasNext()) {
			TableRow tRow = (TableRow) rowsIter.next();
			HSSFRow row = sheet.createRow(startRow);
			sheet.setRowSumsBelow(true);
			Collection hCells = tRow.getCells();
			Iterator hIter = hCells.iterator();
			int cellCounter = 0;
			while (hIter.hasNext()) {
				TableCell2 tCell = (TableCell2) hIter.next();
				if (tCell.getChildCount() == 1) {
					obj = (Text) tCell.getChildren().iterator().next();
				}
				if (obj != null) {
					text = obj.toString();
					if (text == null) {
						text = "";
					}
					text = sp.matcher(text).replaceAll(" ");
					text = br.matcher(text).replaceAll("\n");
					row.createCell((short)cellCounter).setCellValue(text);
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
		String text;
		
		Pattern sp = Pattern.compile(Text.NON_BREAKING_SPACE, Pattern.CASE_INSENSITIVE);
		Pattern br = Pattern.compile(Text.BREAK, Pattern.CASE_INSENSITIVE);
		
		for (int x = 1; x <= rows; x++) { // has to start on 1, because getCellAt does (x-1, y-1)
			HSSFRow row = sheet.createRow((x-1));
			sheet.setRowSumsBelow(true);
			for (int y = 1; y <= cols; y++) {// has to start on 1, because getCellAt does (x-1, y-1)
				obj = (Text) table.getCellAt(y, x).getContainedObject(Text.class);
				if (obj != null) {
					text = obj.toString();
					if (text == null) {
						text = "";
					}
					text = sp.matcher(text).replaceAll(" ");
					text = br.matcher(text).replaceAll("\n");
					row.createCell((short)(y-1)).setCellValue(text);
				}
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
