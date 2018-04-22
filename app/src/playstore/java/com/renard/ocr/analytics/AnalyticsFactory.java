package com.renard.betty.analytics;

import android.content.Context;

public class AnalyticsFactory {

    public static Analytics createAnalytics(Context context) {
        return new AnalyticsWithGoogle(context);
    }
}
