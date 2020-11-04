package com.example.littleync;

import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface ArmoryActivityInterface {
    void onDestroy();

    void readUser(Task<DocumentSnapshot> ds);

    void setWoodUpgrade();

    void setFishUpgrade();

    void setCombatUpgrade();

    void refreshScreen();

    void upgradeWood(View view);

    void upgradeFish(View view);

    void upgradeCombat(View view);
}
