package com.example.littleync;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Locale;

/**
 * Cendana Forest Activity page where the user can idly chop down trees to gain wood resource
 */
public class CendanaForestActivity extends ActionActivity {

    public CendanaForestActivity() {
        super("CendanaForestActivity");
    }

    @Override
    protected void settingContentView() {
        setContentView(R.layout.cendana_forest_page);
    }

    @Override
    protected void createSpinners() {}

    @Override
    protected void action() {
        user.chopWood();
        updateGainText();
        Animation animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        gainDisplay.startAnimation(animFadeOut);
    }

    protected void updateGainText() {
        int gain = user.getWoodchoppingGearLevel();

        String woodText = String.format(Locale.getDefault(), "+%s Wood", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", gain);
        SpannableStringBuilder woodSpan = new SpannableStringBuilder(woodText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int woodColor = Color.parseColor("#8FFF7C");
        int expColor = Color.parseColor("#FF9999");

        ForegroundColorSpan woodToColor = new ForegroundColorSpan(woodColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        woodSpan.setSpan(woodToColor, 0, woodText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        woodSpan.append(" and ");
        woodSpan.append(expSpan);

        gainDisplay.setText(woodSpan);
    }

}

