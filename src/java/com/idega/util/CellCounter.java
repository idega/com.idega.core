package com.idega.util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class CellCounter
{
    int totalRows;
    int totalCols;
    int row;
    int col;
    int count;
    boolean fillVertical;

    public CellCounter(int Columns, int Rows)
    {
        totalRows = 1;
        totalCols = 1;
        row = 0;
        col = 0;
        count = 0;
        fillVertical = false;
        totalRows = Rows;
        totalCols = Columns;
    }

    public void setFillVertical(boolean fill)
    {
        fillVertical = fill;
    }

    public int getColumn()
    {
        return col;
    }

    public int getRow()
    {
        return row;
    }

    public void reset()
    {
        row = 0;
        col = 0;
        count = 0;
    }

    public boolean hasNext()
    {
        if (count < totalRows * totalCols)
            return true;
        else
            return false;
    }

    public void next()
    {
        calculateNext();
    }

    private void calculateNext()
    {
        if (fillVertical)
        {
            row++;
            if (row > totalRows)
            {
                row = 1;
                col++;
            }
            if (count == 0)
                col = 1;
        }
        else
        {
            col++;
            if (col > totalCols)
            {
                col = 1;
                row++;
            }
            if (count == 0)
                row = 1;
        }
        count++;
    }
}
