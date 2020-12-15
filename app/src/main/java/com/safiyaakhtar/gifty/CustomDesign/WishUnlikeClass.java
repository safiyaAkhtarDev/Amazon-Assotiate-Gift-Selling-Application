package com.safiyaakhtar.gifty.CustomDesign;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by SafiyaAkhtar on 12/17/2018.
 */

public class WishUnlikeClass extends AsyncTask{
    ConnectionClass connectionClass=new ConnectionClass();
    boolean isSuccess;
    boolean isDeleted;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    Context context;
    int userid;int itemid;int itemtypecode;

    public WishUnlikeClass(int userid, int itemid, int itemtypecode, Context context) {
        this.userid=userid;
        this.itemid=itemid;
        this.context=context;
        this.itemtypecode=itemtypecode;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
              isSuccess=false;
            } else {

                String query = "delete WishTable where UserId="+userid+" and ItemId="+itemid+" and item_type_code="+itemtypecode+"";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.executeUpdate();
                String query1 = "  update gifty_items set Wishcount=Wishcount-1 where srno="+itemid+"";
                PreparedStatement preparedStatements = con.prepareStatement(query1);
                preparedStatements.executeUpdate();
                isSuccess = true;
            }
        } catch (Exception ex) {
            isSuccess = false;
        }
        return null;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (isSuccess){
            setDeleted(true);
            Toast.makeText(context, "Deleted From Wishlist", Toast.LENGTH_SHORT).show();
        }else{
            setDeleted(false);
            Toast.makeText(context, "Try Again Later", Toast.LENGTH_SHORT).show();
        }
    }
}
