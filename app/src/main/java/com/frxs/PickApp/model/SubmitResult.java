package com.frxs.PickApp.model;

/**
 * Created by Endoon on 2016/7/18.
 */
public class SubmitResult {
    // 是否执行成功的标识
    private boolean IsResult;//":false,
    // 返回标识：0、提交成功 1、找不到订单编号对应的数据 2、订单已完成拣货 3、订单的当前获取已完成拣货（只针对APP）4、提交的商品数据为空
    private int Flag;//":2,
    // 返回的文字
    private String Info;

    public boolean isResult() {
        return IsResult;
    }

    public void setResult(boolean result) {
        IsResult = result;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }
}
