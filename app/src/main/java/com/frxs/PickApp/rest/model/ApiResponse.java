package com.frxs.PickApp.rest.model;

import java.util.List;

/**
 * Created by ewu on 2016/3/23.
 */
public class ApiResponse<T> {

    private String Flag;// 状态

    private String Info;// 信息

    private T Data;           // 单个对象

    private List<T> DataList;

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public List<T> getDataList() {
        return DataList;
    }

    public void setDataList(List<T> dataList) {
        DataList = dataList;
    }
}
