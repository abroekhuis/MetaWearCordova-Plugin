package com.mbientlab.metawear.cordova;

import android.util.Log;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Settings;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MWSettings {

    public static final String TAG = "com.mbientlab.metawear.cordova";

    private MWDevice mwDevice;

    public MWSettings(MWDevice device){
        mwDevice = device;
    }

    private Settings getSettings(){
        Settings settings = null;

        try {
            settings = mwDevice.getMwBoard().getModule(Settings.class);
        } catch (UnsupportedModuleException e) {
            Log.e("Metawear Cordova Error", e.toString());
        }

        return settings;
    }

    private final AsyncOperation.CompletionHandler<Settings.AdvertisementConfig> readAdvertisingParametersHandler =
            new AsyncOperation.CompletionHandler<Settings.AdvertisementConfig>() {
                @Override
                public void success(Settings.AdvertisementConfig result) {
                    Log.i("Metawear Cordova AdvertisingParameters: ", result.toString());

                    JSONObject resultObject = new JSONObject();
                    try {
                        resultObject.put("deviceName", result.deviceName());
                        resultObject.put("interval", result.interval());
                        resultObject.put("scanResponse", result.scanResponse());
                        resultObject.put("timeout", result.timeout());
                        resultObject.put("txPower", result.txPower());
                    } catch (JSONException e){
                        Log.e("Metawear Cordova Error", e.toString());
                    }

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
                            resultObject);
                    mwDevice.getMwCallbackContexts().get(mwDevice.READ_ADVERTISING_PARAMETERS).sendPluginResult(pluginResult);
                }

                @Override
                public void failure(Throwable error) {
                    Log.i("Metawear Cordova Error: ", error.toString());
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,
                            "ERROR");
                    mwDevice.getMwCallbackContexts().get(mwDevice.READ_ADVERTISING_PARAMETERS).sendPluginResult(pluginResult);
                }
            };

    public void readAdvertisingParameters() {
        Settings settings = getSettings();

        Log.i("Metawear Cordova", "read adv");
        AsyncOperation<Settings.AdvertisementConfig> result = settings.readAdConfig();
        result.onComplete(readAdvertisingParametersHandler);
    }

    public void setAdvertisingParameters(JSONArray arguments) {
        Settings settings = getSettings();

        Settings.ConfigEditor configuration = settings.configure();
        boolean commit = false;

        try {
            JSONObject argumentObject = arguments.getJSONObject(0);

            if (argumentObject.has("deviceName")) {
                configuration.setDeviceName(argumentObject.getString("deviceName"));
                commit = true;
            }

            if (argumentObject.has("txPower")) {
                configuration.setTxPower((byte) argumentObject.getInt("txPower"));
                commit = true;
            }

            if (argumentObject.has("adInterval") && argumentObject.has("adTimeout")) {
                configuration.setAdInterval((short) argumentObject.getInt("adInterval"), (byte) argumentObject.getInt("adTimeout"));
                commit = true;
            }

            if (commit) {
                configuration.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setConnectionParameters(JSONArray arguments) {
        Settings settings = getSettings();

        Settings.ConnectionParameterEditor connectionParameters = settings.configureConnectionParameters();
        boolean commit = false;

        try {
            JSONObject argumentObject = arguments.getJSONObject(0);

            Log.v(TAG, argumentObject.toString());

            if (argumentObject.has("minConnectionInterval")) {
                connectionParameters.setMinConnectionInterval((float) argumentObject.getDouble("minConnectionInterval"));
                commit = true;
            }

            if (argumentObject.has("maxConnectionInterval")) {
                connectionParameters.setMaxConnectionInterval((float) argumentObject.getDouble("maxConnectionInterval"));
                commit = true;
            }

            if (argumentObject.has("supervisorTimeout")) {
                connectionParameters.setSupervisorTimeout((short) argumentObject.getInt("supervisorTimeout"));
                commit = true;
            }

            if (argumentObject.has("slaveLatency")) {
                connectionParameters.setSlaveLatency((short) argumentObject.getInt("slaveLatency"));
                commit = true;
            }

            if (commit) {
                connectionParameters.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
