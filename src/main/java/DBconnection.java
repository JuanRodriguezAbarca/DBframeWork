package main.java;



import java.sql.*;
import java.util.Properties;

/**
 * Created by Juan_Rodriguez on 12/10/2015.
 */
public class DBconnection {

    String userName = "Juan";
    String password = "password";


    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection conn = null;
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", this.userName);
        connectionProperties.put("password", this.password);

        conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", connectionProperties);

//        conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",userName,password);

        System.out.println("Connected to the DB!");

        return conn;
    }


    public void viewTable(String dbName) throws SQLException, ClassNotFoundException {
        PreparedStatement searchQuery = null;
        Connection con = getConnection();

        switch (dbName) {
            case "MY_GAMES":
                searchQuery = con.prepareStatement("select * from MY_GAMES ORDER BY ID ASC");
                break;
            case "EXPANSIONS":
                searchQuery = con.prepareStatement("select * from EXPANSIONS");
                break;

        }


//        System.out.println("ID\tNAME\tTYPE\tEXPANSIONS\tWHEREISGAME\tAUTHOR\tORIGINAL");
        System.out.printf("%-5s%-30s%-7s%-15s%-14s%-10s%s%n", "ID", "NAME", "TYPE", "EXPANSIONS", "WHEREISGAME", "AUTHOR", "ORIGINAL");



        try {
            ResultSet rs = searchQuery != null ? searchQuery.executeQuery() : null;


            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String gameName = rs.getString("NAME");
                    int gameType = rs.getInt("TYPE");
                    String expansions = rs.getString("EXPANSIONS");
                    int whereIsGame = rs.getInt("WHEREISGAME");
                    int author = rs.getInt("AUTHOR");
                    String isOriginal = rs.getString("ORIGINAL");

//                    System.out.println(id + "\t" + gameName + "\t\t" + gameType + "\t" + expansions + "\t" + whereIsGame + "\t" +author + "\t" + isOriginal);

                    System.out.printf("%-5s%-30s%-10s%-15s%-13s%-10s%s%n",id,gameName,gameType,expansions,whereIsGame,author,isOriginal);

                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }


    }

    public void insertNewGameInTable(String name, String whereIsGame, String isOriginal, String author, String type)
            throws SQLException, ClassNotFoundException {

        Connection con = getConnection();
        PreparedStatement insertRow = con.prepareStatement("insert into MY_GAMES\n" +
                "(NAME, EXPANSIONS, WHEREISGAME, ORIGINAL, AUTHOR, TYPE)\n" +
                "VALUES(?,?," +
                "(select place_id from WHEREISTHEGAME where who_has = ?)," +
                "?," +
                "(select author_id from game_author where author_name = ?)," +
                "(select type_id from type where type_name = ?))");
        insertRow.setString(1,name);
        insertRow.setString(2,"N");
        insertRow.setString(3, whereIsGame);
        insertRow.setString(4, isOriginal);
        insertRow.setString(5, author);
        insertRow.setString(6, type);

        insertRow.executeQuery();
        con.commit();

        con.close();

        System.out.println("New Row added for game: " + name);

    }

    public void insertNewExpansionInTable(String gameName, String expansionName) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement insertRow = con.prepareStatement("insert into EXPANSIONS" +
                "(GAME_ID, EXPANSION_NAME) VALUES" +
                "((select id from MY_GAMES where NAME = ?), ?)");
        insertRow.setString(1, gameName);
        insertRow.setString(2, expansionName);

        insertRow.executeQuery();

        PreparedStatement updateGameStatus = con.prepareStatement("UPDATE MY_GAMES SET EXPANSIONS = 'Y' WHERE id = ((select id from MY_GAMES where NAME = ?))");
        updateGameStatus.setString(1,gameName);
        updateGameStatus.executeQuery();

        con.commit();
        con.close();

        System.out.println("Expansion "+expansionName+" has been added on "+gameName);

    }


    public void deleteGameBasedOnName(String name) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        CallableStatement deleteGame = con.prepareCall("begin deletegames(?);end;");
        deleteGame.registerOutParameter(1,Types.VARCHAR);
        deleteGame.setString(1, name);
        deleteGame.execute();
        con.commit();
        con.close();
        System.out.println("Game '" + name + "' and all it's records have been deleted.");

    }

    public void deleteExpansion(String expansionName, String gameName) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        CallableStatement deleteExpansion = con.prepareCall("begin delete_expansion(?,?);end;");
        deleteExpansion.setString(1, expansionName);
        deleteExpansion.setString(2,gameName);
        deleteExpansion.execute();
        con.commit();
        con.close();
        System.out.println("Expansion '"+expansionName+"' of game '"+gameName+"' has been deleted.");
    }

    public void deleteAllExpansions(String gameName) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        CallableStatement deleteExpansion = con.prepareCall("begin delete_all_expansions(?);end;");
        deleteExpansion.setString(1,gameName);
        deleteExpansion.execute();
        con.commit();
        con.close();
        System.out.println("All expansions for the game '"+gameName+"' have been deleted.");
    }

    public void displayGamesOfEditor(String editor) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        System.out.println("Retriving games from editorial "+editor);
        PreparedStatement displayByEditor = con.prepareStatement("SELECT MY_GAMES.name, TYPE.Type_NAME from MY_GAMES RIGHT JOIN TYPE on TYPE.TYPE_ID=MY_GAMES.TYPE WHERE MY_GAMES.AUTHOR = " +
                "(select Author_ID from GAME_AUTHOR where AUTHOR_NAME = ?)");
        displayByEditor.setString(1, editor);
        ResultSet rs = displayByEditor.executeQuery();
        System.out.printf("%-25s%s%n","GAME","TYPE");
        while(rs.next()){
            System.out.printf("%-25s%s",rs.getString("NAME"),rs.getString("TYPE_NAME"));
            System.out.println();
        }

        con.close();

    }

}
