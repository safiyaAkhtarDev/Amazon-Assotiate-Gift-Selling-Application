package com.safiyaakhtar.gifty.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.safiyaakhtar.gifty.CustomDesign.WishDeleteClass;
import com.safiyaakhtar.gifty.POJO.WishPojo;
import com.safiyaakhtar.gifty.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SafiyaAkhtar on 12/18/2018.
 */

public class WishListGridViewAdapter extends ArrayAdapter {
    int userid;
    int itemtypecode;

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<WishPojo> mWishData = new ArrayList<WishPojo>();

    public WishListGridViewAdapter(Context mContext, int layoutResourceId,
                                   ArrayList<WishPojo> mWishData, int userid) {
        super(mContext, layoutResourceId, mWishData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mWishData = mWishData;
        this.userid = userid;
    }

    public void setGridData(ArrayList<WishPojo> mWishData) {
        this.mWishData = mWishData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.txt_itemname = (TextView) row.findViewById(R.id.txt_item);
            holder.txt_wishcount = (TextView) row.findViewById(R.id.wish_count);
            holder.txt_itemprice = (TextView) row.findViewById(R.id.txt_price);
            holder.item_imageView = (ImageView) row.findViewById(R.id.item_image);
            holder.share_imageView = (ImageView) row.findViewById(R.id.img_share);
            holder.btn_itemurl = (Button) row.findViewById(R.id.btn_checkitout);
            holder.linearLayout = (LinearLayout) row.findViewById(R.id.android_custom_gridview_layout);
            holder.wishbutton = (LikeButton) row.findViewById(R.id.wish_button);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final WishPojo wish = mWishData.get(position);
        holder.txt_itemname.setText(Html.fromHtml(wish.getItem_name()));
        holder.txt_itemprice.setText("\u20B9" + Html.fromHtml(wish.getItem_price()));
        holder.txt_itemprice.setTag(Html.fromHtml(wish.getItem_URL()));
        holder.txt_wishcount.setText(String.valueOf(wish.getWishCount()));
        Picasso.with(mContext).load(wish.getItem_image()).into(holder.item_imageView);
        itemtypecode = wish.getItemtypecode();
        holder.wishbutton.setLiked(true);
//        if (wish.isLiked()) {
//            holder.wishbutton.setLiked(true);
//        }
        final int count = Integer.parseInt(String.valueOf(holder.txt_wishcount.getText())) + 1;
        holder.wishbutton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                holder.wishbutton.setLiked(false);
                WishDeleteClass adapter = new WishDeleteClass(userid, wish.getItemid(), wish.getItemtypecode(), mContext);
                adapter.execute();
                mWishData.remove(position);
                notifyDataSetChanged();
                //row.setVisibility(View.GONE);
            }
        });

        holder.btn_itemurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(holder.txt_itemprice.getTag())));
                mContext.startActivity(browserIntent);
            }
        });
        holder.item_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(holder.txt_itemprice.getTag())));
                mContext.startActivity(browserIntent);
            }
        });
        holder.share_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareItem(wish.getItem_image(), wish.getItem_name(), wish.getItem_URL());
            }
        });
        return row;
    }

    public void shareItem(String url, final String name, final String afURL) {
        Picasso.with(mContext).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_TEXT, name + "\n" + afURL + "\n\nGet More Amazing Gift Ideas On this App " + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                mContext.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(mContext.getExternalFilesDir
                    (Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    static class ViewHolder {
        TextView txt_itemname, txt_itemprice, txt_wishcount;
        ImageView item_imageView, share_imageView;
        Button btn_itemurl;
        LikeButton wishbutton;
        LinearLayout linearLayout;
    }

}
