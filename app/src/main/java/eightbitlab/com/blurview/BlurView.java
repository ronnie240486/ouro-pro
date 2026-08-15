package eightbitlab.com.blurview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class BlurView extends View {
    public BlurView(Context context) { super(context); }
    public BlurView(Context context, AttributeSet attrs) { super(context, attrs); }
    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public BlurView setupWith(ViewGroup root) { return this; }
    public BlurView setFrameClearDrawable(Drawable drawable) { return this; }
    public BlurView setBlurAlgorithm(RenderScriptBlur algorithm) { return this; }
    public BlurView setBlurRadius(float radius) { return this; }
    public BlurView setBlurAutoUpdate(boolean enabled) { return this; }
    public BlurView setOverlayColor(int color) { setBackgroundColor(color); return this; }
    public BlurView setHasFixedTransformationMatrix(boolean fixed) { return this; }
}
