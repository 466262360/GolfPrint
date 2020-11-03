 package com.mashangyou.golfprint.usbsdk;
 
 import java.util.Vector;

 
 public class BBB extends FFF
 {
   BBB(DeviceParameters parameters)
   {
     super(parameters);
   }
 
   Common.ERROR_CODE openPort()
   {
     return null;
   }
 
   Common.ERROR_CODE closePort()
   {
     return null;
   }
 
   boolean isPortOpen()
   {
     return false;
   }
 
   Common.ERROR_CODE writeData(Vector<Byte> data)
   {
     return null;
   }
   Common.ERROR_CODE writeData(byte data[])
   {
     return null;
   }
   protected Common.ERROR_CODE writeDataImmediately(Vector<Byte> data)
   {
     return null;
   }
 }
