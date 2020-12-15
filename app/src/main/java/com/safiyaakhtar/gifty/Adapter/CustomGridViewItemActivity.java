package com.safiyaakhtar.gifty.Adapter;

/**
 * Created by SafiyaAkhtar on 11/28/2018.
 */

import android.app.Activity;
import android.content.ClipData;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.safiyaakhtar.gifty.CustomDesign.WishClass;
import com.safiyaakhtar.gifty.CustomDesign.WishUnlikeClass;
import com.safiyaakhtar.gifty.POJO.ItemPOJO;
import com.safiyaakhtar.gifty.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CustomGridViewItemActivity extends BaseAdapter {
    int userid;
    int itemtypecode;
    CustomFilter filter;
    WishClass wishClass;
    ItemPOJO item;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<ItemPOJO> itemdatalist = new ArrayList<ItemPOJO>();
    private ArrayList<ItemPOJO> filterlist = new ArrayList<ItemPOJO>();

    public CustomGridViewItemActivity(Context mContext, int layoutResourceId,
                                      ArrayList<ItemPOJO> itemdatalist, int userid) {
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.userid = userid;
        this.itemdatalist = itemdatalist;
        this.filterlist = itemdatalist;
    }

    public void setGridData(ArrayList<ItemPOJO> itemdatalist) {
        this.itemdatalist = itemdatalist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemdatalist.size();
    }

    @Override
    public ItemPOJO getItem(int position) {
        return itemdatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            holder.wishbutton = (LikeButton) row.findViewById(R.id.wish_button);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        item = itemdatalist.get(position);
        holder.txt_itemname.setText(Html.fromHtml(item.getItemname()));
        holder.txt_itemprice.setText("\u20B9" + Html.fromHtml(item.getItemPrice()));
        holder.txt_itemprice.setTag(Html.fromHtml(item.getItemURl()));
        holder.txt_wishcount.setText(String.valueOf(item.getWishCount()));
        Picasso.with(mContext).load(item.getItemImage()).into(holder.item_imageView);
        itemtypecode = Integer.parseInt(item.getItemtypecode());
        holder.wishbutton.setLiked(false);
        final int count = Integer.parseInt(String.valueOf(holder.txt_wishcount.getText())) + 1;

        if (item.isLiked()) {
            holder.wishbutton.setLiked(true);

        } else {
            holder.wishbutton.setLiked(false);
        }

        holder.wishbutton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                holder.wishbutton.isLiked();
                wishClass = new WishClass(userid, item.getItemId(), itemtypecode, item.getWishCount(), mContext);
                wishClass.execute();
                item.setLiked(true);
                holder.txt_wishcount.setText("" + count);
                item.setWishCount(count);
                if (wishClass.isAlreadyexist()) {
                    holder.wishbutton.setLiked(false);
//                    item.setLiked(false);
                    item.setWishCount(count-1);
                    holder.txt_wishcount.setText("" + (count - 1));
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                holder.wishbutton.setLiked(false);
                WishUnlikeClass wishUnlikeClass = new WishUnlikeClass(userid, item.getItemId(), itemtypecode, mContext);
                wishUnlikeClass.execute();
                holder.txt_wishcount.setText("" + (count - 1));
                item.setWishCount(count - 1);
                item.setLiked(false);
                if (wishUnlikeClass.isDeleted()) {
//                    item.setLiked(false);
                    item.setWishCount(count - 1);
                    holder.txt_wishcount.setText("" + (count - 1));
                }
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
                shareItem(item.getItemImage(), item.getItemname(), item.getItemURl());
            }
        });
        return row;
    }

    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new CustomFilter();
        }

        return filter;
    }


    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<ItemPOJO> filters=new ArrayList<ItemPOJO>();

                //get specific items
                for(int i=0;i<filterlist.size();i++)
                {
                    if(filterlist.get(i).getItemname().toUpperCase().contains(constraint))
                    {
                        ItemPOJO p=new ItemPOJO(filterlist.get(i).getItemname(),filterlist.get(i).isLiked(), filterlist.get(i).getWishCount()
                                , filterlist.get(i).getItemId() , filterlist.get(i).getItemPrice()
                                , filterlist.get(i).getItemURl() , filterlist.get(i).getItemtypecode() , filterlist.get(i).getItemImage());

                        filters.add(p);
                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterlist.size();
                results.values=filterlist;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            if (results.count == 0) {
//                itemdatalist=filterlist;
                notifyDataSetInvalidated();
            } else {
                itemdatalist=(ArrayList<ItemPOJO>) results.values;
                notifyDataSetChanged();
            }

        }

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
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
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
    }
}
