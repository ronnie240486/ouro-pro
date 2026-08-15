package com.ouropro.player.improvements;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/** Mantém apenas a Activity visível para ações de atualização em segundo plano. */
public final class ForegroundActivityTracker {
    private static WeakReference<Activity> current = new WeakReference<>(null);

    private ForegroundActivityTracker() {
    }

    public static void install(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                current = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Activity active = current.get();
                if (active == activity) {
                    current = new WeakReference<>(null);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Activity active = current.get();
                if (active == activity) {
                    current = new WeakReference<>(null);
                }
            }
        });
    }

    public static Activity get() {
        Activity activity = current.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return null;
        }
        return activity;
    }
}
