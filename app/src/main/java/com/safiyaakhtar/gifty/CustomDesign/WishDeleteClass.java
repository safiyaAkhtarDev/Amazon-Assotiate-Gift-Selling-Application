package com.safiyaakhtar.gifty.CustomDesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by SafiyaAkhtar on 12/18/2018.
 */

public class WishDeleteClass extends AsyncTask {
    ProgressDialog progressDialog;
    ConnectionClass connectionClass=new ConnectionClass();
    boolean isSuccess;
    int userid,itemid,itemtypecode;
    Context context;

    public WishDeleteClass(int userid, int itemid, int itemtypecode, Context context) {
        this.userid=userid;
        this.itemid=itemid;
        this.itemtypecode=itemtypecode;
        this.context=context;
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
//            Log.e("safiya",ex.getMessage());
        }
        return null;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Deleting From WishList");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.hide();
        if (isSuccess){
//            Intent intent=new Intent(context, WishListActivity.class);
//            intent.putExtra("userid",userid);
//            context.startActivity(intent);
        }else {
            Toast.makeText(context, "Please Check Your Interent Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
