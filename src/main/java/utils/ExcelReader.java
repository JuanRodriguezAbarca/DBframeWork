package main.java.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

;

/**
 * Created by Juan_Rodriguez on 1/14/2016.
 */
public class ExcelReader {



    public String[][] readingExcelFile(Integer sheetIndex)
    throws IOException {

        System.out.println("Reading the file...");

        File myFile = new File(".\\data\\ListaDeJuegos.xlsx");

        FileInputStream fis = new FileInputStream(myFile);

        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);

        Iterator<Row> rowIterator = mySheet.iterator();

        int numOfRows = mySheet.getLastRowNum();
        int numOfColumns = mySheet.getRow(0).getLastCellNum();

        String[][] forTheProvider = new String[numOfRows][numOfColumns];

        int rows = 0;
        int cells = 0;

        while(rowIterator.hasNext()){
            Row row = rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                if(rows!=0)
                forTheProvider[rows-1][cells] = cell.getStringCellValue();
                cells++;

            }
            cells = 0;
            rows++;
        }

        return forTheProvider;

    }
}
