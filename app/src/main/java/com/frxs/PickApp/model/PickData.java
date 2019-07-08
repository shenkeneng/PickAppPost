package com.frxs.PickApp.model;

/**
 * Created by Endoon on 2016/4/18.
 */
public class PickData {
    private int WaitPickOrderNum;

    private Order WaitPickData;

    private int IsPicked;

    public int getWaitPickOrderNum() {
        return WaitPickOrderNum;
    }

    public void setWaitPickOrderNum(int waitPickOrderNum) {
        WaitPickOrderNum = waitPickOrderNum;
    }

    public Order getWaitPickData() {
        return WaitPickData;
    }

    public void setWaitPickData(Order waitPickData) {
        WaitPickData = waitPickData;
    }

    public int getIsPicked() {
        return IsPicked;
    }

    public void setIsPicked(int isPicked) {
        IsPicked = isPicked;
    }
}
