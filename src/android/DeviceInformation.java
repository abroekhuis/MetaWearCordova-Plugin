package com.mbientlab.metawear.cordova;

import android.util.Log;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.MetaWearBoard;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by Lance Gleason of Polyglot Programming LLC. on 07/12/2016.
 * http://www.polyglotprogramminginc.com
 * https://github.com/lgleasain
 * Twitter: @lgleasain
 *
 */

public class DeviceInformation {

    private MWDevice mwDevice;

    public DeviceInformation(MWDevice device){
        mwDevice = device;
    }

    private final AsyncOperation.CompletionHandler<MetaWearBoard.DeviceInformation> readDeviceInformationHandler =
            new AsyncOperation.CompletionHandler<MetaWearBoard.DeviceInformation>() {
                @Override
                public void success(final MetaWearBoard.DeviceInformation result){
                    Log.i("Metawear Cordova DeviceInformation: ", result.toString());

                    JSONObject resultObject = new JSONObject();
                    try {
                        resultObject.put("manufacturer", result.manufacturer());
                        resultObject.put("modelNumber", result.modelNumber());
                        resultObject.put("serialNumber", result.serialNumber());
                        resultObject.put("hardwareRevision", result.hardwareRevision());
                        resultObject.put("firmwareRevision", result.firmwareRevision());
                    } catch (JSONException e){
                        Log.e("Metawear Cordova Error", e.toString());
                    }

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
                                                                 resultObject);
                    mwDevice.getMwCallbackContexts().get(mwDevice.READ_DEVICE_INFORMATION).sendPluginResult(pluginResult);
                }

                @Override
                public void failure(Throwable error){
                    Log.e("Metawear Cordova Error: ", error.toString());
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,
                                                                 "ERROR");
                    mwDevice.getMwCallbackContexts().get(mwDevice.READ_DEVICE_INFORMATION).sendPluginResult(pluginResult);
                }
            };
        
    public void readDeviceInformation() {
        AsyncOperation<MetaWearBoard.DeviceInformation> result = mwDevice.getMwBoard().readDeviceInformation();
        result.onComplete(readDeviceInformationHandler);
    }

}
