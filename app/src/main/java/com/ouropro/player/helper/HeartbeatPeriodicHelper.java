package com.ouropro.player.helper;

import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatPeriodicHelper {
    private String content;
    private Handler handler;
    private String mac;
    private boolean running;
    private String serverUrl;
    private Runnable ticker;

    public void sendAndSchedule() {
        if (this.running) {
            HeartbeatHelper.sendHeartbeat(this.mac, this.content, this.serverUrl);
            Runnable runnable = new Runnable() { // from class: com.ouropro.player.helper.HeartbeatPeriodicHelper.1
                public void run() {
                    HeartbeatPeriodicHelper.this.sendAndSchedule();
                }
            };
            this.ticker = runnable;
            this.handler.postDelayed(runnable, 30000L);
        }
    }

    public void start(String str, String str2, String str3) {
        this.mac = str;
        this.content = str2;
        this.serverUrl = str3;
        this.running = true;
        this.handler = new Handler(Looper.getMainLooper());
        sendAndSchedule();
    }

    public void stop() {
        Runnable runnable;
        this.running = false;
        Handler handler = this.handler;
        if (handler != null && (runnable = this.ticker) != null) {
            handler.removeCallbacks(runnable);
        }
        if (this.mac == null || this.serverUrl == null) {
            return;
        }
        HeartbeatClearHelper.clearHeartbeat(this.mac, this.serverUrl);
    }

    public void updateContent(String str) {
        this.content = str;
    }
}
