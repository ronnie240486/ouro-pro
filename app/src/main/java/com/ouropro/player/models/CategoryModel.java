package com.ouropro.player.models;

import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class CategoryModel implements Serializable {

    @SerializedName("category_id")
    private String id;

    @SerializedName("category_name")
    private String name;

    public CategoryModel(String str, String str2) {
        this.id = str;
        this.name = str2;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        String str = this.name;
        return (str == null || !str.contains("!@#%")) ? this.name : this.name.split("!@#%")[1];
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String toString() {
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("CategoryModel{category_id='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.id, '\'', ", category_name='");
        sbM.append(this.name);
        sbM.append('\'');
        sbM.append('}');
        return sbM.toString();
    }
}
