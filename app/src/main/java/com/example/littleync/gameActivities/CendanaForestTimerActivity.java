package com.example.littleync.gameActivities;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.littleync.R;

import java.util.Locale;

/**
 * Cendana Forest Activity page where the user can idly chop down trees to gain wood resource
 */
public class CendanaForestTimerActivity extends ActionTimerActivity {

    /**
     * Sets the tag for the Log
     */
    public CendanaForestTimerActivity() {
        super("CendanaForestActivity");
    }

    /**
     * Sets the correct content view
     */
    @Override
    protected void settingContentView() {
        setContentView(R.layout.cendana_forest_page3);
    }

    /**
     * On this page, since it's the forest, we want to chop wood
     */
    @Override
    protected void action() {
        user.chopWood();
        updateGainText();
        // Instantiating animation to add a fade out effect to the text after a 5 second tick.
        Animation animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        gainDisplay.startAnimation(animFadeOut);
    }

    /**
     * Sets the toast-message-like display depending on the action
     */
    private void updateGainText() {
        int gain = user.getWoodchoppingGearLevel();

        String woodText = String.format(Locale.getDefault(), "+%s Wood", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", gain);

        SpannableStringBuilder woodSpan = new SpannableStringBuilder(woodText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int woodColor = Color.parseColor("#90BCA0");
        int expColor = Color.parseColor("#C89FFD");

        ForegroundColorSpan woodToColor = new ForegroundColorSpan(woodColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        woodSpan.setSpan(woodToColor, 0, woodText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        woodSpan.append(" and ");
        woodSpan.append(expSpan);

        gainDisplay.setText(woodSpan);
    }

}