package com.example.shop;

public class ShoppingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartCount;

    public ShoppingItem() {

    }

    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource, int cartCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.cartCount = cartCount;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCartCount() {
        return cartCount;
    }

    public String _getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
