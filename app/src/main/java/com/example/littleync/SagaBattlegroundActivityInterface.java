package com.example.littleync;

import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface SagaBattlegroundActivityInterface {
    void onDestroy();

    void onItemSelected(AdapterView<?> parent, View view, int position, long id);

    void onNothingSelected(AdapterView<?> parent);

    void readUser(Task<DocumentSnapshot> ds);

    void fight();
}
