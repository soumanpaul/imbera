package com.imebrademoproject;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class ActivityStarterModule extends ReactContextBaseJavaModule {

    ActivityStarterModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "ActivityStarter";
    }

    @ReactMethod
    void navigateToExample() {
        ReactApplicationContext context = getReactApplicationContext();
        Intent intent = new Intent(context, ExampleActivity.class);
        context.startActivity(intent);
    }
    @ReactMethod
    void navigateToMRI() {
        ReactApplicationContext context = getReactApplicationContext();
        Intent intent = new Intent(context, MriActivity.class);
        context.startActivity(intent);
    }
}
