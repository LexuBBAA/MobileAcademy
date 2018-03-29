package com.lexu.mobileacademy2.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by lexu on 28.03.2018.
 */

public class CustomListItemTextView extends CustomTextView {

    public CustomListItemTextView(Context context) {
        super(context);
        this.setMaxLines(5);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    public CustomListItemTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setMaxLines(5);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    public CustomListItemTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMaxLines(5);
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void setText(String text, String title) {
        int index = text.indexOf('\n');
        String subs = index >= 50 ? (text.length() > 50 ? text.substring(0, 50) + "...": text) : text.substring(0, index);
        String formattedText = "<p><b>" + subs + "</b></p>";
        formattedText += "<p><i>" + text.substring(index + 1) + "</i></p>";
        formattedText += "<p><i>- <u>" + title + "</u></i></p>";

        Spanned resultString = Html.fromHtml(formattedText);

        this.setText(resultString);
    }
}
