package com.mashangyou.golfprint.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2020/11/4.
 * Des:
 */
public class USBNewPrinter {
    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    private static USBNewPrinter mInstance;

    private Context mContext;
    private UsbDevice mUsbDevice;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        mUsbDevice = usbDevice;
                        LogUtils.d(mUsbDevice.getVendorId() + "-" + mUsbDevice.getProductId());
                    } else {
                        LogUtils.d("Permission denied for device " + usbDevice);
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    LogUtils.d("Device closed");
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.close();
                    }
                }
            }
        }
    };

    private USBNewPrinter() {

    }

    public static USBNewPrinter getInstance() {
        if (mInstance == null) {
            mInstance = new USBNewPrinter();
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public static void initPrinter(Context context) {
        getInstance().init(context);
    }

    /**
     * 销毁打印机持有的对象
     */
    public static void destroyPrinter() {
        getInstance().destroy();
    }

    private void init(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);

        UsbDevice usbDevice = getUsbDevice(mUsbManager);
        mUsbManager.requestPermission(usbDevice, mPermissionIntent);
    }

    private void destroy() {
        mContext.unregisterReceiver(mUsbDeviceReceiver);

        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }

        mContext = null;
        mUsbManager = null;
    }

    private static UsbDevice getUsbDevice(UsbManager um) {
        HashMap<String, UsbDevice> lst = um.getDeviceList();

        Iterator<UsbDevice> deviceIterator = lst.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice dev = (UsbDevice) deviceIterator.next();
            if (dev.getVendorId() == 0x0485) {

                return dev;
            }
            if (dev.getVendorId() == 0xB000) {

                return dev;
            }

            if (dev.getVendorId() == 0x28E9) {

                return dev;
            }


            if (dev.getVendorId() == 0x0289) {

                return dev;
            }
            if (dev.getVendorId() == 0x28e9) {
                //debug.d("dev.getVendorId() == 0x28e9   :"+dev.getInterfaceCount());
                return dev;
            }
        }

        return null;
    }

    /**
     * 打印方法
     *
     * @param msg
     */
    public void print(String msg) {
        final String printData = msg;
        if (mUsbDevice != null) {
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                final UsbEndpoint ep = usbInterface.getEndpoint(i);
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                    if (mUsbDeviceConnection != null) {
                        Toast.makeText(mContext, "Device connected", Toast.LENGTH_SHORT).show();
                        mUsbDeviceConnection.claimInterface(usbInterface, true);
                        try {
                            LogUtils.d(mUsbDevice.getVendorId() + "-" + mUsbDevice.getProductId());
                            String s = "abcdeddsdsddsdrfgghhh";
                            byte[]  bytes = printData.getBytes("gb2312");
                            int b = mUsbDeviceConnection.bulkTransfer(ep, bytes, bytes.length, 1000);
                            LogUtils.d("Return Status", "b-->" + b);
                            mUsbDeviceConnection.releaseInterface(usbInterface);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }

        } else {
            Toast.makeText(mContext, "No available USB print device", Toast.LENGTH_SHORT).show();
        }
    }
}
