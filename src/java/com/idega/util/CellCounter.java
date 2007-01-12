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
        this.totalRows = 1;
        this.totalCols = 1;
        this.row = 0;
        this.col = 0;
        this.count = 0;
        this.fillVertical = false;
        this.totalRows = Rows;
        this.totalCols = Columns;
    }

    public void setFillVertical(boolean fill)
    {
        this.fillVertical = fill;
    }

    public int getColumn()
    {
        return this.col;
    }

    public int getRow()
    {
        return this.row;
    }

    public void reset()
    {
        this.row = 0;
        this.col = 0;
        this.count = 0;
    }

    public boolean hasNext()
    {
        if (this.count < this.totalRows * this.totalCols) {
			return true;
		}
		else {
			return false;
		}
    }

    public void next()
    {
        calculateNext();
    }

    private void calculateNext()
    {
        if (this.fillVertical)
        {
            this.row++;
            if (this.row > this.totalRows)
            {
                this.row = 1;
                this.col++;
            }
            if (this.count == 0) {
				this.col = 1;
			}
        }
        else
        {
            this.col++;
            if (this.col > this.totalCols)
            {
                this.col = 1;
                this.row++;
            }
            if (this.count == 0) {
				this.row = 1;
			}
        }
        this.count++;
    }
}
