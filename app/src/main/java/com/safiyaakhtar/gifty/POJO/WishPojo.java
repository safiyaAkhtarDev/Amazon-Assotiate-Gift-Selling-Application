package com.safiyaakhtar.gifty.POJO;

/**
 * Created by SafiyaAkhtar on 12/17/2018.
 */

public class WishPojo {
    int userid,itemid,itemtypecode;
    String createdAt;
    String item_name;
    public  boolean Liked;
    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }



    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public String getItem_URL() {
        return Item_URL;
    }

    public void setItem_URL(String item_URL) {
        Item_URL = item_URL;
    }

    public String getItem_type_code() {
        return Item_type_code;
    }

    public void setItem_type_code(String item_type_code) {
        Item_type_code = item_type_code;
    }


    String item_image;
    String item_price;
    String Item_URL;
    String Item_type_code;

    public int getWishCount() {
        return WishCount;
    }

    public void setWishCount(int wishCount) {
        WishCount = wishCount;
    }

    int WishCount;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getItemtypecode() {
        return itemtypecode;
    }

    public void setItemtypecode(int itemtypecode) {
        this.itemtypecode = itemtypecode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
