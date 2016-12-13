package com.mbientlab.metawear.cordova;

import android.util.Log;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Haptic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MWHaptic {

    private MWDevice mwDevice;

    public MWHaptic(MWDevice device){
        mwDevice = device;
    }

    private Haptic getHaptic(){
        Haptic haptic = null;

        try {
            haptic = mwDevice.getMwBoard().getModule(Haptic.class);
        } catch (UnsupportedModuleException e) {
            Log.e("Metawear Cordova Error", e.toString());
        }

        return haptic;
    }

    public void startBuzzer(JSONArray arguments) {
        Haptic haptic = getHaptic();

        try {
            JSONObject argumentObject = arguments.getJSONObject(0);

            if (argumentObject.has("pulseWidth")) {
                int pulseWidth = argumentObject.getInt("pulseWidth");
                Log.i("Metawear Cordova", "Start buzzer: " + pulseWidth);
                haptic.startBuzzer((short) pulseWidth);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
