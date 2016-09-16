package com.mbientlab.metawear.cordova;

import android.util.Log;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.RouteManager.MessageHandler;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Settings;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by Lance Gleason of Polyglot Programming LLC. on 10/11/2015.
 * http://www.polyglotprogramminginc.com
 * https://github.com/lgleasain
 * Twitter: @lgleasain
 *
 */

public class MWAccelerometer{

    private MWDevice mwDevice;

//    private Logging loggingModule;

//    private Handler handler;

//    private final Runnable downloadLog= new Runnable() {
//        @Override
//        public void run() {
//            loggingModule.downloadLog(0.1f, new Logging.DownloadHandler() {
//                @Override
//                public void onProgressUpdate(int nEntriesLeft, int totalEntries) {
//                    Log.i("test", String.format("Progress: %d/%d/", nEntriesLeft, totalEntries));
//
//                    if (nEntriesLeft == 0) {
//                        Log.i("test", "Log download completed");
//                        handler.post(downloadLog);
//                    }
//                }
//
//                @Override
//                public void receivedUnknownLogEntry(byte logId, Calendar timestamp, byte[] data) {
//                    Log.i("test", String.format("Unknown log entry: {id: %d, data: %s}", logId, Arrays.toString(data)));
//                }
//            });
//        }
//    };

    public MWAccelerometer(MWDevice device){
        mwDevice = device;
    }

    private final AsyncOperation.CompletionHandler<RouteManager> accelerometerHandler =
        new AsyncOperation.CompletionHandler<RouteManager>(){
            @Override
            public void success(RouteManager result){
//                result.setLogMessageHandler("accel_stream_key", new MessageHandler() {
//                    @Override
//                    public void process(Message message) {
//                            CartesianFloat axes = message.getData(CartesianFloat.class);
//                            JSONObject resultObject = new JSONObject();
//                            try {
//                                resultObject.put("x", axes.x());
//                                resultObject.put("y", axes.y());
//                                resultObject.put("z", axes.z());
//                            } catch (JSONException e){
//                                Log.e("Metawear Cordova Error: ", e.toString());
//                            }
//                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
//                                                                         resultObject);
//                            pluginResult.setKeepCallback(true);
//                            mwDevice.getMwCallbackContexts().get(mwDevice.START_ACCELEROMETER).sendPluginResult(pluginResult);
//                            Log.i("Metawear Cordova Axis", axes.toString());
//                    }
//                });


                result.subscribe("accel_stream_key", new MessageHandler() {
                        @Override
                        public void process(Message msg){
                            long millis = msg.getTimestamp().getTimeInMillis();
                            CartesianFloat axes = msg.getData(CartesianFloat.class);
                            JSONObject resultObject = new JSONObject();
                            try {
                                resultObject.put("x", axes.x());
                                resultObject.put("y", axes.y());
                                resultObject.put("z", axes.z());
                                resultObject.put("time", millis);
                            } catch (JSONException e){
                                Log.e("Metawear Cordova Error", e.toString());
                            }

                            final JSONObject obj = resultObject;
                            mwDevice.cordova.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,
                                            obj);
                                    pluginResult.setKeepCallback(true);
                                    mwDevice.getMwCallbackContexts().get(mwDevice.START_ACCELEROMETER).sendPluginResult(pluginResult);
                                }
                            });
                            Log.i("Metawear Cordova Axis", axes.toString());
                        }
                    });
            }
        };

    private Accelerometer getAccelerometer(){
        Accelerometer accelModule = null;

        try {
            accelModule= mwDevice.getMwBoard().getModule(Accelerometer.class);
//            loggingModule = mwDevice.getMwBoard().getModule(Logging.class);
        }catch(UnsupportedModuleException e){
            Log.e("Metawear Cordova Error", e.toString());
        }
        return accelModule;
    }

    public void startAccelerometer(JSONArray arguments){
//        try {
//            Settings settings = mwDevice.getMwBoard().getModule(Settings.class);
//
//            Log.i("MetaWear Plugin", "Settings: " + settings);
//
//            settings.configureConnectionParameters()
////                    .setMinConnectionInterval(10f)
//                    .setMaxConnectionInterval(20f)
////                    .setSupervisorTimeout((short) 6000)
////                    .setSlaveLatency((short) 10)
//                    .commit();
//        }catch(UnsupportedModuleException e){
//            Log.e("Metawear Cordova Error", e.toString());
//        }


        Log.i("MetaWear Plugin", "Start accelerometer");
        double outputDataRate = 50;
        double axisSamplingRange = 4;

        try {
            Log.i("MetaWear Plugin", "Args: " + arguments.toString());
            JSONObject argumentObject = arguments.getJSONObject(0);
            outputDataRate = argumentObject.getDouble("outputDataRate");
            axisSamplingRange = argumentObject.getDouble("axisSamplingRange");

            Log.i("MetaWear Plugin", "Settings: " + outputDataRate + " " + axisSamplingRange);
        } catch(JSONException e){
            Log.e("Metawear Cordova Error", e.toString());
        }

        Accelerometer accelModule = getAccelerometer();
        accelModule.routeData()
            .fromAxes().stream("accel_stream_key")
            .commit().onComplete(accelerometerHandler);

//        accelModule.routeData()
//            .fromAxes().log("accel_stream_key")
//            .commit().onComplete(accelerometerHandler);


        // Set the sampling frequency to 50Hz, or closest valid ODR
        accelModule.setOutputDataRate(new Float(outputDataRate));
        // Set the measurement range to +/- 4g, or closet valid range
        accelModule.setAxisSamplingRange(new Float(axisSamplingRange));

        // enable axis sampling
        accelModule.enableAxisSampling();

        // Switch the accelerometer to active mode
        accelModule.start();

//        handler = new Handler();
//        handler.postDelayed(downloadLog, 500);

    }

    public void stopAccelerometer(){
        getAccelerometer().stop();
    }
}
