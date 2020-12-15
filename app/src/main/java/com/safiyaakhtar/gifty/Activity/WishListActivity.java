package com.safiyaakhtar.gifty.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.safiyaakhtar.gifty.Adapter.WishListGridViewAdapter;
import com.safiyaakhtar.gifty.POJO.WishPojo;
import com.safiyaakhtar.gifty.R;
import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import id.arieridwan.lib.PageLoader;

public class WishListActivity extends AppCompatActivity {
    GridView grid_wish;
    PageLoader pageLoader;
    WishListGridViewAdapter adapter;
    ConnectionClass connectionClass;
    ArrayList<WishPojo> mylist;
    int userid;
    ImageView imgback;
    Intent intent;
    WishPojo wishPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        intent = getIntent();
        userid = Integer.parseInt(intent.getStringExtra("userid"));
        grid_wish = (GridView) findViewById(R.id.grid_wish);
        pageLoader = (PageLoader) findViewById(R.id.pageloader);
        imgback = (ImageView) findViewById(R.id.imgback);
        connectionClass = new ConnectionClass();
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        WishData wishData = new WishData();
        wishData.execute();
    }

    public class WishData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            pageLoader.startProgress();
        }

        @Override
        protected void onPostExecute(String r) {
            pageLoader.stopProgress();
            if (isSuccess) {
                adapter = new WishListGridViewAdapter(WishListActivity.this, R.layout.item_grid_view, mylist,userid);
                adapter.setGridData(mylist);
                grid_wish.setAdapter(adapter);
            } else {
                Toast.makeText(WishListActivity.this, "No Items In Wish List", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Check Your Internet Connection";
                } else {
                    String query = "SELECT * FROM gifty_items INNER JOIN WishTable \n" +
                            "ON gifty_items.Srno = WishTable.ItemId where \n" +
                            "WishTable.userId=" + userid + "\n";
                    Statement stmt = con.createStatement();
                    ResultSet rs = null;
                    mylist = new ArrayList<WishPojo>();
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        wishPojo = new WishPojo();
                        isSuccess = true;
                        wishPojo.setItemid(rs.getInt("ItemId"));
                        wishPojo.setItemtypecode(Integer.parseInt(rs.getString("Item_type_Code")));
                        wishPojo.setCreatedAt(rs.getString("createdAt"));
                        wishPojo.setItem_image(rs.getString("item_image"));
                        wishPojo.setItem_name(rs.getString("item_name"));
                        wishPojo.setItem_price(rs.getString("item_price"));
                        wishPojo.setItem_URL(rs.getString("Item_URL"));
                        wishPojo.setWishCount(rs.getInt("WishCount"));
                        mylist.add(wishPojo);

                    }
                }
            } catch (SQLException ex) {
                isSuccess = false;
//                Log.e("safiya",ex.getMessage());
                z = "Check Your Internet Connection";
            }
            return z;
        }
    }
}
