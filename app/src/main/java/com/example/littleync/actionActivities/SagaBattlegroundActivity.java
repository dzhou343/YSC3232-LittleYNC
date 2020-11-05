package com.example.littleync.actionActivities;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.littleync.R;
import com.example.littleync.model.Monsters;

import java.util.Locale;

/**
 * Saga Battleground Activity page where the user can idly battle monsters to gain gold resource
 */
public class SagaBattlegroundActivity extends ActionActivity implements AdapterView.OnItemSelectedListener {

    // Monster-specific attributes
    private final Monsters MONSTERS = new Monsters();
    private String currentMonster;
    private int currentHP;
    private TextView healthDisplay;
    private ImageView monsterDisplay;

    /**
     * Sets the tag for the Log
     */
    public SagaBattlegroundActivity() {
        super("SagaBattlegroundActivity");
    }

    /**
     * Sets the correct content view
     */
    @Override
    protected void settingContentView() {
        setContentView(R.layout.battleground_page);
    }

    /**
     * Create the spinner to select and initiate the monster to battle
     */
    @Override
    protected void createSpinners() {
        // Initializing Spinner
        // This spinner refers to initializing the drop down menu for picking which enemy a user would like to fight
        Spinner enemySpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> enemyAdapter = new ArrayAdapter<>(
                SagaBattlegroundActivity.this,
                R.layout.battleground_spinner,
                getResources().getStringArray(R.array.monster_names));
        // Creates the list of data
        enemyAdapter.setDropDownViewResource(R.layout.battleground_spinner_dropdown);
        // Allows the spinner to show the data within the spinner.
        enemySpinner.setAdapter(enemyAdapter);
        enemySpinner.setOnItemSelectedListener(this);
        healthDisplay = findViewById(R.id.health_display);
        monsterDisplay = findViewById(R.id.monster_img);
    }

    /**
     * Updates the current monster being fought, and relevant TextViews and image
     *
     * @param parent   spinner
     * @param view     for Android
     * @param position for Android
     * @param id       for Android
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentMonster = parent.getItemAtPosition(position).toString();
        currentHP = MONSTERS.getMonsterHitpoints(currentMonster);
        String healthMaxFormatted = String.format(Locale.getDefault(),
                "HP: %s / %s", currentHP, MONSTERS.getMonsterHitpoints(currentMonster));
        healthDisplay.setText(healthMaxFormatted);
        changeMonsterImage();
    }

    /**
     * Sets the default for the spinner
     *
     * @param parent spinner
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * On this page, since it's the battleground, we want to deal damage to monsters
     */
    @Override
    protected void action() {
        // Deal damage to monster
        currentHP -= user.getCombatGearLevel();
        if (currentHP <= 0) {
            // If monster is dead, then increment gold and exp
            user.addGold(MONSTERS.getMonsterGoldYield(currentMonster));
            user.addExp(MONSTERS.getMonsterExpYield(currentMonster));
            // Reset monster HP to fight again
            currentHP = MONSTERS.getMonsterHitpoints(currentMonster);
            // Only play animation here
            updateGainText();
            Animation animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            gainDisplay.startAnimation(animFadeOut);
        }
        // Otherwise, always update the textView to reflect damage dealt
        String healthMaxFormatted = String.format(Locale.getDefault(),
                "HP: %s / %s", currentHP, MONSTERS.getMonsterHitpoints(currentMonster));
        healthDisplay.setText(healthMaxFormatted);
    }

    /**
     * Sets the toast-message-like display depending on the action
     */
    private void updateGainText() {
        int gain = MONSTERS.getMonsterGoldYield(currentMonster);
        int expGain = MONSTERS.getMonsterExpYield(currentMonster);

        String combatText = String.format(Locale.getDefault(), "+%s Gold", gain);
        String expText = String.format(Locale.getDefault(), "+%s Exp", expGain);

        SpannableStringBuilder combatSpan = new SpannableStringBuilder(combatText);
        SpannableStringBuilder expSpan = new SpannableStringBuilder(expText);

        int combatColor = Color.parseColor("#CCA533");
        int expColor = Color.parseColor("#FF9999");

        ForegroundColorSpan combatToColor = new ForegroundColorSpan(combatColor);
        ForegroundColorSpan expToColor = new ForegroundColorSpan(expColor);

        combatSpan.setSpan(combatToColor, 0, combatText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        expSpan.setSpan(expToColor, 0, expText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        combatSpan.append(" and ");
        combatSpan.append(expSpan);

        gainDisplay.setText(combatSpan);
    }

    /**
     * Changes the monster image
     */
    private void changeMonsterImage() {
        switch (currentMonster) {
            case "Prof. Bodin":
                monsterDisplay.setImageResource(R.drawable.battle_bruno);
                break;
            case "Prof. Wertz":
                monsterDisplay.setImageResource(R.drawable.battle_wertz);
                break;
            case "Prof. Cheung":
                monsterDisplay.setImageResource(R.drawable.battle_cheung);
                break;
            case "Prof. Comaroff":
                monsterDisplay.setImageResource(R.drawable.battle_comaroff);
                break;
            case "Prof. Danvy":
                monsterDisplay.setImageResource(R.drawable.battle_danvy);
                break;
            case "Prof. Field":
                monsterDisplay.setImageResource(R.drawable.battle_field);
                break;
            case "Prof. Hobor":
                monsterDisplay.setImageResource(R.drawable.battle_hobor);
                break;
            case "Prof. De Iorio":
                monsterDisplay.setImageResource(R.drawable.battle_iorio);
                break;
            case "Prof. Sergey":
                monsterDisplay.setImageResource(R.drawable.battle_sergey);
                break;
            case "Prof. Stamps":
                monsterDisplay.setImageResource(R.drawable.battle_stamps);
                break;
            case "Prof. Tolwinski":
                monsterDisplay.setImageResource(R.drawable.battle_tolwinski);
                break;
            case "Prof. Liu":
                monsterDisplay.setImageResource(R.drawable.battle_liu);
                break;
        }
    }

}