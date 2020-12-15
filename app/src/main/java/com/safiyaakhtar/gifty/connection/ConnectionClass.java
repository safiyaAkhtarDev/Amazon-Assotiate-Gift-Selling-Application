package com.safiyaakhtar.gifty.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by SafiyaAkhtar on 10/8/2017.
 */

public class ConnectionClass {

    String ip = "111.000.000.00";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "database_name";
    String un = "username";
    String password = "password";
    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL ;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
        } catch (ClassNotFoundException e) {

        } catch (Exception e) {

        }
        return conn;
    }
}


