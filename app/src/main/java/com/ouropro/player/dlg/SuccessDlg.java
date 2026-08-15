package com.ouropro.player.dlg;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.ouropro.player.R;

/* JADX INFO: loaded from: classes.dex */
public class SuccessDlg extends Dialog {
    public Button btn_cancel;
    public Button btn_ok;
    public OkButtonClickListener listener;

    public interface OkButtonClickListener {
        void onCancelClick();

        void onOkClick();
    }

    public SuccessDlg(@NonNull Context context, String str, String str2, String str3, final OkButtonClickListener okButtonClickListener) {
        super(context);
        this.listener = okButtonClickListener;
        final int i = 1;
        requestWindowFeature(1);
        setContentView(R.layout.dlg_success);
        final int i2 = 0;
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.btn_ok = (Button) findViewById(R.id.btn_ok);
        this.btn_cancel = (Button) findViewById(R.id.btn_cancel);
        this.btn_ok.setText(str2);
        this.btn_cancel.setText(str3);
        ((TextView) findViewById(R.id.txt_description)).setText(str);
        this.btn_ok.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlg.SuccessDlg$$ExternalSyntheticLambda0
            public final /* synthetic */ SuccessDlg f$0;

            {
                this.f$0 = SuccessDlg.this;
            }

            public final void onClick(View view) {
                switch (i2) {
                    case 0:
                        this.f$0.lambda$new$0(okButtonClickListener, view);
                        break;
                    default:
                        this.f$0.lambda$new$1(okButtonClickListener, view);
                        break;
                }
            }
        });
        this.btn_cancel.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlg.SuccessDlg$$ExternalSyntheticLambda0
            public final /* synthetic */ SuccessDlg f$0;

            {
                this.f$0 = SuccessDlg.this;
            }

            public final void onClick(View view) {
                switch (i) {
                    case 0:
                        this.f$0.lambda$new$0(okButtonClickListener, view);
                        break;
                    default:
                        this.f$0.lambda$new$1(okButtonClickListener, view);
                        break;
                }
            }
        });
        this.btn_ok.requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(OkButtonClickListener okButtonClickListener, View view) {
        dismiss();
        okButtonClickListener.onOkClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(OkButtonClickListener okButtonClickListener, View view) {
        dismiss();
        okButtonClickListener.onCancelClick();
    }
}
