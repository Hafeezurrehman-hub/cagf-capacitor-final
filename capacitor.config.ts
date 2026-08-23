import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.sakhashmi.cagf',
  appName: 'CAGF',
  webDir: 'www',
  // No 'server.url' set on purpose — games load from bundled local assets,
  // not a remote host, matching CAGF's offline-first design.
  android: {
    allowMixedContent: false,
    webContentsDebuggingEnabled: false // set true only during development
  }
};

export default config;
