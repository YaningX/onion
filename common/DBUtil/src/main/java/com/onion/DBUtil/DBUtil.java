package com.onion.DBUtil;

import java.sql.*;

public class DBUtil {
    // JDBC driver name and database URL
    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL = "jdbc:mysql://localhost/testdb";
    private String USER = "root";
    private String PASS = "laogu123";
    public static boolean isTableExist = true;

    public DBUtil() {}
    public DBUtil(String Url, String User, String Password) {

        this.DB_URL = Url;
        this.USER = User;
        this.PASS = Password;

    }

    public void createTable() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "create table tbl(filename VARCHAR(255), " +
                    "blkID1 BIGINT, blkID2 BIGINT, blkID3 BIGINT, blkID4 BIGINT, blkID5 BIGINT, " +
                    "blkID6 BIGINT, blkID7 BIGINT, blkID8 BIGINT, blkID9 BIGINT, blkID10 BIGINT, " +
                    "blkID11 BIGINT, blkID12 BIGINT, blkID13 BIGINT, blkID14 BIGINT, blkID15 BIGINT, " +
                    "blkID16 BIGINT, blkID17 BIGINT, blkID18 BIGINT, blkID19 BIGINT, blkID20 BIGINT, " +
                    "primary key(filename))";
            stmt.execute(sql);
            conn.close();
            isTableExist = true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void write(String filename, long[] blockID)  {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "INSERT into tbl(filename, " +
                    "blkID1, blkID2, blkID3, blkID4, blkID5, blkID6, blkID7, blkID8, blkID9, blkID10, " +
                    "blkID11, blkID12, blkID13, blkID14, blkID15, blkID16, blkID17, blkID18, blkID19, blkID20) " +
                    "values(?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,filename);
            int len = blockID.length;
            for(int i = 0; i < len; i++) {
                pstmt.setLong(i+2, blockID[i]);
            }
            pstmt.execute();
            pstmt.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(String filename, long[] blockID) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * from tbl where filename =\"" + filename + "\"";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int len = blockID.length;
            for(int i = 0; i < len; i++) {
                blockID[i] = rs.getLong(i+2);
            }
            stmt.close();
            rs.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(String filename) {
        try{
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * from tbl where filename =\"" + filename + "\"";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                rs.deleteRow();
            }
            stmt.close();
            rs.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void droptable() {
        try{
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "drop table tbl";
            if(isTableExist) {
                stmt.execute(sql);
            }
            stmt.close();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
