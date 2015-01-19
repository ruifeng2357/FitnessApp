/* 健康*/
package com.damy.jiankang;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import com.damy.Utils.ResolutionSet;

/**
 * Created with IntelliJ IDEA.
 * User: Jun
 * Date: 8/28/14
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyActivity extends Activity {

    boolean m_bInitialized = false;

    public void initActivity (final int i_nRootLayoutID) {
        final View rlayout = findViewById(i_nRootLayoutID);
        rlayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (m_bInitialized == false)
                        {
                            Rect r = new Rect();
                            rlayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(i_nRootLayoutID));
                            m_bInitialized = true;
                        }
                    }
                }
        );
    }
}
