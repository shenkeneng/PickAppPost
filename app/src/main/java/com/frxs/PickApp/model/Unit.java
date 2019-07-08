package com.frxs.PickApp.model;

import java.io.Serializable;

/**
 * Created by Endoon on 2016/4/13.
 */
public class Unit implements Serializable{
    private String Unit;//:”瓶”,
    private int SalePrice;//:10

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public int getSalePrice() {
        return SalePrice;
    }

    public void setSalePrice(int salePrice) {
        SalePrice = salePrice;
    }
}
