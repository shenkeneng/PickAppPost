package com.frxs.PickApp.service.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.R;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.utils.DateUtil;
import com.zqprintersdk.PrinterConst;
import com.zqprintersdk.ZQPrinterSDK;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;

public class PrintDataService {

    private static final String TAG = PrintDataService.class.getSimpleName();

    public ZQPrinterSDK prn = null;

    private Context context;

    private OnPrintLisenter lisenter;

    private String deviceAddress;

    private BluetoothDevice btDevice;

    private Order printOrderInfo;

    private Handler mHandler = new Handler();

    public PrintDataService(Context context) {
        this.context = context;

        prn = new ZQPrinterSDK();
        prn.SetCharacterSet("gb2312");
    }

    public void setPrintLisenter(OnPrintLisenter lisenter) {
        this.lisenter = lisenter;
    }

    public void setPrintData(Order order) {
        printOrderInfo = order;
    }


    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    private void notifyConnectedStateChanged(boolean isConnected){
        if (null != lisenter) {
            lisenter.onConnectedStateChanged(isConnected);
        }
    }

    /**
     * 获取设备名称
     *
     * @return String
     */
    public String getDeviceName() {
        return this.btDevice.getName();
    }

    public void print() {
        if (null == btDevice) {
            ToastUtils.show(context, "未指定打印蓝牙打印设备！");
            return;
        }

        boolean nRet = connect(btDevice.getAddress());
        if (nRet) {
            // 连接成功延迟300毫秒打印
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (printOrderInfo == null) {
//						printFrxsTicketTest();
                        printFrxsTicket();
                    } else {
                        printOrderInfo();
                    }
                }
            }, 300);
        }
    }

    /**
     * 连接打印接
     */
    public boolean connect(String deviceAddress) {
        Log.d(TAG, "连接打印机");
        this.deviceAddress = deviceAddress;

        // 连接打印机
        int nRet = prn.Prn_Connect(deviceAddress, context);

        if (nRet != PrinterConst.ErrorCode.SUCCESS) {
//			context.progressDialog.dismiss();
            if (null != lisenter) {
                lisenter.onConnectFailed(nRet);
            }
            return false;
        } else {
            notifyConnectedStateChanged(true);
            return true;
        }
    }

    /**
     * 断开蓝牙设备连接
     */
    public void disconnect() {
        Log.d(TAG, "断开打印机连接");
        prn.Prn_Disconnect();
        notifyConnectedStateChanged(false);
    }

    private void printSpitString(String str, int offLength, int maxLenght) {
        if (!TextUtils.isEmpty(str)) {
            int strlength = str.length();

            String[] strGroup = new String[strlength / maxLenght + 1];
            for (int i = 0; i < strGroup.length; i++) {
                int end = i * maxLenght + maxLenght;
                String spitStr = str.substring(i * maxLenght, end > strlength ? strlength : end);
                if (i != 0) {
                    spitStr = addSpace(spitStr, offLength, false);
                }
                prn.Prn_PrintString(spitStr);
                prn.Prn_LineFeed(1);
            }
        }
    }

    public String addSpace(String str, int count, boolean isAfter) {
        String resultStr = str;
        for (int i = 0; i < count; i++) {
            if (isAfter) {
                resultStr = resultStr + " ";
            } else {
                resultStr = " " + resultStr;
            }
        }

        return resultStr;
    }

    private void printLine() {
        String printStr = "                                                \r\n";
        prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.UNDERLINE,
                PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
        prn.Prn_LineFeed(1);
    }

    private void printFrxsTicketTest() {
        int nRet;

        nRet = prn.SetCharacterSet("gb2312");
        nRet = prn.Prn_PrinterInit();

        String printStr = "没有打印数据\n";
        nRet = prn.Prn_PrintString(printStr);

        nRet = prn.Prn_LineFeed(6);
        nRet = prn.Prn_CutPaper();

        // 打印完成延迟300秒断开蓝牙
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                disconnect();
            }
        }, 300);
    }

    // private String getProductTypeMark(int productType)
    // {
    // String mark = "";
    //
    // int type = Integer.valueOf(productType);
    //
    // switch (type)
    // {
    // case 0:
    // mark = "★";
    // break;
    // case 1:
    // mark = "";
    // break;
    // case 2:
    // mark = "◆";
    // break;
    //
    // default:
    // break;
    // }
    //
    // return mark;
    // }

    private String formatMoney(String price) {
        try {
            DecimalFormat df = new DecimalFormat("######0.00");
            price = df.format(Double.parseDouble(price));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return price;
    }

    private void printOrderInfo() {
        int nRet;

        String printStr = "";
        printStr = printOrderInfo.getStationNumber() + "\r\n";
        nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.CENTER, PrinterConst.Font.BOLD,
                PrinterConst.WidthSize.SIZE5 | PrinterConst.HeightSize.SIZE5);
        printStr = "打印："+FrxsApplication.getInstance().getUserInfo().getEmpName() +"\r\n";
        nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.CENTER, PrinterConst.Font.DEFAULT,
                PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
        prn.Prn_LineFeed(1);

//        prn.Prn_SetAlignment(PrinterConst.Alignment.RIGHT);
//        prn.Prn_PrintQRCode(PrinterConst.QRCode.CHN | PrinterConst.QRCode.NUM, printOrderInfo.getOrderId()  == null ? "" :printOrderInfo.getOrderId());
//        prn.Prn_LineFeed(1);
//		printLine();

        try {
            printStr = "类型：";
            int lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
            printStr = userInfo.getShelfAreaCode() + userInfo.getShelfAreaName();
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            printStr = "下单：";
            lenght = printStr.getBytes("gb2312").length;
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            String orderTime = printOrderInfo.getOrderDate()  == null ? "" : printOrderInfo.getOrderDate();
            Date beginTime = DateUtil.string2Date(orderTime,"yyyy-MM-dd HH:mm");
            String time = DateUtil.format(beginTime, "yyyy-MM-dd HH:mm");
            printStr = time;
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            printStr = "确认：";
            lenght = printStr.getBytes("gb2312").length;
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            String confDate = printOrderInfo.getConfDate()  == null ? "" : printOrderInfo.getConfDate();
            Date confTime = DateUtil.string2Date(confDate,"yyyy-MM-dd HH:mm");
            printStr = DateUtil.format(confTime, "yyyy-MM-dd HH:mm");
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            printStr = "门店：";
            lenght = printStr.getBytes("gb2312").length;
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            printStr = printOrderInfo.getShopName() == null ? "" : printOrderInfo.getShopName();
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            printStr = "线路：";
            lenght = printStr.getBytes("gb2312").length;
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            printStr = printOrderInfo.getLineName() == null ? "" : printOrderInfo.getLineName();
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            printStr = "包含货区：";
            lenght = printStr.getBytes("gb2312").length;
            nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                    PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
            printStr = printOrderInfo.getShelfArea() == null ? "" : printOrderInfo.getShelfArea();
            prn.Prn_PrintString(printStr);
            prn.Prn_LineFeed(1);

            if (printOrderInfo.getOrderCount() > 1) {
                printStr = String.format(context.getResources().getString(R.string.total_order), printOrderInfo.getOrderCount());
                lenght = printStr.getBytes("gb2312").length;
                nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
                        PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);
                prn.Prn_LineFeed(1);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        nRet = prn.Prn_LineFeed(5);
        nRet = prn.Prn_CutPaper();

        // 打印完成延迟300秒断开蓝牙
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                disconnect();
            }
        }, 1000);

    }

    private void printFrxsTicket() {
        int nRet;

        String printStr = "没有打印数据";
        nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.BOLD,
                PrinterConst.WidthSize.SIZE1 | PrinterConst.HeightSize.SIZE1);

        nRet = prn.Prn_LineFeed(6);
        nRet = prn.Prn_CutPaper();

        // 打印完成延迟300秒断开蓝牙
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        }, 300);

    }

//	private void printFrxsTicket()
//	{
//		int nRet;
//
//		String printStr = "";
//		printStr = "02-1000\r\n";
//		nRet = prn.Prn_PrinterInit();
//		nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.RIGHT, PrinterConst.Font.DEFAULT,
//				PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//		prn.Prn_SetAlignment(PrinterConst.Alignment.LEFT);
//		prn.Prn_PrintBarcode("1234567890", PrinterConst.Barcode.CODE128, 60, 2, PrinterConst.BarcodeText.TEXT_BELOW);
//		prn.Prn_LineFeed(1);
//
//		Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.frxs_logo_250x53);
//		nRet = prn.Prn_PrintBitmap(logo, PrinterConst.BitmapSize.ZQSIZE0);
//
//		printLine();
//
//		try
//		{
//			printStr = "收货人";
//			int lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "蔡睿（13975191080）";
//			printSpitString(printStr, 16, 20);
//			// prn.Prn_LineFeed(1);
//
//			printStr = "收货地址";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "湖南长沙县什么街道什么路多少号芙蓉兴盛办公大楼旁边的仓库4楼技术部";
//			printSpitString(printStr, 16, 15);
//
//			printLine();
//
//			prn.Prn_PrintText("商品", PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//			printStr = addSpace("单价", 8, false);
//			prn.Prn_PrintString(printStr);
//			printStr = addSpace("数量", 8, false);
//			prn.Prn_PrintString(printStr);
//			printStr = addSpace("金额", 8, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			prn.Prn_PrintString("★蔡叔的杜蕾斯");
//			prn.Prn_LineFeed(1);
//
//			printStr = "99.00";
//			lenght = "99.00".getBytes("gb2312").length;
//			printStr = addSpace(printStr, 12, false);
//			prn.Prn_PrintString(printStr);
//
//			printStr = "10";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "999.00";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printStr = "蔡叔的杜蕾斯";
//			printStr = addSpace(printStr, 2, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printStr = "2.00";
//			lenght = "2.00".getBytes("gb2312").length;
//			printStr = addSpace(printStr, 12, false);
//			prn.Prn_PrintString(printStr);
//
//			printStr = "1";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "1".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "2.00";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			prn.Prn_PrintString("◆蔡叔用过的杜蕾斯");
//			prn.Prn_LineFeed(1);
//
//			printStr = "0.50";
//			lenght = "0.50".getBytes("gb2312").length;
//			printStr = addSpace(printStr, 12, false);
//			prn.Prn_PrintString(printStr);
//
//			printStr = "1";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "1".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "0.50";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printLine();
//
//			printStr = "网仓小计";
//			prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//			lenght = "网仓小计".getBytes("gb2312").length;
//
//			printStr = "10";
//			printStr = addSpace(printStr, 24 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "999.00";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printStr = "门店小计";
//			prn.Prn_PrintString(printStr);
//			lenght = "门店小计".getBytes("gb2312").length;
//
//			printStr = "1";
//			printStr = addSpace(printStr, 24 - lenght, false);
//			lenght = "1".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "2.00";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printStr = "第三方小计";
//			prn.Prn_PrintString(printStr);
//			lenght = "第三方小计".getBytes("gb2312").length;
//
//			printStr = "1";
//			printStr = addSpace(printStr, 24 - lenght, false);
//			lenght = "1".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "0.50";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printStr = "合计";
//			prn.Prn_PrintString(printStr);
//			lenght = "合计".getBytes("gb2312").length;
//
//			printStr = "10";
//			printStr = addSpace(printStr, 24 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//
//			printStr = "10001.50";
//			printStr = addSpace(printStr, 12 - lenght, false);
//			lenght = "10".getBytes("gb2312").length;
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printLine();
//
//			printStr = "配送费  ";
//			prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "3.00";
//			prn.Prn_PrintString(printStr);
//
//			printStr = "               总金额  ";
//			prn.Prn_PrintString(printStr);
//
//			printStr = "10004.50";
//			prn.Prn_PrintString(printStr);
//			prn.Prn_LineFeed(1);
//
//			printLine();
//
//			printStr = "门店名称";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "芙蓉兴盛早安新城店";
//			printSpitString(printStr, 16, 20);
//
//			printStr = "门店地址";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "湖南长沙县什么街道什么路多少号芙蓉兴盛办公大楼旁边的仓库4楼技术部";
//			printSpitString(printStr, 16, 15);
//
//			printStr = "下单时间";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//
//			printStr = "2015-11-18 21:30";
//			printSpitString(printStr, 16, 20);
//
//			printStr = "订单类型";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//			printStr = "一小时达";
//			printSpitString(printStr, 16, 15);
//
//			printStr = "客服电话";
//			lenght = printStr.getBytes("gb2312").length;
//			printStr = addSpace(printStr, 16 - lenght, true);
//			nRet = prn.Prn_PrintText(printStr, PrinterConst.Alignment.LEFT, PrinterConst.Font.DEFAULT,
//					PrinterConst.WidthSize.SIZE0 | PrinterConst.HeightSize.SIZE0);
//			printStr = "400-600-2220";
//			printSpitString(printStr, 16, 15);
//		}
//		catch (UnsupportedEncodingException e)
//		{
//			e.printStackTrace();
//		}
//
//		nRet = prn.Prn_LineFeed(6);
//		nRet = prn.Prn_CutPaper();
//
//		// 打印完成延迟300秒断开蓝牙
//		mHandler.postDelayed(new Runnable()
//		{
//
//			@Override
//			public void run()
//			{
//				disconnect();
//			}
//		}, 300);
//
//	}

    /**
     * 合计数量
     *
     * @param WicQuantity
     * @param StoresQuantity
     * @param VendorQuantity
     * @return
     */
    private String totalNumber(String WicQuantity, String StoresQuantity, String VendorQuantity) {
        int numberInt = Integer.parseInt(WicQuantity) + Integer.parseInt(StoresQuantity)
                + Integer.parseInt(VendorQuantity);
        String numberStr = String.valueOf(numberInt);
        return numberStr;
    }

    /**
     * 合计金额
     *
     * @param WicTotal
     * @param StoresQuantity
     * @param VendorTotal
     * @return
     */
    private String totalAmount(String WicTotal, String StoresQuantity, String VendorTotal) {
        double numberInt = Double.parseDouble(WicTotal) + Double.parseDouble(StoresQuantity)
                + Double.parseDouble(VendorTotal);
        String numberStr = String.valueOf(numberInt);
        return numberStr;
    }
}
