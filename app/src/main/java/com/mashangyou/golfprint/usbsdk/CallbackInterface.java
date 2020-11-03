package com.mashangyou.golfprint.usbsdk;



public abstract interface CallbackInterface
{
  public abstract Common.ERROR_CODE CallbackMethod(CallbackInfo paramGpComCallbackInfo);
}
