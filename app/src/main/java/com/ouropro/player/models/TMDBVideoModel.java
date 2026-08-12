package com.ouropro.player.models;

import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class TMDBVideoModel implements Serializable {
    public String id;
    public String key;
    public String name;
    public String published_at;
    public String site;
    public String type;

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getPublished_at() {
        return this.published_at;
    }

    public String getSite() {
        return this.site;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("TMDBVideoModel{name='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.name, '\'', ", key='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.key, '\'', ", site='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.site, '\'', ", type='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.type, '\'', ", published_at='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.published_at, '\'', ", id='");
        sbM.append(this.id);
        sbM.append('\'');
        sbM.append('}');
        return sbM.toString();
    }
}
