package com.mashangyou.golfprint.util;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.usbsdk.CallbackInfo;
import com.mashangyou.golfprint.usbsdk.CallbackInterface;
import com.mashangyou.golfprint.usbsdk.Common;
import com.mashangyou.golfprint.usbsdk.Device;
import com.mashangyou.golfprint.usbsdk.DeviceParameters;
import com.mashangyou.golfprint.usbsdk.PrintStatesCallback;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import androidx.annotation.NonNull;

/**
 * Created by Administrator on 2020/10/28.
 * Des:
 */
public class USBPrinter {
    public static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    private static USBPrinter mInstance;

    private Context mContext;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;

    private UsbEndpoint ep, printerEp;
    private UsbInterface usbInterface;

    private static final int TIME_OUT = 100000;
    private Device m_Device;
    private DeviceParameters m_DeviceParameters;
    private Boolean bold;
    private Common.FONT font;
    private Boolean underlined;
    private Boolean doubleHeight;
    private Boolean doubleWidth;
    private int location = 0;
    String debug_str = "";
    String debug_strX = "";
    private int lcd_width = 10;
    private byte[] line10;
    private byte[] c7;
    private byte[] line9;
    private byte[][] cmdBytes;
    private PrintStatesCallback printStatesCallback;

    public static USBPrinter getInstance() {
        if (mInstance == null) {
            synchronized (USBPrinter.class) {
                if (mInstance == null) {
                    mInstance = new USBPrinter();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public void initPrinter(Context context) {
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init(Context context) {
        mContext = context;
        openDevice();
    }

    private void openDevice() {
        m_Device = new Device();
        m_DeviceParameters = new DeviceParameters();
        m_Device.registerCallback(new CallbackInterface() {
            @Override
            public Common.ERROR_CODE CallbackMethod(CallbackInfo cbInfo) {

                Common.ERROR_CODE retval = Common.ERROR_CODE.SUCCESS;

                try {
                    StringBuilder str = new StringBuilder();
                    StringBuilder strBuild = new StringBuilder();
                    for (int i = 0; i < cbInfo.receiveCount; i++) {
                        //USBPort.this.m_receiveBuffer.add(Byte.valueOf(USBPort.this.m_receiveData[i]));
                        str.append(String.format(" %x", cbInfo.m_receiveData[i]));
                        strBuild.append(String.format("%c", (char) cbInfo.m_receiveData[i]));
                    }
                    LogUtils.d(str.toString());
                    String receive = str.toString();
                    String[] split = receive.split(" ");
                    LogUtils.d(split[0] + "-" + split[1] + "-" + split[2] + "-" + split[3] + "-" + split[4]);
                    Message message = new Message();
                    if (split[2].equals("8")) {
                        message.arg1 = 1;
                        message.obj = "切刀错误";
                        handler.sendMessage(message);
                    } else if (split[2].equals("40")) {
                        message.arg1 = 1;
                        message.obj = "打印机过热";
                        handler.sendMessage(message);
                    } else if (split[3].equals("c")) {
                        message.arg1 = 1;
                        message.obj = "打印机缺纸";
                        handler.sendMessage(message);
                    } else {
                        message.arg1 = 0;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    ToastUtils.showShort(e.toString() + " - " + e.getMessage());
                }

                return retval;
            }
        });
        Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;
        m_DeviceParameters.PortType = Common.PORT_TYPE.USB;
        m_DeviceParameters.PortName = "";
        m_DeviceParameters.ApplicationContext = mContext;
        byte[] dx = {29, 103, 102};
        //print(dx);

        if (err == Common.ERROR_CODE.SUCCESS) {
            //set the parameters to the device
            err = m_Device.setDeviceParameters(m_DeviceParameters);
            if (err != Common.ERROR_CODE.SUCCESS) {
                String errorString = Common.getErrorText(err);
                ToastUtils.showShort(errorString);
            }

            if (err == Common.ERROR_CODE.SUCCESS) {
                //open the device
                err = m_Device.openDevice();
                if (err != Common.ERROR_CODE.SUCCESS) {
                    String errorString = Common.getErrorText(err);
                    ToastUtils.showShort(errorString);
                }
            }

            if (err == Common.ERROR_CODE.SUCCESS) {
                //activate ASB sending
                //err = m_Device.activateASB(true, true, true, true, true, true);
                if (err != Common.ERROR_CODE.SUCCESS) {
                    String errorString = Common.getErrorText(err);
                    ToastUtils.showShort(errorString);
                }
            }
            //m_Device.sendCommand("ESC 6 0");
        }
    }

    public void print(byte[] bs) {
        Vector<Byte> data = new Vector<Byte>(bs.length);
        for (int i = 0; i < bs.length; i++) {

            data.add(bs[i]);
        }
        m_Device.sendData(data);
    }

    public void printTest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Contant.PRINT_NAME, "张三");
        hashMap.put(Contant.PRINT_ID, "44453457765");
        hashMap.put(Contant.PRINT_MEMBER_NAME, "个绝对高度");
        hashMap.put(Contant.PRINT_DATE, "2020-10-30 13:12");
        hashMap.put(Contant.PRINT_PEOPLE, "2");
        hashMap.put(Contant.PRINT_CAVES, "4");
        hashMap.put(Contant.PRINT_ORDER, "3343435");
        hashMap.put(Contant.PRINT_GOLFNAME, "吃饭饭方法");
        hashMap.put(Contant.PRINT_FREQUENCY, "14/18(次)  同组6/6(次)");
        hashMap.put(Contant.PRINT_CURRENT_DATE, "2020-10-30 13:12");
        if (m_Device.isDeviceOpen()) {
            printText(PrintContract.createXxTxt(hashMap));
        }
    }

    private void initPrint() {
        m_Device.sendCommand("ESC @");
    }

    private void doubleWH() {
        int options = 0;
        options |= 1;
        options |= 16;
        String command = String.format("GS ! %d", new Object[]{Integer.valueOf(options)});
        m_Device.sendCommand(command);
    }

    private void defaultWH() {
        int options = 0;
        String command = String.format("GS ! %d", new Object[]{Integer.valueOf(options)});
        m_Device.sendCommand(command);
    }

    public void unregisterCallback(){
        m_Device.unregisterCallback();
    }

    public void checkPrint() {
        m_Device.sendCommand("GS a 1");
    }

    private void printText(String text) {
        try {
            byte[] bs = text.getBytes("gb2312");
            Vector<Byte> data = new Vector<Byte>(bs.length);
            for (int i = 0; i < bs.length; i++) {
                data.add(bs[i]);
            }
            m_Device.sendData(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void printNewLine(int count) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buffer.append("\n");
        }
        String command = buffer.toString();
        try {
            byte[] bs = command.getBytes("gb2312");
            Vector<Byte> data = new Vector<Byte>(bs.length);
            for (int i = 0; i < bs.length; i++) {
                data.add(bs[i]);
            }
            m_Device.sendData(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void printContent(Map<String, String> hashMap) {
        if (m_Device.isDeviceOpen()) {
            printText(PrintContract.createXxTxt(hashMap));
        }

    }

    private void cutPaper() {
        Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;
        //test=32;
        try {
            if (m_Device.isDeviceOpen()) {
                err = m_Device.cutPaper();
                if (err != Common.ERROR_CODE.SUCCESS) {
                    String errorString = Common.getErrorText(err);
                    ToastUtils.showShort(errorString);
                }
                byte[] dx = {29, 103, 102, 0x10, 0x04, 0x04};

            } else {
                ToastUtils.showShort("Device is not open");
            }
        } catch (Exception e) {
            ToastUtils.showShort(e.getMessage());
        }
    }

    public void closeDevice() {
        if (m_Device != null) {
            Common.ERROR_CODE err = m_Device.closeDevice();
            if (err != Common.ERROR_CODE.SUCCESS) {
                String errorString = Common.getErrorText(err);
                ToastUtils.showShort(errorString);
            }
        }
    }

    private void sleep(int ms) {
        // debug.d(TAG,"start sleep "+ms);
        try {
            java.lang.Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // debug.d(TAG,"end sleep "+ms);
    }

    private void alignLeft() {
        m_Device.sendCommand("ESC a 0");
    }

    private void alignCenter() {
        m_Device.sendCommand("ESC a 1");
    }

    public void setOnCallBack(PrintStatesCallback onCallBack) {
        printStatesCallback = onCallBack;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.arg1 == 0) {
                if (printStatesCallback != null)
                    printStatesCallback.success();
            } else if (msg.arg1 == 1) {
                String error = (String) msg.obj;
                ToastUtils.showLong(error);
                if (printStatesCallback != null)
                    printStatesCallback.error(error);
            }
        }
    };
}
