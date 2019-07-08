package com.frxs.PickApp.rest.service;


import com.frxs.PickApp.model.AppVersionGetRespData;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.model.OrderCount;
import com.frxs.PickApp.model.PickData;
import com.frxs.PickApp.model.PickingOrderList;
import com.frxs.PickApp.model.SubmitDatas;
import com.frxs.PickApp.model.SubmitResult;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.rest.model.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    /**
     * 登录接口
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/PickingLogin")
    Call<ApiResponse<UserInfo>> PostLogin(@FieldMap Map<String, Object> params);

    /**
     * 待拣订单数量
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetWaitPickingNum")
    Call<ApiResponse<OrderCount>> PostWaitPickingNum(@FieldMap Map<String, Object> params);

    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/PickingUpdatePwd")
    Call<ApiResponse<String>> PostUpdatePwd(@FieldMap Map<String, Object> params);

    /**
     * 正在拣货订单列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetAtPickingOrderList")
    Call<ApiResponse<PickingOrderList>> PostPickingOrderList(@FieldMap Map<String, Object> params);

    /**
     * 拣货完成订单列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetPickingFishOrderList")
    Call<ApiResponse<PickingOrderList>> PostPickingFishOrderList(@FieldMap Map<String, Object> params);

    /**
     * 等待拣货订单列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetWaitPickingOrderList")
    Call<ApiResponse<PickingOrderList>> PostWaitingOrderList(@FieldMap Map<String, Object> params);

    /**
     * 预取拣货订单ID
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/prePickingOrderID")
    Call<ApiResponse<String>> prePickingOrderID(@FieldMap Map<String, Object> params);

    /**
     * v1.9 开始拣货
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetStartPickingOrderInfo")
    Call<ApiResponse<PickData>> PostStartPickingOrderInfo(@FieldMap Map<String, Object> params);


    /**
     * 开始拣货
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/StartPickingHandle")
    Call<ApiResponse<PickData>> PostStartPickGoodsList(@FieldMap Map<String, Object> params);

    /**
     * 拣货完成
     *
     * @return
     */
    @POST("Picking/SubmitPick")
    Call<ApiResponse<SubmitResult>> PostFinishPick(@Body SubmitDatas datas);
//	Call<ApiResponse<String>> PostFinishPick(@FieldMap Map<String,Object> params);

    @FormUrlEncoded
    @POST("Picking/GetPickDetailsCount")
    Call<ApiResponse<Integer>> GetPickDetailsCount(@FieldMap Map<String, Object> params);

    /**
     * 版本更新
     *
     * @param params
     * @return
     */
    //版本更新
    @FormUrlEncoded
    @POST("AppVersion/AppVersionUpdateGet")
    Call<ApiResponse<AppVersionGetRespData>> PostAppVersion(@FieldMap Map<String, Object> params);


    /**
     * 请求当前商品库存
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("Picking/GetOrderStockQty")
    Call<ApiResponse<String>> getOrderStockQty(@FieldMap Map<String, Object> params);

    /**
     * 请求完成拣货商品详情
     */
    @FormUrlEncoded
    @POST("Picking/GetFishOrderDetails")
    Call<ApiResponse<Order>> getPickedOrderGoods(@FieldMap Map<String, Object> params);

    /**
     * 验证组长
     */
    @FormUrlEncoded
    @POST("Picking/CheckShelfAreaOfLogin")
    Call<ApiResponse<String>> IsShelfAreaMaster(@FieldMap Map<String, Object> params);

    /**
     * 订单汇总
     */
    @FormUrlEncoded
    @POST("Picking/GetPostAllOrderList")
    Call<ApiResponse<PickingOrderList>> getAllOrderList(@FieldMap Map<String, Object> params);

    /**
     * 订单汇总商品详情
     */
    @FormUrlEncoded
    @POST("Picking/GetPosAllOrderDetails")
    Call<ApiResponse<Order>> getAllOrderDetails(@FieldMap Map<String, Object> params);
}
