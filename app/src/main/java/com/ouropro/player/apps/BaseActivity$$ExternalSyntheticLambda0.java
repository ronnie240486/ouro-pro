package com.ouropro.player.apps;

import android.net.Uri;
import android.os.Bundle;
import androidx.constraintlayout.core.state.Interpolator;
import androidx.constraintlayout.core.state.Transition;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.Bundleable;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DrmSessionEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.amr.AmrExtractor;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.exoplayer2.util.FlagSet;
import com.google.android.exoplayer2.util.ListenerSet;
import com.ouropro.player.activities.SearchActivity;
import com.ouropro.player.activities.SeriesInfoActivity;
import com.ouropro.player.activities.SettingActivity;
import com.ouropro.player.dlgfragment.ClearHistoryDlgFragment;
import com.ouropro.player.dlgfragment.HideCategoryDlgFragment;
import com.ouropro.player.helper.RealmChangeItemListener;
import io.realm.Realm;
import java.lang.reflect.Constructor;
import java.util.Map;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseActivity$$ExternalSyntheticLambda0 implements Interpolator, RealmChangeItemListener, Response.ErrorListener, HideCategoryDlgFragment.OnCategoryChanged, Realm.Transaction, ListenerSet.IterationFinishedEvent, Bundleable.Creator, Consumer, DrmSessionManager.DrmSessionReference, ExtractorsFactory {
    public final /* synthetic */ int $r8$classId;
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$1 = new BaseActivity$$ExternalSyntheticLambda0(1);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$2 = new BaseActivity$$ExternalSyntheticLambda0(2);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$3 = new BaseActivity$$ExternalSyntheticLambda0(3);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$4 = new BaseActivity$$ExternalSyntheticLambda0(4);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$5 = new BaseActivity$$ExternalSyntheticLambda0(5);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$6 = new BaseActivity$$ExternalSyntheticLambda0(6);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$7 = new BaseActivity$$ExternalSyntheticLambda0(7);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$8 = new BaseActivity$$ExternalSyntheticLambda0(8);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$9 = new BaseActivity$$ExternalSyntheticLambda0(9);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$10 = new BaseActivity$$ExternalSyntheticLambda0(10);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE = new BaseActivity$$ExternalSyntheticLambda0(0);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$11 = new BaseActivity$$ExternalSyntheticLambda0(11);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$12 = new BaseActivity$$ExternalSyntheticLambda0(12);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$13 = new BaseActivity$$ExternalSyntheticLambda0(13);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$14 = new BaseActivity$$ExternalSyntheticLambda0(14);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$15 = new BaseActivity$$ExternalSyntheticLambda0(15);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$16 = new BaseActivity$$ExternalSyntheticLambda0(16);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$17 = new BaseActivity$$ExternalSyntheticLambda0(17);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$18 = new BaseActivity$$ExternalSyntheticLambda0(18);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$19 = new BaseActivity$$ExternalSyntheticLambda0(19);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$20 = new BaseActivity$$ExternalSyntheticLambda0(20);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$21 = new BaseActivity$$ExternalSyntheticLambda0(21);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$22 = new BaseActivity$$ExternalSyntheticLambda0(22);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$23 = new BaseActivity$$ExternalSyntheticLambda0(23);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$24 = new BaseActivity$$ExternalSyntheticLambda0(24);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$25 = new BaseActivity$$ExternalSyntheticLambda0(25);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$26 = new BaseActivity$$ExternalSyntheticLambda0(26);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$27 = new BaseActivity$$ExternalSyntheticLambda0(27);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$28 = new BaseActivity$$ExternalSyntheticLambda0(28);
    public static final /* synthetic */ BaseActivity$$ExternalSyntheticLambda0 INSTANCE$29 = new BaseActivity$$ExternalSyntheticLambda0(29);

    public /* synthetic */ BaseActivity$$ExternalSyntheticLambda0(int i) {
        this.$r8$classId = i;
    }

    public final void CategoryChanged() {
        SettingActivity.lambda$showHideCategoryDlgFragment$4();
    }

    public final void accept(Object obj) {
        switch (this.$r8$classId) {
            case 22:
                ((DrmSessionEventListener.EventDispatcher) obj).drmKeysRemoved();
                break;
            case 23:
                ((DrmSessionEventListener.EventDispatcher) obj).drmKeysLoaded();
                break;
            default:
                ((DrmSessionEventListener.EventDispatcher) obj).drmKeysRestored();
                break;
        }
    }

    public final Extractor[] createExtractors() {
        switch (this.$r8$classId) {
            case 28:
                return new Extractor[0];
            default:
                return new Extractor[0];
        }
    }

    public final /* synthetic */ Extractor[] createExtractors(Uri uri, Map map) {
        switch (this.$r8$classId) {
            case 28:
                break;
            default:
                break;
        }
        return createExtractors();
    }

    public final void execute(Realm realm) {
        switch (this.$r8$classId) {
            case 0:
                realm.deleteAll();
                break;
            case 11:
                realm.deleteAll();
                break;
            case 12:
                realm.deleteAll();
                break;
            case 13:
                realm.deleteAll();
                break;
            case 14:
                realm.deleteAll();
                break;
            default:
                realm.deleteAll();
                break;
        }
    }

    public final Bundleable fromBundle(Bundle bundle) {
        return null;
    }

    public final Constructor getConstructor() {
        switch (this.$r8$classId) {
            case 26:
                return null;
            default:
                return null;
        }
    }

    public final float getInterpolation(float f) {
        switch (this.$r8$classId) {
            case 1:
                return Transition.lambda$getInterpolator$1(f);
            case 2:
                return Transition.lambda$getInterpolator$2(f);
            case 3:
                return Transition.lambda$getInterpolator$3(f);
            case 4:
                return Transition.lambda$getInterpolator$4(f);
            case 5:
                return Transition.lambda$getInterpolator$5(f);
            case 6:
                return Transition.lambda$getInterpolator$6(f);
            default:
                return Transition.lambda$getInterpolator$7(f);
        }
    }

    public final void invoke(Object obj, FlagSet flagSet) {
    }

    public final void onErrorResponse(VolleyError volleyError) {
        SeriesInfoActivity.lambda$getSeriesInfo$1(volleyError);
    }

    public final void onItemChanged() {
        switch (this.$r8$classId) {
            case 8:
                SearchActivity.lambda$searchModels$0();
                break;
            case 16:
                ClearHistoryDlgFragment.lambda$setRecentSeries$4();
                break;
            case 17:
                ClearHistoryDlgFragment.lambda$clearRecentSeriesFromRealm$2();
                break;
            case 18:
                ClearHistoryDlgFragment.lambda$clearRecentMoviesFromRealm$1();
                break;
            default:
                ClearHistoryDlgFragment.lambda$setProToVod$3();
                break;
        }
    }

    public final void release() {
    }
}
