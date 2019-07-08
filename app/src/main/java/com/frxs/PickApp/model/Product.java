package com.frxs.PickApp.model;

import java.util.List;

/**
 * Created by Endoon on 2016/4/19.
 */
public class Product {
    private String OrderId;

    private List<ProductData> ProductsData;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public List<ProductData> getProductsData() {
        return ProductsData;
    }

    public void setProductsData(List<ProductData> productsData) {
        ProductsData = productsData;
    }
}
