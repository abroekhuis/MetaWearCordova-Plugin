package com.mbientlab.metawear.cordova;

import com.mbientlab.metawear.AsyncOperation;
import android.util.Log;
import org.apache.cordova.PluginResult;
import com.mbientlab.metawear.cordova.MWDevice;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.RouteManager.MessageHandler;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Bmm150Magnetometer;

public class MWMagnetometer {

    private MWDevice mwDevice;

    public MWMagnetometer(MWDevice device) {
        mwDevice = device;
    }

    private final AsyncOperation.CompletionHandler<RouteManager> magnetometerHandler =
            new AsyncOperation.CompletionHandler<RouteManager>() {
                @Override
                public void success(RouteManager result) {
                    result.subscribe("magneto_stream_key", new MessageHandler() {
                        @Override
                        public void process(Message msg) {
                            CartesianFloat bField = msg.getData(CartesianFloat.class);
                            JSONObject resultObject = new JSONObject();
                            try {
                                resultObject.put("x", bField.x());
                                resultObject.put("y", bField.y());
                                resultObject.put("z", bField.z());
                            } catch (JSONException e){
                                Log.e("Metawear Cordova Error: ", e.toString());
                            }
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
                                    resultObject);
                            pluginResult.setKeepCallback(true);
                            mwDevice.getMwCallbackContexts().get(mwDevice.START_MAGNETOMETER).sendPluginResult(pluginResult);
                            Log.i("Metawear Cordova BField", bField.toString());
                        }
                    });
                }
            };

    private Bmm150Magnetometer getMagnetometer() {
        Bmm150Magnetometer magnetoModule = null;

        try {
            Log.i("MetaWear plugin", "Get MagnetoMeter");
            Log.i("MetaWear plugin", "Device: " + mwDevice);
            magnetoModule = mwDevice.getMwBoard().getModule(Bmm150Magnetometer.class);
        } catch (UnsupportedModuleException e) {
            Log.e("Metawear Cordova Error: ", e.toString());

            JSONObject resultObject = new JSONObject();
            try {
                resultObject.put(MWDevice.STATUS, MWDevice.MODULE_NOT_SUPPORTED);
                mwDevice.getMwCallbackContexts().get(mwDevice.START_MAGNETOMETER).error(resultObject);
            } catch (JSONException jsonException){
                Log.e("Metawear Cordova Error: ", jsonException.toString());
            }
        }
        return magnetoModule;
    }

    public void startMagnetometer(JSONArray arguments) {
        Log.v("MetaWear Plugin", " Start magnetometer");
        Bmm150Magnetometer.PowerPreset preset = null;

        try {
            Log.i("MetaWear Plugin", "Args: " + arguments.toString());
            JSONObject argumentObject = arguments.getJSONObject(0);
            preset = Bmm150Magnetometer.PowerPreset.valueOf(argumentObject.getString("powerPreset"));
        } catch(JSONException e){
            Log.e("MetaWear Plugin", "Error parsing arguments", e);
        }

        Bmm150Magnetometer magnetoModule = getMagnetometer();

        if (magnetoModule != null){
            Log.v("MetaWear Plugin", " Start magnetometer");
            magnetoModule.setPowerPrsest(preset);
            magnetoModule.enableBFieldSampling();

            magnetoModule.routeData()
                    .fromBField().stream("magneto_stream_key")
                    .commit().onComplete(magnetometerHandler);
            magnetoModule.start();
        }
    }

    public void stopMagnetometer(){
        getMagnetometer().stop();
    }
}
