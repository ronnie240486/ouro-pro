package com.ouropro.player.dlgfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.adapter.LanguageRecyclerViewAdapter;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.models.LanguageModel;
import com.ouropro.player.models.WordModels;
import java.util.List;
import kotlin.Unit;

/* JADX INFO: loaded from: classes.dex */
public class LanguageDlgFragment extends DialogFragment {
    public LanguageRecyclerViewAdapter adapter;
    public Button btn_cancel;
    public Button btn_ok;
    public Context context;
    public List<LanguageModel> formatStrings;
    public ItemPositionListener listener;
    public RecyclerView recyclerTimes;
    public TextView txt_header;
    public WordModels wordModels = new WordModels();
    public int selected_position = 0;

    public interface ItemPositionListener {
        void onItemPosition(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreateView$0(Integer num, Boolean bool) {
        if (!bool.booleanValue()) {
            return null;
        }
        this.selected_position = num.intValue();
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(View view) {
        this.listener.onItemPosition(this.selected_position);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$2(View view) {
        dismiss();
    }

    public static LanguageDlgFragment newInstance(Context context, List<LanguageModel> list, int i, ItemPositionListener itemPositionListener) {
        LanguageDlgFragment languageDlgFragment = new LanguageDlgFragment();
        languageDlgFragment.context = context;
        languageDlgFragment.formatStrings = list;
        languageDlgFragment.selected_position = i;
        languageDlgFragment.listener = itemPositionListener;
        return languageDlgFragment;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.FullScreenDialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        final int i = 0;
        View viewInflate = layoutInflater.inflate(R.layout.fragment_language, viewGroup, false);
        this.wordModels = GetSharedInfo.getWordModel(this.context);
        this.btn_ok = (Button) viewInflate.findViewById(R.id.btn_ok);
        this.btn_cancel = (Button) viewInflate.findViewById(R.id.btn_cancel);
        this.txt_header = (TextView) viewInflate.findViewById(R.id.txt_header);
        this.recyclerTimes = (RecyclerView) viewInflate.findViewById(R.id.recyclerGroups);
        this.btn_ok.setText(this.wordModels.getOk());
        this.btn_cancel.setText(this.wordModels.getCancel());
        this.txt_header.setText(this.wordModels.getChange_language());
        this.adapter = new LanguageRecyclerViewAdapter(getContext(), this.formatStrings, this.selected_position, new EpisodeDlgFragment$$ExternalSyntheticLambda1(this, 5));
        this.recyclerTimes.setLayoutManager(new LinearLayoutManager(getContext()));
        final int i2 = 1;
        this.recyclerTimes.setHasFixedSize(true);
        this.recyclerTimes.setAdapter(this.adapter);
        this.recyclerTimes.scrollToPosition(this.selected_position);
        this.recyclerTimes.requestFocus();
        this.btn_ok.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlgfragment.LanguageDlgFragment$$ExternalSyntheticLambda0
            public final /* synthetic */ LanguageDlgFragment f$0;

            {
                this.f$0 = LanguageDlgFragment.this;
            }

            public final void onClick(View view) {
                switch (i) {
                    case 0:
                        this.f$0.lambda$onCreateView$1(view);
                        break;
                    default:
                        this.f$0.lambda$onCreateView$2(view);
                        break;
                }
            }
        });
        this.btn_cancel.setOnClickListener(new View.OnClickListener() { // from class: com.ouropro.player.dlgfragment.LanguageDlgFragment$$ExternalSyntheticLambda0
            public final /* synthetic */ LanguageDlgFragment f$0;

            {
                this.f$0 = LanguageDlgFragment.this;
            }

            public final void onClick(View view) {
                switch (i2) {
                    case 0:
                        this.f$0.lambda$onCreateView$1(view);
                        break;
                    default:
                        this.f$0.lambda$onCreateView$2(view);
                        break;
                }
            }
        });
        return viewInflate;
    }
}
