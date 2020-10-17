package com.example.littleync.model;

import java.util.HashMap;
import java.util.Map;

public class Monsters {
    // { Monster Name : [Hitpoints, Gold Yield, Exp Yield] }
    private Map<String, int[]> monsters = new HashMap<String, int[]>();

    public Monsters() {
        monsters.put("Prof. Sergey", new int[] {10, 1, 1});
        monsters.put("Prof. Stamps", new int[] {20, 2, 2});
        monsters.put("Prof. Danvy", new int[] {40, 4, 4});

        monsters.put("Prof. Wertz", new int[] {80, 8, 8});
        monsters.put("Prof. Cheung", new int[] {160, 16, 16});
        monsters.put("Prof. Field", new int[] {320, 32, 32});

        monsters.put("Prof. Liu", new int[] {640, 64, 64});
        monsters.put("Prof. De Iorio", new int[] {1280, 128, 128});
        monsters.put("Prof. Tolwinski", new int[] {2000, 200, 200});

        monsters.put("Prof. Comaroff", new int[] {3000, 300, 300});
        monsters.put("Prof. Hobor", new int[] {4000, 400, 400});
        monsters.put("Prof. Bodin", new int[] {1000000, 10000, 10000});
    }

    public int getMonsterHitpoints(String monsterName) {
        return monsters.get(monsterName)[0];
    }

    public int getGoldYield(String monsterName) {
        return monsters.get(monsterName)[1];
    }

    public int getExpYield(String monsterName) {
        return monsters.get(monsterName)[2];
    }

}
