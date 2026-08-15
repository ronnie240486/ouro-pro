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
import com.ouropro.player.adapter.HideCategoryRecyclerViewAdapter;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.WordModels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;

/* JADX INFO: loaded from: classes.dex */
public class HideCategoryDlgFragment extends DialogFragment implements View.OnClickListener {
    public HideCategoryRecyclerViewAdapter adapter;
    public Button btn_all;
    public Button btn_cancel;
    public Button btn_ok;
    public List<CategoryModel> categoryModels;
    public String[] category_ids;
    public String[] category_names;
    public boolean[] checkedItems;
    public Context context;
    public int id;
    public OnCategoryChanged listener;
    public RecyclerView recyclerGroups;
    public PreferenceHelper sharedPreferenceHelper;
    public TextView txt_header;
    public List<String> selectedIds = new ArrayList();
    public WordModels wordModels = new WordModels();

    public interface OnCategoryChanged {
        void CategoryChanged();
    }

    private void initView(View view) {
        this.txt_header = (TextView) view.findViewById(R.id.txt_header);
        this.recyclerGroups = (RecyclerView) view.findViewById(R.id.recyclerGroups);
        this.btn_ok = (Button) view.findViewById(R.id.btn_ok);
        this.btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        this.btn_all = (Button) view.findViewById(R.id.btn_all);
        this.btn_ok.setOnClickListener(this);
        this.btn_cancel.setOnClickListener(this);
        this.btn_all.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreateView$0(Integer num, Boolean bool) {
        if (bool.booleanValue()) {
            if (this.selectedIds.contains(this.category_ids[num.intValue()])) {
                return null;
            }
            this.selectedIds.add(this.category_ids[num.intValue()]);
            return null;
        }
        if (!this.selectedIds.contains(this.category_ids[num.intValue()])) {
            return null;
        }
        this.selectedIds.removeAll(Collections.singletonList(this.category_ids[num.intValue()]));
        return null;
    }

    public static HideCategoryDlgFragment newInstance(Context context, int i) {
        HideCategoryDlgFragment hideCategoryDlgFragment = new HideCategoryDlgFragment();
        hideCategoryDlgFragment.context = context;
        hideCategoryDlgFragment.id = i;
        return hideCategoryDlgFragment;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_all) {
            if (this.selectedIds.size() < this.category_ids.length) {
                this.adapter.allChecked(true);
                this.selectedIds.addAll(Arrays.asList(this.category_ids));
                return;
            } else {
                this.adapter.allChecked(false);
                this.selectedIds = new ArrayList();
                return;
            }
        }
        if (id == R.id.btn_cancel) {
            dismiss();
            return;
        }
        if (id != R.id.btn_ok) {
            return;
        }
        int i = this.id;
        if (i == 0) {
            Constants.getLiveGroupModels(this.selectedIds, this.context);
            this.sharedPreferenceHelper.setSharedPreferenceInvisibleLiveCategories(this.selectedIds);
        } else if (i == 1) {
            Constants.getVodGroupModels(this.selectedIds, this.context);
            this.sharedPreferenceHelper.setSharedPreferenceInvisibleVodCategories(this.selectedIds);
        } else if (i == 2) {
            Constants.getSeriesGroupModels(this.selectedIds, this.context);
            this.sharedPreferenceHelper.setSharedPreferenceInvisibleSeriesCategories(this.selectedIds);
        }
        this.listener.CategoryChanged();
        dismiss();
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, R.style.FullScreenDialogStyle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_hide_categories, viewGroup, false);
        initView(viewInflate);
        this.sharedPreferenceHelper = new PreferenceHelper(this.context);
        WordModels wordModel = GetSharedInfo.getWordModel(this.context);
        this.wordModels = wordModel;
        this.txt_header.setText(wordModel.getSelect_categories_you_want_to_hide());
        this.btn_ok.setText(this.wordModels.getOk());
        this.btn_all.setText(this.wordModels.getSelect_all());
        this.btn_cancel.setText(this.wordModels.getCancel());
        int i = this.id;
        if (i == 0) {
            this.categoryModels = this.sharedPreferenceHelper.getSharedLiveCategoryModels();
            this.selectedIds = this.sharedPreferenceHelper.getSharedPreferenceInvisibleLiveCategories();
        } else if (i == 1) {
            this.categoryModels = this.sharedPreferenceHelper.getSharedPreferenceVodCategory();
            this.selectedIds = this.sharedPreferenceHelper.getSharedPreferenceInvisibleVodCategories();
        } else if (i == 2) {
            this.categoryModels = this.sharedPreferenceHelper.getSharedPreferenceSeriesCategoryModel();
            this.selectedIds = this.sharedPreferenceHelper.getSharedPreferenceInvisibleSeriesCategories();
        }
        this.category_names = new String[this.categoryModels.size()];
        this.category_ids = new String[this.categoryModels.size()];
        this.checkedItems = new boolean[this.category_names.length];
        for (int i2 = 0; i2 < this.categoryModels.size(); i2++) {
            CategoryModel categoryModel = this.categoryModels.get(i2);
            this.category_names[i2] = categoryModel.getName();
            this.category_ids[i2] = categoryModel.getId();
            this.checkedItems[i2] = this.selectedIds.contains(categoryModel.getId());
        }
        this.adapter = new HideCategoryRecyclerViewAdapter(getContext(), this.category_names, this.checkedItems, new EpisodeDlgFragment$$ExternalSyntheticLambda1(this, 4));
        this.recyclerGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerGroups.setHasFixedSize(true);
        this.recyclerGroups.setAdapter(this.adapter);
        return viewInflate;
    }

    public void setOnCategoryChangedListener(OnCategoryChanged onCategoryChanged) {
        this.listener = onCategoryChanged;
    }
}
