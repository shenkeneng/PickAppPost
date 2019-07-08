package com.frxs.PickApp.model;

/**
 * Created by Endoon on 2016/4/21.
 */
public class ProductData {
    private String ID;
    private int ShelfAreaID;
    private String SaleUnit;
    private double SalePackingQty;
    private double PickQty;
    private int ProductID;
    private double PreQty;
    private int IsSet;
    private String ProductName;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getShelfAreaID() {
        return ShelfAreaID;
    }

    public void setShelfAreaID(int shelfAreaID) {
        ShelfAreaID = shelfAreaID;
    }

    public String getSaleUnit() {
        return SaleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        SaleUnit = saleUnit;
    }

    public double getSalePackingQty() {
        return SalePackingQty;
    }

    public void setSalePackingQty(double salePackingQty) {
        SalePackingQty = salePackingQty;
    }

    public double getPickQty() {
        return PickQty;
    }

    public void setPickQty(double pickQty) {
        PickQty = pickQty;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public double getPreQty() {
        return PreQty;
    }

    public void setPreQty(double preQty) {
        PreQty = preQty;
    }

    public int getIsSet() {
        return IsSet;
    }

    public void setIsSet(int isSet) {
        IsSet = isSet;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }
}
