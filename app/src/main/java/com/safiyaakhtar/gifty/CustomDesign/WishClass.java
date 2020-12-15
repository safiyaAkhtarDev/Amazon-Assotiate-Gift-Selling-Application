package com.safiyaakhtar.gifty.CustomDesign;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SafiyaAkhtar on 12/16/2018.
 */

public class WishClass extends AsyncTask {
    boolean isSuccess;
    boolean z;
    Context context;
    String back;
    int itemid,itemtypecode,userid;
    int wishcount;
    boolean alreadyexist;

    public boolean isAlreadyexist() {
        return alreadyexist;
    }

    public void setAlreadyexist(boolean alreadyexist) {
        this.alreadyexist = alreadyexist;
    }

    ConnectionClass connectionClass=new ConnectionClass();

    public WishClass(int userid,int itemid,int itemtypecode,int wishcount,Context context) {
        this.userid = userid;
        this.itemid = itemid;
        this.itemtypecode = itemtypecode;
        this.context = context;
        this.wishcount=wishcount;

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Connection con = connectionClass.CONN();
        wishcount=wishcount+1;
        try {
            if (con == null) {
                isSuccess = false;
            } else {
                    String querywish = "select * from WishTable where UserId="+userid+" " +
                            "and ItemId="+itemid+" and Item_type_Code="+itemtypecode+"";
                Statement stmt1 = con.createStatement();
                ResultSet rs1 = stmt1.executeQuery(querywish);
                if (!rs1.next()){
                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    String query = " INSERT INTO WishTable\n" +
                            "           (UserId\n" +
                            "           ,ItemId\n" +
                            "           ,Item_type_Code\n" +
                            "           ,createdAt)\n" +
                            "     VALUES("+userid+","+itemid+","+itemtypecode+","+dates+")";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    String query1 = " update gifty_items set Wishcount=Wishcount+1 where srno='"+itemid+"'";
                    PreparedStatement preparedStatements = con.prepareStatement(query1);
                    preparedStatements.executeUpdate();
                    isSuccess = true;
                }

            }
        } catch (Exception e) {
            isSuccess = false;
//            Log.e("safiya", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (isSuccess){
            setAlreadyexist(false);
            Toast.makeText(context, "Added To Wish List", Toast.LENGTH_SHORT).show();
        }
        else{
            setAlreadyexist(true);
            Toast.makeText(context, "Already In WishList", Toast.LENGTH_SHORT).show();
        }

    }


}
