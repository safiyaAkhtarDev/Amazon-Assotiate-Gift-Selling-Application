package com.safiyaakhtar.gifty.Activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.safiyaakhtar.gifty.Adapter.CustomGridViewItemActivity;
import com.safiyaakhtar.gifty.POJO.ItemPOJO;
import com.safiyaakhtar.gifty.POJO.SharedPrefManager;
import com.safiyaakhtar.gifty.R;
import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import id.arieridwan.lib.PageLoader;

public class ItemsActivity extends AppCompatActivity {
    ItemPOJO itemPOJO;
    GridView gridView;
    ArrayList<ItemPOJO> mylist;
    CustomGridViewItemActivity adapter;
    ConnectionClass connectionClass;
    int code;
    Intent intent;
    ImageView img_back;
        CardView img_filter;
    PageLoader pageLoader;
    //    EditText edtMin, edtMax;
//    Button btn_search;
    String max, min;
    TextView txtname;
    SharedPrefManager sharedPrefManager;
    int userid;
    ImageView img_wishlist;
    EditText edt_search;
    String message;
    private int checkedItem;
    private String[] androidVersions;


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,Home_Activity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPrefManager = new SharedPrefManager(this);
        intent = getIntent();
        code =  intent.getIntExtra("id", 0);
        Log.e("message:", String.valueOf(code));
        //connection class object
        connectionClass = new ConnectionClass();

        userid = Integer.parseInt(sharedPrefManager.getUserId());

        gridView = (GridView) findViewById(R.id.grid_item);
        pageLoader = (PageLoader) findViewById(R.id.pageloader);
        img_back = (ImageView) findViewById(R.id.imgback);
        img_wishlist = (ImageView) findViewById(R.id.img_wishlist);
        img_filter = (CardView) findViewById(R.id.img_filter);
        txtname = (TextView) findViewById(R.id.txtname);
        edt_search = (EditText) findViewById(R.id.edt_search);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
               finish();
            }
        });

        // calling async to fill data
        ItemData itemData = new ItemData();
        itemData.execute();


        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.filter, null);
                RadioButton rel, pop, price;
                LinearLayout linlay;
                EditText edtmin, edtmax;
                RadioGroup rg=dialogLayout.findViewById(R.id.rg);
                rel = dialogLayout.findViewById(R.id.rd_most);
                pop = dialogLayout.findViewById(R.id.rd_popular);
                price = dialogLayout.findViewById(R.id.rd_price);
                edtmax = dialogLayout.findViewById(R.id.edt_max);
                edtmin = dialogLayout.findViewById(R.id.edt_min);
                linlay = dialogLayout.findViewById(R.id.pricelayout);
                final AlertDialog dialog = new AlertDialog.Builder(ItemsActivity.this)
                        .setView(dialogLayout)
                        .setTitle("Filter Options")
                        .setPositiveButton("Done", null) //Set to null. We override the onclick
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                if (rel.isChecked()) {
                                    dialog.dismiss();
                                    ItemData itemData = new ItemData();
                                    itemData.execute();
                                } else if (pop.isChecked()) {
                                    dialog.dismiss();
                                    PopularData popularData=new PopularData();
                                    popularData.execute();

                                } else if (price.isChecked()) {

                                    max = edtmax.getText().toString().trim();
                                    min = edtmin.getText().toString().trim();
                                    if (min.isEmpty()) {
                                        min = "100";
                                    }
                                    if (max.isEmpty()) {
                                        edtmax.setError("Enter max Price");
                                    } else if (Integer.parseInt(max) < 100) {
                                        edtmax.setError("Enter Value Greater than 100");
                                    } else if (Integer.parseInt(min) < 100) {
                                        edtmin.setError("Enter Value Greater than 100");
                                    } else if (Integer.parseInt(max) >= Integer.parseInt(min)) {
                                        edtmax.setError(null);
                                        dialog.dismiss();
                                        PriceSort priceSort = new PriceSort();
                                        priceSort.execute();
                                    } else {
                                        edtmin.setError(null);
                                        edtmax.setError("Max Must be greater than Min");
                                    }
                                }

                            }
                        });
                    }
                });
                dialog.show();






            }

        });
        img_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemsActivity.this, WishListActivity.class);
                intent.putExtra("userid", sharedPrefManager.getUserId());
                startActivity(intent);
            }
        });
        edt_search.setEnabled(false);
        if (code == 0) {
            txtname.setText("Gift For Men");
            edt_search.setHint("Search In Gift For Men");
        } else if (code == 1) {
            txtname.setText("Gift For Women");
            edt_search.setHint("Search In Gift For Women");
        } else if (code == 2) {
            txtname.setText("Gift For Kids");
            edt_search.setHint("Search In Gift For Kids");
        } else if (code == 3) {
            txtname.setText("Geeky Stuff");
            edt_search.setHint("Search In Geeky Stuff");
        } else if (code == 4) {
            txtname.setText("Gift For Couples");
            edt_search.setHint("Search In Gift For Couples");
        } else if (code == 5) {
            txtname.setText("Food & Drinks");
            edt_search.setHint("Search In Food & Drinks");
        } else if (code == 6) {
            txtname.setText("Home & Office");
            edt_search.setHint("Search In Home & Office");
        } else if (code == 7) {
            txtname.setText("Toys & Games");
            edt_search.setHint("Search In Toys & Games");
        } else if (code == 8) {
            txtname.setText("Travel");
            edt_search.setHint("Search In Travel");
        } else if (code == 9) {
            txtname.setText("Wearables");
            edt_search.setHint("Search In Wearables");
        } else if (code == 10) {
            txtname.setText("Random");
            edt_search.setHint("Search In Random");
        } else if (code == 11) {
            txtname.setText("Music Intruments");
            edt_search.setHint("Search In Music Instrument");
        } else if (code == 12) {
            txtname.setText("DIY & Customize");
            edt_search.setHint("Search In DIY & Customize");
        } else if (code == 13) {
            txtname.setText("Gift For Festivals");
            edt_search.setHint("Search In Gift For Festivals");
        } else if (code == 14) {
            txtname.setText("Funny Gifts");
            edt_search.setHint("Search In Funny Gifts");
        } else if (code == 15) {
            txtname.setText("More...");
            edt_search.setHint("Search In More...");
        } else if (code == 100) {
            txtname.setText("Trending Gifts");
            edt_search.setHint("Search In Trending Gifts");
        }


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s);
            }
        });

    if (!edt_search.getText().toString().trim().isEmpty()){
        adapter.getFilter().filter(edt_search.getText().toString().trim());
    }
    }

    public class PriceSort extends AsyncTask {
        String z;
        boolean isSuccess;

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Check Your Internet Connection";
                } else {
                    String querymen = "select * from gifty_items where Item_type_code=1 and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querywomen = "select * from gifty_items where Item_type_code=2 and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querykids = "select * from gifty_items where Item_type_code=3 and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querygeeky = "select * from gifty_items where Item_type_code=4  and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querycouples = "select * from gifty_items where Item_type_code=5   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryfood = "select * from gifty_items where Item_type_code=6   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryhome = "select * from gifty_items where Item_type_code=7   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querytoys = "select * from gifty_items where Item_type_code=8   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querytravel = "select * from gifty_items where Item_type_code=9   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querywearables = "select * from gifty_items where Item_type_code=10   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryrandom = "select * from gifty_items where Item_type_code=11   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querymusic = "select * from gifty_items where Item_type_code=12   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryDIY = "select * from gifty_items where Item_type_code=13   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryfestival = "select * from gifty_items where Item_type_code=14   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String queryfunny = "select * from gifty_items where Item_type_code=15   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querymore = "select * from gifty_items where Item_type_code=16   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    String querytrending = "select * from gifty_items where Item_type_code=100   and item_price between " + min + " AND " + max + "  order by CreatedAt DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = null;
                    mylist = new ArrayList<>();

                    if (code == 0) {
                        rs = stmt.executeQuery(querymen);
                    } else if (code == 1) {
                        rs = stmt.executeQuery(querywomen);
                    } else if (code == 2) {
                        rs = stmt.executeQuery(querykids);
                    } else if (code == 3) {
                        rs = stmt.executeQuery(querygeeky);
                    } else if (code == 4) {
                        rs = stmt.executeQuery(querycouples);
                    } else if (code == 5) {
                        rs = stmt.executeQuery(queryfood);
                    } else if (code == 6) {
                        rs = stmt.executeQuery(queryhome);
                    } else if (code == 7) {
                        rs = stmt.executeQuery(querytoys);
                    } else if (code == 8) {
                        rs = stmt.executeQuery(querytravel);
                    } else if (code == 9) {
                        rs = stmt.executeQuery(querywearables);
                    } else if (code == 10) {
                        rs = stmt.executeQuery(queryrandom);
                    } else if (code == 11) {
                        rs = stmt.executeQuery(querymusic);
                    } else if (code == 12) {
                        rs = stmt.executeQuery(queryDIY);
                    } else if (code == 13) {
                        rs = stmt.executeQuery(queryfestival);
                    } else if (code == 14) {
                        rs = stmt.executeQuery(queryfunny);
                    } else if (code == 15) {
                        rs = stmt.executeQuery(querymore);
                    } else if (code == 100) {
                        rs = stmt.executeQuery(querytrending);
                    }
                    while (rs.next()) {
                        isSuccess = true;
                        itemPOJO = new ItemPOJO();
                        itemPOJO.setItemname(rs.getString("item_name"));
                        itemPOJO.setItemURl(rs.getString("Item_URL"));
                        itemPOJO.setItemPrice(rs.getString("item_price"));
                        itemPOJO.setItemImage(rs.getString("item_image"));
                        itemPOJO.setWishCount(rs.getInt("WishCount"));
                        itemPOJO.setItemId(rs.getInt("srno"));
                        itemPOJO.setItemtypecode(rs.getString("Item_type_code"));
                        mylist.add(itemPOJO);
                    }
                }
            } catch (SQLException ex) {
                isSuccess = false;
                z = "Check Your Internet Connection";
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pageLoader.startProgress();
            gridView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pageLoader.stopProgress();
            if (isSuccess) {
                gridView.setVisibility(View.VISIBLE);
                adapter = new CustomGridViewItemActivity(ItemsActivity.this, R.layout.item_grid_view, mylist, userid);
                adapter.setGridData(mylist);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(ItemsActivity.this, "No Gifts or Check Your Interenet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ItemData extends AsyncTask<String, String, String> {
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
                edt_search.setEnabled(true);
                adapter = new CustomGridViewItemActivity(ItemsActivity.this, R.layout.item_grid_view, mylist, userid);
                adapter.setGridData(mylist);
                gridView.setAdapter(adapter);
                if (intent.hasExtra("message")){
                    message = intent.getStringExtra("message");
                    edt_search.setText(message);
                    adapter.getFilter().filter(message);
                }
            } else {
                Toast.makeText(ItemsActivity.this, "No Gifts or Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Check Your Internet Connection";
                } else {
                    String querymen = "select * from gifty_items where Item_type_code=1 order by CreatedAt DESC";
                    String querywomen = "select * from gifty_items where Item_type_code=2  order by CreatedAt DESC";
                    String querykids = "select * from gifty_items where Item_type_code=3  order by CreatedAt DESC";
                    String querygeeky = "select * from gifty_items where Item_type_code=4  order by CreatedAt DESC";
                    String querycouples = "select * from gifty_items where Item_type_code=5  order by CreatedAt DESC";
                    String queryfood = "select * from gifty_items where Item_type_code=6  order by CreatedAt DESC";
                    String queryhome = "select * from gifty_items where Item_type_code=7  order by CreatedAt DESC";
                    String querytoys = "select * from gifty_items where Item_type_code=8  order by CreatedAt DESC";
                    String querytravel = "select * from gifty_items where Item_type_code=9  order by CreatedAt DESC";
                    String querywearables = "select * from gifty_items where Item_type_code=10  order by CreatedAt DESC";
                    String queryrandom = "select * from gifty_items where Item_type_code=11  order by CreatedAt DESC";
                    String querymusic = "select * from gifty_items where Item_type_code=12  order by CreatedAt DESC";
                    String queryDIY = "select * from gifty_items where Item_type_code=13  order by CreatedAt DESC";
                    String queryfestival = "select * from gifty_items where Item_type_code=14  order by CreatedAt DESC";
                    String queryfunny = "select * from gifty_items where Item_type_code=15  order by CreatedAt DESC";
                    String querymore = "select * from gifty_items where Item_type_code=16  order by CreatedAt DESC";
                    String querytrending = "select * from gifty_items where Item_type_code=100  order by CreatedAt DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = null;
                    mylist = new ArrayList<>();

                    if (code == 0) {
                        rs = stmt.executeQuery(querymen);
                    } else if (code == 1) {
                        rs = stmt.executeQuery(querywomen);
                    } else if (code == 2) {
                        rs = stmt.executeQuery(querykids);
                    } else if (code == 3) {
                        rs = stmt.executeQuery(querygeeky);
                    } else if (code == 4) {
                        rs = stmt.executeQuery(querycouples);
                    } else if (code == 5) {
                        rs = stmt.executeQuery(queryfood);
                    } else if (code == 6) {
                        rs = stmt.executeQuery(queryhome);
                    } else if (code == 7) {
                        rs = stmt.executeQuery(querytoys);
                    } else if (code == 8) {
                        rs = stmt.executeQuery(querytravel);
                    } else if (code == 9) {
                        rs = stmt.executeQuery(querywearables);
                    } else if (code == 10) {
                        rs = stmt.executeQuery(queryrandom);
                    } else if (code == 11) {
                        rs = stmt.executeQuery(querymusic);
                    } else if (code == 12) {
                        rs = stmt.executeQuery(queryDIY);
                    } else if (code == 13) {
                        rs = stmt.executeQuery(queryfestival);
                    } else if (code == 14) {
                        rs = stmt.executeQuery(queryfunny);
                    } else if (code == 15) {
                        rs = stmt.executeQuery(querymore);
                    } else if (code == 100) {
                        rs = stmt.executeQuery(querytrending);
                    }
                    while (rs.next()) {
                        isSuccess = true;
                        itemPOJO = new ItemPOJO();
                        itemPOJO.setItemname(rs.getString("item_name"));
                        itemPOJO.setItemURl(rs.getString("Item_URL"));
                        itemPOJO.setItemId(rs.getInt("srno"));
                        itemPOJO.setWishCount(rs.getInt("WishCount"));
                        itemPOJO.setItemPrice(rs.getString("item_price"));
                        itemPOJO.setItemImage(rs.getString("item_image"));
                        itemPOJO.setLiked(false);
                        itemPOJO.setItemtypecode(rs.getString("Item_type_code"));
                        mylist.add(itemPOJO);
                    }
                }
            } catch (SQLException ex) {
                isSuccess = false;
                z = "Check Your Internet Connection";
            }
            return z;
        }
    }


    public class PopularData extends AsyncTask<String, String, String> {
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
                edt_search.setEnabled(true);
                adapter = new CustomGridViewItemActivity(ItemsActivity.this, R.layout.item_grid_view, mylist, userid);
                adapter.setGridData(mylist);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(ItemsActivity.this, "No Gifts or Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Check Your Internet Connection";
                } else {
                    String querymen = "select * from gifty_items where Item_type_code=1 order by WishCount DESC";
                    String querywomen = "select * from gifty_items where Item_type_code=2  order by WishCount DESC";
                    String querykids = "select * from gifty_items where Item_type_code=3  order by WishCount DESC";
                    String querygeeky = "select * from gifty_items where Item_type_code=4  order by WishCount DESC";
                    String querycouples = "select * from gifty_items where Item_type_code=5  order by WishCount DESC";
                    String queryfood = "select * from gifty_items where Item_type_code=6  order by WishCount DESC";
                    String queryhome = "select * from gifty_items where Item_type_code=7  order by WishCount DESC";
                    String querytoys = "select * from gifty_items where Item_type_code=8  order by WishCount DESC";
                    String querytravel = "select * from gifty_items where Item_type_code=9  order by WishCount DESC";
                    String querywearables = "select * from gifty_items where Item_type_code=10  order by WishCount DESC";
                    String queryrandom = "select * from gifty_items where Item_type_code=11  order by WishCount DESC";
                    String querymusic = "select * from gifty_items where Item_type_code=12  order by WishCount DESC";
                    String queryDIY = "select * from gifty_items where Item_type_code=13  order by WishCount DESC";
                    String queryfestival = "select * from gifty_items where Item_type_code=14  order by WishCount DESC";
                    String queryfunny = "select * from gifty_items where Item_type_code=15  order by WishCount DESC";
                    String querymore = "select * from gifty_items where Item_type_code=16  order by WishCount DESC";
                    String querytrending = "select * from gifty_items where Item_type_code=100  order by WishCount DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = null;
                    mylist = new ArrayList<>();

                    if (code == 0) {
                        rs = stmt.executeQuery(querymen);
                    } else if (code == 1) {
                        rs = stmt.executeQuery(querywomen);
                    } else if (code == 2) {
                        rs = stmt.executeQuery(querykids);
                    } else if (code == 3) {
                        rs = stmt.executeQuery(querygeeky);
                    } else if (code == 4) {
                        rs = stmt.executeQuery(querycouples);
                    } else if (code == 5) {
                        rs = stmt.executeQuery(queryfood);
                    } else if (code == 6) {
                        rs = stmt.executeQuery(queryhome);
                    } else if (code == 7) {
                        rs = stmt.executeQuery(querytoys);
                    } else if (code == 8) {
                        rs = stmt.executeQuery(querytravel);
                    } else if (code == 9) {
                        rs = stmt.executeQuery(querywearables);
                    } else if (code == 10) {
                        rs = stmt.executeQuery(queryrandom);
                    } else if (code == 11) {
                        rs = stmt.executeQuery(querymusic);
                    } else if (code == 12) {
                        rs = stmt.executeQuery(queryDIY);
                    } else if (code == 13) {
                        rs = stmt.executeQuery(queryfestival);
                    } else if (code == 14) {
                        rs = stmt.executeQuery(queryfunny);
                    } else if (code == 15) {
                        rs = stmt.executeQuery(querymore);
                    } else if (code == 100) {
                        rs = stmt.executeQuery(querytrending);
                    }
                    while (rs.next()) {
                        isSuccess = true;
                        itemPOJO = new ItemPOJO();
                        itemPOJO.setItemname(rs.getString("item_name"));
                        itemPOJO.setItemURl(rs.getString("Item_URL"));
                        itemPOJO.setItemId(rs.getInt("srno"));
                        itemPOJO.setWishCount(rs.getInt("WishCount"));
                        itemPOJO.setItemPrice(rs.getString("item_price"));
                        itemPOJO.setItemImage(rs.getString("item_image"));
                        itemPOJO.setLiked(false);
                        itemPOJO.setItemtypecode(rs.getString("Item_type_code"));
                        mylist.add(itemPOJO);
                    }
                }
            } catch (SQLException ex) {
                isSuccess = false;
                z = "Check Your Internet Connection";
            }
            return z;
        }
    }
}
