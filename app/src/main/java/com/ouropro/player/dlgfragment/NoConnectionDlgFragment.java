package com.ouropro.player.dlgfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.ouropro.player.R;
import com.ouropro.player.activities.SearchActivity$$ExternalSyntheticLambda0;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.utils.TestPlaylistClient;

/* JADX INFO: loaded from: classes.dex */
public class NoConnectionDlgFragment extends DialogFragment {
    public Button btn_retry;
    // Fallback pra cliente já cadastrado cuja lista principal parou de
    // funcionar -- essa tela (diferente da DescriptionDlgFragment) só
    // aparece depois que o MAC já passou pela checagem de cadastro/validade,
    // então aqui o botão TESTE pode ficar sempre visível sem risco de dar
    // teste de graça pra quem nunca pagou.
    public Button btn_test;
    public Context context;
    public OnRetryClickListener listener;
    public TextView txt_check_network;
    public TextView txt_no_connection;
    public WordModels wordModels = new WordModels();
    public String description = "";

    public interface OnRetryClickListener {
        void onRetryClick();
    }

    private void initView(View view) {
        this.txt_no_connection = (TextView) view.findViewById(R.id.txt_no_connection);
        this.txt_check_network = (TextView) view.findViewById(R.id.txt_check_network);
        this.txt_no_connection.setText(this.wordModels.getNo_connection());
        this.txt_check_network.setText(this.description);
        Button button = (Button) view.findViewById(R.id.btn_retry);
        this.btn_retry = button;
        button.setText(this.wordModels.getRetry());
        this.btn_retry.setOnClickListener(new SearchActivity$$ExternalSyntheticLambda0(this, 11));
        this.btn_retry.requestFocus();

        this.btn_test = (Button) view.findViewById(R.id.btn_test);
        if (this.btn_test != null) {
            this.btn_test.setOnClickListener((View v) -> {
                android.app.Activity hostActivity = getActivity();
                if (hostActivity == null || hostActivity.isFinishing()) return;
                String mac = new PreferenceHelper(hostActivity).getSharedPreferenceMacAddress();
                TestPlaylistClient.showTestLeadDialog(hostActivity, mac, () -> {
                    dismiss();
                    TestPlaylistClient.restartApp(hostActivity);
                });
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initView$1(View view) {
        dismiss();
        this.listener.onRetryClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreateView$0(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 4) {
            return false;
        }
        dismiss();
        return true;
    }

    public static NoConnectionDlgFragment newInstance(Context context, String str) {
        NoConnectionDlgFragment noConnectionDlgFragment = new NoConnectionDlgFragment();
        noConnectionDlgFragment.context = context;
        noConnectionDlgFragment.description = str;
        return noConnectionDlgFragment;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.FullScreenDialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_no_connection, viewGroup, false);
        this.wordModels = GetSharedInfo.getWordModel(this.context);
        initView(viewInflate);
        getDialog().setOnKeyListener(new ExitDlgFragment$$ExternalSyntheticLambda0(this, 3));
        return viewInflate;
    }

    public void setOnRetryClickListener(OnRetryClickListener onRetryClickListener) {
        this.listener = onRetryClickListener;
    }
}
