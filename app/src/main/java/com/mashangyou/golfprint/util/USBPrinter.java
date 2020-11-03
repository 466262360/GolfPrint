package com.mashangyou.golfprint.util;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.usbsdk.CallbackInfo;
import com.mashangyou.golfprint.usbsdk.CallbackInterface;
import com.mashangyou.golfprint.usbsdk.Common;
import com.mashangyou.golfprint.usbsdk.Device;
import com.mashangyou.golfprint.usbsdk.DeviceParameters;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.mashangyou.golfprint.util.ESCUtil.ESC;

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
                    debug_str = debug_str + strBuild.toString();
                    debug_strX = debug_strX + str.toString();
                    if (debug_str.length() > (lcd_width * 8 / 10)) {
                        debug_str = debug_str + "\n";

                    }
                    if (debug_strX.length() > (lcd_width * 8 / 10)) {
                        debug_strX = debug_strX + "\n";

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
        print(dx);
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
                err = m_Device.activateASB(true, true, true, true, true, true);
                if (err != Common.ERROR_CODE.SUCCESS) {
                    String errorString = Common.getErrorText(err);
                    ToastUtils.showShort(errorString);
                }
            }
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
                initPrint();
                alignCenter();
                printNewLineText("订场消费凭证");
                printNewLine(2);
                alignLeft();
                printNewLineText("会籍身份");
                printNewLineText("姓名：    " + hashMap.get(Contant.PRINT_NAME));
                printNewLineText("ID：      " + hashMap.get(Contant.PRINT_ID));
                printNewLineText("订场信息");
                printNewLineText("时间：    " + hashMap.get(Contant.PRINT_DATE));
                printNewLineText("地点：    " + hashMap.get(Contant.PRINT_GOLFNAME));
                if (!TextUtils.isEmpty(hashMap.get(Contant.PRINT_FREQUENCY))) {
                    String[] split = hashMap.get(Contant.PRINT_FREQUENCY).split("同组");
                    printNewLineText("年度剩余： " + "个人" + split[0] + " 同组" + split[1]);
                }
                printNewLineText("订单号：  " + hashMap.get(Contant.PRINT_ORDER));
                printNewLineText("核销时间： " + hashMap.get(Contant.PRINT_CURRENT_DATE));
                printNewLineText("签名");
                printNewLine(2);
                printNewLineText("______________________________________");
                cutPaper();
            }
    }

    private void initPrint() {
        m_Device.sendCommand("ESC @");
    }

    private void printNewLineText(String text) {
        String command = String.format("%s \n", new Object[]{text});
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
            initPrint();
            alignCenter();
            printNewLineText("订场消费凭证");
            printNewLine(2);
            alignLeft();
            printNewLineText("会籍身份");
            printNewLineText("姓名：    " + hashMap.get(Contant.PRINT_NAME));
            printNewLineText("ID：      " + hashMap.get(Contant.PRINT_ID));
            printNewLineText("订场信息");
            printNewLineText("时间：    " + hashMap.get(Contant.PRINT_DATE));
            printNewLineText("地点：    " + hashMap.get(Contant.PRINT_GOLFNAME));
            if (!TextUtils.isEmpty(hashMap.get(Contant.PRINT_FREQUENCY))) {
                String[] split = hashMap.get(Contant.PRINT_FREQUENCY).split("同组");
                printNewLineText("年度剩余： " + "个人" + split[0] + " 同组" + split[1]);
            }
            printNewLineText("订单号：  " + hashMap.get(Contant.PRINT_ORDER));
            printNewLineText("核销时间： " + hashMap.get(Contant.PRINT_CURRENT_DATE));
            printNewLine(1);
            printNewLineText("签名");
            printNewLine(2);
            printNewLineText("______________________________________");
            cutPaper();
        }

    }


    public void printText(String string) {
        Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;

        font = Common.FONT.FONT_A;
        bold = false;
        underlined = false;
        doubleHeight = false;
        doubleWidth = false;
        sleep(1000);
        try {
            if (m_Device.isDeviceOpen()) {
                switch (location) {
                    case 0:
                        err = m_Device.selectAlignment(Common.ALIGNMENT.LEFT);
                        if (err != Common.ERROR_CODE.SUCCESS) {
                            String errorString = Common.getErrorText(err);
                            ToastUtils.showShort(errorString);
                        }
                        break;
                    case 1:
                        err = m_Device.selectAlignment(Common.ALIGNMENT.CENTER);
                        if (err != Common.ERROR_CODE.SUCCESS) {
                            String errorString = Common.getErrorText(err);
                            ToastUtils.showShort(errorString);
                        }
                        break;
                    case 2:
                        err = m_Device.selectAlignment(Common.ALIGNMENT.RIGHT);
                        if (err != Common.ERROR_CODE.SUCCESS) {
                            String errorString = Common.getErrorText(err);
                            ToastUtils.showShort(errorString);
                        }
                        break;
                }
                if (err == Common.ERROR_CODE.SUCCESS) {
                    err = m_Device.printString(string, font, bold, underlined, doubleHeight, doubleWidth);
                    if (err != Common.ERROR_CODE.SUCCESS) {
                        String errorString = Common.getErrorText(err);
                        ToastUtils.showShort(errorString);
                    }
                }
            } else {
                ToastUtils.showShort("Device is not open");
            }
        } catch (Exception e) {
            ToastUtils.showShort(e.toString() + "-" + e.getMessage());
        }
    }

    public void cutPaper() {
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

    public byte[] byteMerger(byte[][] byteList) {

        int length = 0;
        for (int i = 0; i < byteList.length; i++) {
            length += byteList[i].length;
        }
        byte[] result = new byte[length];

        int index = 0;
        for (int i = 0; i < byteList.length; i++) {
            byte[] nowByte = byteList[i];
            for (int k = 0; k < byteList[i].length; k++) {
                result[index] = nowByte[k];
                index++;
            }
        }
        for (int i = 0; i < index; i++) {

        }
        return result;
    }

    public byte[] nextLine(int lineNum) {
        byte[] result = new byte[3];
        result[0] = 0x1B;
        result[1] = 0x64;
        result[2] = 0x02;
        return result;
    }

    public byte[] nextLine1() {
        byte[] result = new byte[3];
        result[0] = 0x1B;
        result[1] = 0x64;
        result[2] = 0x01;
        return result;
    }


    public void alignLeft() {
        m_Device.sendCommand("ESC a 0");
    }

    public void alignCenter() {
        m_Device.sendCommand("ESC a 1");
    }

}
