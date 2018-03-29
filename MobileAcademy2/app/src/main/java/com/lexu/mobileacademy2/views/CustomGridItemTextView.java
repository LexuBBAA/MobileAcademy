package com.lexu.mobileacademy2.views;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by lexu on 29.03.2018.
 */

public class CustomGridItemTextView extends CustomTextView {
    public CustomGridItemTextView(Context context) {
        super(context);
        this.setMaxLines(3);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    public CustomGridItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setMaxLines(5);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    public CustomGridItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMaxLines(5);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void setText(String text, String title) {
        String formattedText = "<p><b><i> - " + title + "</i></b></p>";

        Spanned resultString = Html.fromHtml(formattedText);

        this.setText(resultString);
    }
}
