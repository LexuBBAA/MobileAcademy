package com.lexu.mobileacademy2.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;

/**
 * Created by lexu on 28.03.2018.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String text, String title) {
        int index = text.indexOf('\n');
        String formattedText = "<p><b>" + text.substring(0, index + 1) + "</b></p>";
        formattedText += "<p><i>" + text.substring(index + 1) + "</i></p>";
        formattedText += "<p><i>- <u>" + title + "</u></i><p>";

        Spanned resultString = Html.fromHtml(formattedText);

        this.setText(resultString);
    }
}
