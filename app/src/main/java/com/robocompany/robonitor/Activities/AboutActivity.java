package com.robocompany.robonitor.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.robocompany.robonitor.BuildConfig;
import com.robocompany.robonitor.R;

public class AboutActivity extends AppCompatActivity {

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv_body_about = findViewById(R.id.tv_body_about);
        tv_body_about.setText(Html.fromHtml(getString(R.string.about_body)));

        //setTextViewHTML(tv_body_about,getString(R.string.about_body));

        tv_body_about.setLinksClickable(true);
        tv_body_about.setMovementMethod(LinkMovementMethod.getInstance());

        try {


            String versionName = BuildConfig.VERSION_NAME;

            ((TextView) findViewById(R.id.tv_version)).setText("v.: " + versionName);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
