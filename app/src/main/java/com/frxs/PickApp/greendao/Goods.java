package com.frxs.PickApp.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entity mapped to table "GOODS".
 */
@Entity
public class Goods implements java.io.Serializable {
    private static final long serialVersionUID = -1;

    @Id
    private Long id;
    private String ID;
    private String OrderID;
    private Integer ProductID;
    private String SKU;
    private String ProductName;
    private String BarCode;
    private String ProductImageUrl400;
    private Integer ShelfAreaID;
    private String ShelfCode;
    private double SaleQty;
    private String SaleUnit;
    private double SalePackingQty;
    private double PickQty;
    private String Remark;
    private double PreQty;
    private double SalePrice;
    private String Unit;
    private double UnitQty;
    private double UnitPrice;
    private String IsGiftStr;
    private String PickTime;// 拣货时间  用来判断商品的拣货状态
    private String BigUnitBarCode;

    private int IsPicked;  //客户端自定义字段是否已经拣货
    @Transient
    private String CurrentUnit; //客户端自定义字段临时保留的字段做单位切换用
    private double pickPackingQty; //客户端自定义字段拣货单位的包装数
    private String pickUnit; //客户端自定义字段拣货单位


    @Generated(hash = 1156754535)
    public Goods(Long id, String ID, String OrderID, Integer ProductID, String SKU, String ProductName,
            String BarCode, String ProductImageUrl400, Integer ShelfAreaID, String ShelfCode,
            double SaleQty, String SaleUnit, double SalePackingQty, double PickQty, String Remark,
            double PreQty, double SalePrice, String Unit, double UnitQty, double UnitPrice,
            String IsGiftStr, String PickTime, String BigUnitBarCode, int IsPicked,
            double pickPackingQty, String pickUnit) {
        this.id = id;
        this.ID = ID;
        this.OrderID = OrderID;
        this.ProductID = ProductID;
        this.SKU = SKU;
        this.ProductName = ProductName;
        this.BarCode = BarCode;
        this.ProductImageUrl400 = ProductImageUrl400;
        this.ShelfAreaID = ShelfAreaID;
        this.ShelfCode = ShelfCode;
        this.SaleQty = SaleQty;
        this.SaleUnit = SaleUnit;
        this.SalePackingQty = SalePackingQty;
        this.PickQty = PickQty;
        this.Remark = Remark;
        this.PreQty = PreQty;
        this.SalePrice = SalePrice;
        this.Unit = Unit;
        this.UnitQty = UnitQty;
        this.UnitPrice = UnitPrice;
        this.IsGiftStr = IsGiftStr;
        this.PickTime = PickTime;
        this.BigUnitBarCode = BigUnitBarCode;
        this.IsPicked = IsPicked;
        this.pickPackingQty = pickPackingQty;
        this.pickUnit = pickUnit;
    }

    @Generated(hash = 1770709345)
    public Goods() {
    }


    public String getCurrentUnit() {
        if (TextUtils.isEmpty(CurrentUnit)) {
            CurrentUnit = TextUtils.isEmpty(pickUnit) ? SaleUnit: pickUnit;
        }
        return CurrentUnit;
    }

    public void setCurrentUnit(String currentUnit) {
        this.CurrentUnit = currentUnit;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrderID() {
        return this.OrderID;
    }

    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public Integer getProductID() {
        return this.ProductID;
    }

    public void setProductID(Integer ProductID) {
        this.ProductID = ProductID;
    }

    public String getSKU() {
        return this.SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getProductName() {
        return this.ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getBarCode() {
        return this.BarCode;
    }

    public void setBarCode(String BarCode) {
        this.BarCode = BarCode;
    }

    public String getProductImageUrl400() {
        return this.ProductImageUrl400;
    }

    public void setProductImageUrl400(String ProductImageUrl400) {
        this.ProductImageUrl400 = ProductImageUrl400;
    }

    public Integer getShelfAreaID() {
        return this.ShelfAreaID;
    }

    public void setShelfAreaID(Integer ShelfAreaID) {
        this.ShelfAreaID = ShelfAreaID;
    }

    public String getShelfCode() {
        return this.ShelfCode;
    }

    public void setShelfCode(String ShelfCode) {
        this.ShelfCode = ShelfCode;
    }

    public double getSaleQty() {
        return this.SaleQty;
    }

    public void setSaleQty(double SaleQty) {
        this.SaleQty = SaleQty;
    }

    public String getSaleUnit() {
        return this.SaleUnit;
    }

    public void setSaleUnit(String SaleUnit) {
        this.SaleUnit = SaleUnit;
    }

    public double getSalePackingQty() {
        return this.SalePackingQty;
    }

    public void setSalePackingQty(double SalePackingQty) {
        this.SalePackingQty = SalePackingQty;
    }

    public double getPickQty() {
        return this.PickQty;
    }

    public void setPickQty(double PickQty) {
        this.PickQty = PickQty;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public double getPreQty() {
        return this.PreQty;
    }

    public void setPreQty(double PreQty) {
        this.PreQty = PreQty;
    }

    public double getSalePrice() {
        return this.SalePrice;
    }

    public void setSalePrice(double SalePrice) {
        this.SalePrice = SalePrice;
    }

    public String getUnit() {
        return this.Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public double getUnitQty() {
        return this.UnitQty;
    }

    public void setUnitQty(double UnitQty) {
        this.UnitQty = UnitQty;
    }

    public double getUnitPrice() {
        return this.UnitPrice;
    }

    public void setUnitPrice(double UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    public String getIsGiftStr() {
        return this.IsGiftStr;
    }

    public void setIsGiftStr(String IsGiftStr) {
        this.IsGiftStr = IsGiftStr;
    }

    public int getIsPicked() {
        return this.IsPicked;
    }

    public void setIsPicked(int IsPicked) {
        this.IsPicked = IsPicked;
    }

    public double getPickPackingQty() {
        return this.pickPackingQty;
    }

    public void setPickPackingQty(double pickPackingQty) {
        this.pickPackingQty = pickPackingQty;
    }

    public String getPickUnit() {
        return this.pickUnit;
    }

    public void setPickUnit(String pickUnit) {
        this.pickUnit = pickUnit;
    }

    public String getPickTime() {
        return PickTime;
    }

    public void setPickTime(String pickTime) {
        PickTime = pickTime;
    }

    public String getBigUnitBarCode() {
        return BigUnitBarCode;
    }

    public void setBigUnitBarCode(String bigUnitBarCode) {
        BigUnitBarCode = bigUnitBarCode;
    }
}
