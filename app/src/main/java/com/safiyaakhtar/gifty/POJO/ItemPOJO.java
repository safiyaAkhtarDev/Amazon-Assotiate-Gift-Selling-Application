package com.safiyaakhtar.gifty.POJO;

/**
 * Created by SafiyaAkhtar on 11/29/2018.
 */

public class ItemPOJO {
    public String Itemname;
    public  boolean Liked;

    public ItemPOJO(String itemname, boolean liked, int wishCount, int itemId, String itemPrice, String itemURl, String itemtypecode, String itemImage) {
        Itemname = itemname;
        Liked=liked;
        WishCount = wishCount;
        ItemId = itemId;
        ItemPrice = itemPrice;
        ItemURl = itemURl;
        Itemtypecode = itemtypecode;
        ItemImage = itemImage;
    }

    public ItemPOJO() {
    }

    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }



    public int getWishCount() {
        return WishCount;
    }

    public void setWishCount(int wishCount) {
        WishCount = wishCount;
    }

    public int WishCount;

    public int getItemId() {
        return ItemId;
    }

    public void setItemId(int itemId) {
        ItemId = itemId;
    }

    public int ItemId;
    public String ItemPrice;
    public String ItemURl;

    public String getItemtypecode() {
        return Itemtypecode;
    }

    public void setItemtypecode(String itemtypecode) {
        Itemtypecode = itemtypecode;
    }

    public String Itemtypecode;

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    public String ItemImage;



    public String getItemname() {
        return Itemname;
    }

    public void setItemname(String itemname) {
        Itemname = itemname;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public String getItemURl() {
        return ItemURl;
    }

    public void setItemURl(String itemURl) {
        ItemURl = itemURl;
    }





}
