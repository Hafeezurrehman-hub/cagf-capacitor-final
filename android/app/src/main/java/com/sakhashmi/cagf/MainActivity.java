package com.sakhashmi.cagf;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject the same bridge object under two JS-facing names, matching
        // what the JS stubs in every CAGF game already look for:
        //   - window.CAGFNative  (used by IAPBridge: gems, skins, purchases)
        //   - window.AndroidAds  (used by AdBridge: interstitial/rewarded)
        CAGFNativeBridge bridge = new CAGFNativeBridge(this);
        this.bridge.getWebView().addJavascriptInterface(bridge, "CAGFNative");
        this.bridge.getWebView().addJavascriptInterface(bridge, "AndroidAds");
    }
}
