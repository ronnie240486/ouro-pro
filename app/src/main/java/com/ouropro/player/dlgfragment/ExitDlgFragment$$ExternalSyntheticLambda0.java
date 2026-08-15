package com.ouropro.player.dlgfragment;

import android.content.DialogInterface;
import android.view.KeyEvent;
import androidx.fragment.app.DialogFragment;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class ExitDlgFragment$$ExternalSyntheticLambda0 implements DialogInterface.OnKeyListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ DialogFragment f$0;

    public /* synthetic */ ExitDlgFragment$$ExternalSyntheticLambda0(DialogFragment dialogFragment, int i) {
        this.$r8$classId = i;
        this.f$0 = dialogFragment;
    }

    public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        switch (this.$r8$classId) {
            case 0:
                return ((ExitDlgFragment) this.f$0).lambda$onCreateView$0(dialogInterface, i, keyEvent);
            case 1:
                return ((AddPlaylistDlgFragment) this.f$0).lambda$onCreateView$0(dialogInterface, i, keyEvent);
            case 2:
                return ((EpisodeDlgFragment) this.f$0).lambda$onCreateView$1(dialogInterface, i, keyEvent);
            case 3:
                return ((NoConnectionDlgFragment) this.f$0).lambda$onCreateView$0(dialogInterface, i, keyEvent);
            case 4:
                return ((ParentControlDlgFragment) this.f$0).lambda$onCreateView$0(dialogInterface, i, keyEvent);
            default:
                return ((SelectColorDlgFragment) this.f$0).lambda$onCreateView$1(dialogInterface, i, keyEvent);
        }
    }
}
