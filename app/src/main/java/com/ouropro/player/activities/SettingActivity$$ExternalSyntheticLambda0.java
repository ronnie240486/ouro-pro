package com.ouropro.player.activities;

import com.ouropro.player.dlgfragment.ExternalPlayerDlgFragment;
import com.ouropro.player.dlgfragment.LiveSortDlgFragment;
import com.ouropro.player.dlgfragment.NoConnectionDlgFragment;
import com.ouropro.player.dlgfragment.UpdateDlgFragment;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class SettingActivity$$ExternalSyntheticLambda0 implements ExternalPlayerDlgFragment.ItemPositionListener, UpdateDlgFragment.UpdateAvailableListener, LiveSortDlgFragment.ItemPositionListener, NoConnectionDlgFragment.OnRetryClickListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ SettingActivity f$0;

    public /* synthetic */ SettingActivity$$ExternalSyntheticLambda0(SettingActivity settingActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = settingActivity;
    }

    public final void onItemPosition(int i) {
        switch (this.$r8$classId) {
            case 0:
                this.f$0.lambda$showAutomationDlgFragment$9(i);
                break;
            case 1:
                this.f$0.lambda$showExternalDlgFragment$8(i);
                break;
            case 2:
            case 3:
            case 6:
            case 7:
            default:
                this.f$0.lambda$showLiveStreamFormatDlgFragment$6(i);
                break;
            case 4:
                this.f$0.lambda$showChangeLayoutDlgFragment$3(i);
                break;
            case 5:
                this.f$0.lambda$showChangeTimeFormatDlgFragment$5(i);
                break;
            case 8:
                this.f$0.lambda$showDeviceTypeDlgFragment$7(i);
                break;
        }
    }

    public final void onRetryClick() {
        this.f$0.lambda$showNoConnectionDlgFragment$11();
    }

    public final void onUpdateAvailable() {
        this.f$0.goToUpdate();
    }
}
