package com.videumcorp.popularmoviesstage2.Utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class ScreenMeasures {

    public static int screenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int screenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
