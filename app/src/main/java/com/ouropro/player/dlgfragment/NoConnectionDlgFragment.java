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
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.utils.TestPlaylistClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            // IMPORTANTE: usar classe anônima em vez de lambda "(v) -> {}"
            // aqui de propósito. Esse arquivo veio de um app descompilado, e
            // o javac nomeia cada lambda de um método automaticamente
            // (lambda$initView$0, lambda$initView$1, ...); esse arquivo já
            // tem um método de verdade chamado "lambda$initView$1" (sobrou
            // da descompilação do app original, usado por
            // SearchActivity$$ExternalSyntheticLambda0 logo acima). Uma
            // lambda nova aqui dentro recebe esse mesmo nome automático e
            // trava o build com "conflicts with a compiler-synthesized
            // symbol". Classe anônima não tem esse problema.
            this.btn_test.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final android.app.Activity hostActivity = getActivity();
                    if (hostActivity == null || hostActivity.isFinishing()) return;
                    PreferenceHelper preferenceHelper = new PreferenceHelper(hostActivity);
                    String mac = preferenceHelper.getSharedPreferenceMacAddress();
                    // Pedido do usuário: sem essa checagem, um MAC já
                    // cadastrado mas bloqueado/vencido ficaria gerando
                    // teste de graça toda vez que a lista principal
                    // falhasse, sem limite nenhum. Cliente com assinatura
                    // ativa (liberado) pode usar esse botão quantas vezes
                    // precisar -- é só uma reserva pra quando a lista dele
                    // cai. Quem está bloqueado/vencido só vê o aviso pra
                    // falar com o revendedor.
                    if (isAccountLiberado(preferenceHelper)) {
                        TestPlaylistClient.showTestLeadDialog(hostActivity, mac, new Runnable() {
                            public void run() {
                                dismiss();
                                TestPlaylistClient.restartApp(hostActivity);
                            }
                        });
                    } else {
                        TestPlaylistClient.showAlreadyTestedDialog(hostActivity, mac);
                    }
                }
            });
        }
    }

    /**
     * true só quando o cache local mostra uma assinatura de verdade ativa
     * (is_trial == 0, ou seja, MAC conhecido pelo painel, e ainda dentro
     * da validade). MAC nunca cadastrado (is_trial == 1) não deveria nem
     * chegar nessa tela na prática -- essa checagem é só uma segurança a
     * mais.
     */
    private boolean isAccountLiberado(PreferenceHelper preferenceHelper) {
        try {
            AppInfoModel info = preferenceHelper.getSharedPreferenceAppInfo();
            if (info == null || info.getIs_trial() == 1) {
                return false;
            }
            long expireMillis = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(info.getExpiredDate()).getTime();
            return expireMillis - new Date().getTime() > 0;
        } catch (Exception e) {
            return false;
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
