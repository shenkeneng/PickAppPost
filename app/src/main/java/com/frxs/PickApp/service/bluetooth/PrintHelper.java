package com.frxs.PickApp.service.bluetooth;

import android.content.Context;

/**
 * Created by ewu on 2017/2/21.
 */

public class PrintHelper {

    private static BluetoothService bluetoothService;

    private static PrintDataService printDataService;

    public static BluetoothService getBluetoothService(Context context) {
        if (null == bluetoothService) {
            bluetoothService = new BluetoothService(context.getApplicationContext());
        }

        return bluetoothService;
    }

    public static PrintDataService getPrintDataService(Context context) {
        if (null == printDataService) {
            printDataService = new PrintDataService(context.getApplicationContext());
        }

        return printDataService;
    }
}
