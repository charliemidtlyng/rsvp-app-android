package no.charlie.rsvpapp.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontResolver {

    public static Typeface getHeaderFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/FedraSansAltPro-Demi.otf");
    }

    public static Typeface getStandardFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/FedraSansAltPro-Light.otf");
    }
}
