package com.example.lab04;

import android.content.Context;

public class Constants {
    public static Context CURRENT_CONTEXT;
    public static float PITCH_THRESHOLD = (float)0.5;
    public static int MAX_LEVELS = 10;
    public static long STARTING_TIME = 15000;
    public static final String DATABASE_NAME = "UserScores.db";
    public static final int DATABASE_VERSION = 1;
    public static final String  TABLE_NAME = "Scores";
    public static final String  COLUMN_ID = "_id";
    public static final String  COLUMN_USERNAME = "_username";
    public static final String  COLUMN_NAME = "_name";
    public static final String  COLUMN_SCORE = "_score";
    public static final String  COLUMN_TIME_SPENT = "_timeSpent";



}
