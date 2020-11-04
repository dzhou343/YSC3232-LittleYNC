package com.example.littleync;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface EcopondActivityInterface {
    void onDestroy();

    void readUser(Task<DocumentSnapshot> ds);

    void fishFish();
}
