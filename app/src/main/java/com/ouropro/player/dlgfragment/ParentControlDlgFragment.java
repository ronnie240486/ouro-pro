package com.ouropro.player.dlgfragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.ouropro.player.R;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.WordModels;

/* JADX INFO: loaded from: classes.dex */
public class ParentControlDlgFragment extends DialogFragment {
    public Button btn_cancel;
    public Button btn_ok;
    public EditText et_confirm_password;
    public EditText et_new_password;
    public EditText et_password;
    public PreferenceHelper sharedPreferenceHelper;
    public TextView str_confirm_password;
    public TextView str_new_password;
    public TextView str_password;
    public TextView txt_name;
    public String pin_code = "";
    public boolean pinConfigured = false;
    public WordModels wordModels = new WordModels();

    private void initView(View view) {
        this.txt_name = (TextView) view.findViewById(R.id.txt_name);
        this.str_password = (TextView) view.findViewById(R.id.str_password);
        this.str_new_password = (TextView) view.findViewById(R.id.str_new_password);
        this.str_confirm_password = (TextView) view.findViewById(R.id.str_confirm_password);
        this.et_password = (EditText) view.findViewById(R.id.et_password);
        this.et_new_password = (EditText) view.findViewById(R.id.et_new_password);
        this.et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
        this.btn_ok = (Button) view.findViewById(R.id.btn_ok);
        this.btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final int i = 0;
        this.btn_ok.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlgfragment.ParentControlDlgFragment$$ExternalSyntheticLambda0
            public final /* synthetic */ ParentControlDlgFragment f$0;

            {
                this.f$0 = ParentControlDlgFragment.this;
            }

            public final void onClick(View view2) {
                switch (i) {
                    case 0:
                        this.f$0.lambda$initView$1(view2);
                        break;
                    default:
                        this.f$0.lambda$initView$2(view2);
                        break;
                }
            }
        });
        final int i2 = 1;
        this.btn_cancel.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlgfragment.ParentControlDlgFragment$$ExternalSyntheticLambda0
            public final /* synthetic */ ParentControlDlgFragment f$0;

            {
                this.f$0 = ParentControlDlgFragment.this;
            }

            public final void onClick(View view2) {
                switch (i2) {
                    case 0:
                        this.f$0.lambda$initView$1(view2);
                        break;
                    default:
                        this.f$0.lambda$initView$2(view2);
                        break;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initView$1(View view) {
        if (this.pinConfigured && this.et_password.getText().toString().isEmpty()) {
            this.et_password.setError("Password can't be empty!");
            return;
        }
        if (this.et_new_password.getText().toString().isEmpty()) {
            this.et_new_password.setError("New password can't be empty!");
            return;
        }
        if (this.et_new_password.getText().toString().length() != 4) {
            this.et_new_password.setError("New password length need to be 4!");
            return;
        }
        if (this.et_confirm_password.getText().toString().isEmpty()) {
            this.et_confirm_password.setError("Confirm password can't be empty!");
            return;
        }
        if (this.pinConfigured && !this.pin_code.equalsIgnoreCase(this.et_password.getText().toString())) {
            this.et_password.setError("Password is incorrect!");
        } else if (this.et_new_password.getText().toString().equalsIgnoreCase(this.et_confirm_password.getText().toString())) {
            updatePinCode();
        } else {
            this.et_confirm_password.setError("Confirm password is not matched!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initView$2(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreateView$0(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 4) {
            return false;
        }
        dismiss();
        return true;
    }

    private void updatePinCode() {
        String string = this.et_new_password.getText().toString();
        this.pin_code = string;
        this.sharedPreferenceHelper.setSharedPreferenceParentPassword(string);
        Toast.makeText(getContext(), "Parent password is changed successfully.", 0).show();
        this.et_password.setText("");
        this.et_new_password.setText("");
        this.et_confirm_password.setText("");
        dismiss();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.FullScreenDialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_parent, viewGroup, false);
        initView(viewInflate);
        PreferenceHelper preferenceHelper = new PreferenceHelper(getContext());
        this.sharedPreferenceHelper = preferenceHelper;
        preferenceHelper.getSharedPreferenceAppInfo();
        this.pin_code = this.sharedPreferenceHelper.getSharedPreferenceParentPassword();
        this.pinConfigured = this.sharedPreferenceHelper.isParentPasswordConfigured();
        if (!this.pinConfigured) {
            this.et_password.setVisibility(View.GONE);
            this.str_password.setVisibility(View.GONE);
        }
        WordModels wordModel = GetSharedInfo.getWordModel(getContext());
        this.wordModels = wordModel;
        this.txt_name.setText(wordModel.getParent_control());
        this.str_password.setText(this.wordModels.getPassword());
        this.str_new_password.setText(this.wordModels.getNet_pass());
        this.str_confirm_password.setText(this.wordModels.getConfirm_password());
        this.btn_ok.setText(this.wordModels.getOk());
        this.btn_cancel.setText(this.wordModels.getCancel());
        this.et_password.addTextChangedListener(new TextWatcher() { // from class: com.ouropro.player.dlgfragment.ParentControlDlgFragment.1
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 4 || editable.toString().equalsIgnoreCase(ParentControlDlgFragment.this.pin_code)) {
                    return;
                }
                ParentControlDlgFragment.this.et_password.setError("Password is incorrect!");
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        this.et_confirm_password.addTextChangedListener(new TextWatcher() { // from class: com.ouropro.player.dlgfragment.ParentControlDlgFragment.2
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 4 || editable.toString().equalsIgnoreCase(ParentControlDlgFragment.this.et_new_password.getText().toString())) {
                    return;
                }
                ParentControlDlgFragment.this.et_confirm_password.setError("Confirm password is not matched!");
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        getDialog().setOnKeyListener(new ExitDlgFragment$$ExternalSyntheticLambda0(this, 4));
        return viewInflate;
    }
}
