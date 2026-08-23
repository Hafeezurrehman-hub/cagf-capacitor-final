package com.sakhashmi.cagf;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

/**
 * CAGFNativeBridge implements the native side of the JS stubs already
 * present in every one of the 20 CAGF games:
 *   - IAPBridge  -> exposed to JS as window.CAGFNative
 *   - AdBridge   -> exposed to JS as window.AndroidAds
 *
 * Gems, owned skins, equipped skin, and purchase flags are persisted here
 * via SharedPreferences so they're SHARED across all 20 games (same app,
 * same storage) instead of each game's localStorage fallback, which would
 * be isolated per-file in a plain browser context.
 *
 * TODO before production release:
 *   1. Replace the showInterstitial/showRewarded bodies with real AdMob
 *      calls (see comments below) once you've added the Play Services Ads
 *      dependency and created real ad unit IDs.
 *   2. Replace purchaseRemoveAds/purchaseFamilyPass with real Google Play
 *      Billing Library purchase flows. These currently just flip local
 *      flags for testing.
 */
public class CAGFNativeBridge {
    private static final String PREFS = "cagf_prefs";
    private final SharedPreferences prefs;

    public CAGFNativeBridge(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.contains("owned_skins")) {
            prefs.edit().putString("owned_skins", "default").apply();
        }
    }

    // ---------- Gems (shared currency across all 20 games) ----------
    @JavascriptInterface
    public int getGems() {
        return prefs.getInt("gems", 0);
    }

    @JavascriptInterface
    public void setGems(int value) {
        prefs.edit().putInt("gems", value).apply();
    }

    // ---------- Skins ----------
    @JavascriptInterface
    public boolean ownsSkin(String id) {
        String owned = prefs.getString("owned_skins", "default");
        for (String s : owned.split(",")) {
            if (s.equals(id)) return true;
        }
        return false;
    }

    @JavascriptInterface
    public void grantSkin(String id) {
        String owned = prefs.getString("owned_skins", "default");
        if (!ownsSkin(id)) {
            prefs.edit().putString("owned_skins", owned + "," + id).apply();
        }
    }

    @JavascriptInterface
    public String getEquippedSkin() {
        return prefs.getString("equipped_skin", "default");
    }

    @JavascriptInterface
    public void equipSkin(String id) {
        prefs.edit().putString("equipped_skin", id).apply();
    }

    // ---------- Purchases ----------
    @JavascriptInterface
    public boolean hasFamilyPass() {
        return prefs.getBoolean("family_pass", false);
    }

    @JavascriptInterface
    public void purchaseRemoveAds() {
        // TODO: launch real Google Play Billing purchase flow for the
        // "remove_ads" one-time product here. On successful purchase
        // callback, persist the flag:
        prefs.edit().putBoolean("no_ads", true).apply();
    }

    @JavascriptInterface
    public void purchaseFamilyPass() {
        // TODO: launch real Google Play Billing purchase flow for the
        // "family_pass" product here. On successful purchase callback:
        prefs.edit().putBoolean("family_pass", true).apply();
        prefs.edit().putBoolean("no_ads", true).apply();
        // Grant all skins
        grantSkin("ninjaRed");
        grantSkin("oceanBlue");
        grantSkin("toxicGreen");
        grantSkin("royalGold");
    }

    @JavascriptInterface
    public boolean noAds() {
        return prefs.getBoolean("no_ads", false) || hasFamilyPass();
    }

    // ---------- Ads (exposed to JS as window.AndroidAds) ----------
    @JavascriptInterface
    public void showInterstitial() {
        if (noAds()) return; // respect purchases — never show ads to paying users
        // TODO: replace with real AdMob interstitial call, e.g.:
        //   InterstitialAd.load(context, "ca-app-pub-XXXX/YYYY", adRequest,
        //     new InterstitialAdLoadCallback() { ... show() ... });
        // Use Google's official test unit ID during development:
        //   ca-app-pub-3940256099942544/1033173712
    }

    @JavascriptInterface
    public void showRewarded() {
        // TODO: replace with real AdMob rewarded ad call. Grant the reward
        // (e.g. extra life) only in the onUserEarnedReward callback.
        // Test unit ID: ca-app-pub-3940256099942544/5224354917
    }
}
