package com.example.littleync;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface CendanaForestActivityInterface {
    void onDestroy();

    void readUser(Task<DocumentSnapshot> ds);

    void chopWood();
}
