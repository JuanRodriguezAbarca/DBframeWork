package test.java;

import main.java.DBconnection;
import main.java.utils.ExcelReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Juan_Rodriguez on 12/10/2015.
 */
public class DBfirstAttempt {

    DBconnection dBconnection = new DBconnection();
    ExcelReader excelReader = new ExcelReader();


    @Test(groups = "Display the Table")
    public void displayingTheTable() throws SQLException, ClassNotFoundException {

        dBconnection.viewTable("MY_GAMES");

    }

    @Test(groups = "Print the excel file")
    public void printTheExcelFile() throws IOException {
        boolean titleFlag = false;
        for (String[] oneLine : excelReader.readingExcelFile(Integer.valueOf(System.getProperty("sheetIndex")))) {
            if (!titleFlag) {
                System.out.printf("%-28s%-15s%-12s%-20s%s%n", "NAME", "WHEREISGAME", "ORIGINAL", "AUTHOR", "TYPE");
                titleFlag = true;
            }
            System.out.printf("%-30s%-17s%-9s%-20s%s%n", oneLine[0], oneLine[1], oneLine[2], oneLine[3], oneLine[4]);

        }

    }

    @Test(groups = "Quering by Editorial")
    @Parameters("Editorial")
    public void queryingByEditorial(String editorial) throws SQLException, ClassNotFoundException {
        dBconnection.displayGamesOfEditor(editorial);
    }


    @Test(dataProvider = "retrivingXLSXdata", groups = "Adding Games")
    public void addingGamesToTheDBFromXLSX(String name, String whereisgame, String original, String author, String type) throws SQLException, ClassNotFoundException {
        dBconnection.insertNewGameInTable(name, whereisgame, original, author, type);
    }


    @Test(groups = "Delete Game By Name")
    @Parameters("gameName")
    public void deletingGameFromTableByName(String name) throws SQLException, ClassNotFoundException {
        dBconnection.deleteGameBasedOnName(name);
    }


    @Test(dataProvider = "retrivingXLSXdata", groups = "Adding Expansions to the base game")
    public void addingExpansionsToTheDBFromXLSX(String gameName, String expansionName) throws SQLException, ClassNotFoundException {
        dBconnection.insertNewExpansionInTable(gameName, expansionName);
    }

    @Test(groups = "Delete Expansion")
    @Parameters({"expansionName", "gameName"})
    public void deletingExpansion(String expansionName, String gameName) throws SQLException, ClassNotFoundException {
        dBconnection.deleteExpansion(expansionName, gameName);
    }

    @Test(groups = "Delete all Game Expansions")
    @Parameters("gameName")
    public void deletingAllExpansions(String gameName) throws SQLException, ClassNotFoundException {
        dBconnection.deleteAllExpansions(gameName);
    }



    @DataProvider(name = "retrivingXLSXdata")
    private String[][] retrivingXLSXdata() throws IOException {
        return excelReader.readingExcelFile(Integer.valueOf(System.getProperty("sheetIndex")));
    }


}
