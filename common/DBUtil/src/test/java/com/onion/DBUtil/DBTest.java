package com.onion.DBUtil;
import org.junit.Test;

/**param
 * DB_URL the database URL
 *        the URL format is like this: jdbc:mysql://hostname/databaseName
 * USER： the user name
 * PASS: password
 */

public class DBTest {
    /**You need to change the three parameters below by youself*/
    private String DB_URL = "jdbc:mysql://localhost/testdb";
    private String USER = "root";
    private String PASS = "laogu123";

    @Test
    public void test() {
        DBUtil db = new DBUtil(DB_URL, USER, PASS);

        db.createTable();

        long[] blockID = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        String filename = "f1";

        db.write(filename, blockID);
        long[] ob = new long[20];
        db.read(filename, ob);
        System.out.println(ob);
        db.delete(filename);
        db.droptable();
    }

}