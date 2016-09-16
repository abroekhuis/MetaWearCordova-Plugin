package com.mbientlab.metawear.cordova;

import android.util.Log;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Settings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MWSettings {

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
