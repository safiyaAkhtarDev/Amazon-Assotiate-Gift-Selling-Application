package com.safiyaakhtar.gifty.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.safiyaakhtar.gifty.R;
import com.safiyaakhtar.gifty.connection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SubmitAProduct extends AppCompatActivity {
    EditText edt_productname, edt_productpice, edt_websire, edt_image;
    TextInputLayout tip_productname, tip_productprice, tip_website, tip_image;
    Button btn_submit;
    TextView txt_img,txt_web;
    String productname, price, website, image, user;
    Intent i;
    ImageView img_back;
    ConnectionClass connectionClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_aproduct);
        tip_image = findViewById(R.id.tip_Imageurl);
        txt_img = findViewById(R.id.imageurl);
        txt_web = findViewById(R.id.weburl);
        img_back = findViewById(R.id.imgback);
        tip_productname = findViewById(R.id.tip_productname);
        tip_website = findViewById(R.id.tip_website);
        tip_productprice = findViewById(R.id.tip_price);
        edt_image = findViewById(R.id.edt_imageurl);
        edt_productname = findViewById(R.id.edt_productname);
        edt_websire = findViewById(R.id.edt_website);
        edt_productpice = findViewById(R.id.edt_price);
        btn_submit = findViewById(R.id.btn_submit);
        connectionClass = new ConnectionClass();
        user = getIntent().getStringExtra("userid");

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txt_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.archiesonline.com/shop-online/accessories/keychains/diamond-studded-heart-keychain/51443"));
                startActivity(browserIntent);
            }
        });
 txt_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://life-news.blog/wp-content/uploads/2018/12/51W7DMh1lL.jpg"));
                startActivity(browserIntent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productname = edt_productname.getText().toString().trim();
                price = edt_productpice.getText().toString().trim();
                image = edt_image.getText().toString().trim();
                website = edt_websire.getText().toString().trim();
                if (productname.isEmpty()) {
                    tip_productname.setError("Please Ente Product Name");
                } else if (price.isEmpty()) {
                    tip_productname.setError(null);
                    tip_productprice.setError("Please Enter Price In Rupees");
                } else if (website.isEmpty()) {
                    tip_productname.setError(null);
                    tip_productprice.setError(null);
                    tip_productprice.setError("Enter Correct Website URL");
                } else if (image.isEmpty()) {
                    tip_productname.setError(null);
                    tip_website.setError(null);
                    tip_productprice.setError(null);
                    tip_image.setError("Enter Image URL");
                }else{
                    AddPro addPro=new AddPro();
                    addPro.execute();
                }
            }
        });


    }

    public class AddPro extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SubmitAProduct.this);
            progressDialog.setMessage("Sending Your Request");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.hide();
            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(SubmitAProduct.this);
            alertDialog.setMessage("Your Request Has Been Send. We Will Contact You In Next 24hr Via Mail \n Till Then Check Availabale Gifts. Have A Good Day! ");
            alertDialog.setTitle("Request Sent");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });
            alertDialog.show();
            Toast.makeText(SubmitAProduct.this, r, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String dates = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    String query = "INSERT INTO SubmitedProducts\n" +
                            "           (Username\n" +
                            "           ,RequestDate\n" +
                            "           ,Productname\n" +
                            "           ,WebsiteURL\n" +
                            "           ,ImageURL\n" +
                            "           ,Price\n" +
                            "           ,Active)\n" +
                            "     VALUES\n" +
                            "           ('"+user+"'\n" +
                            "           ,'"+dates+"'\n" +
                            "           ,'"+productname+"'\n" +
                            "           ,'"+website+"'\n" +
                            "           ,'"+image+"'\n" +
                            "           ,'"+image+"'\n" +
                            "           ,0)";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    z = "Added Successfully";
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }
            return z;
        }
    }
}
