package ru.merkulyevsasha.github.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class RepoSQLiteOpenHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "repos.db";
    public final static int DATABASE_VERSION = 1;

    public final static String TABLE_NAME = "repos";
    public final static String ID = "id";
    public final static String LOGIN_NAME = "username";
    public final static String REPO_NAME = "full_name";

    public RepoSQLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_REPOS_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " +
                ID + " INTEGER PRIMARY KEY, " +
                LOGIN_NAME + "  TEXT, " +
                REPO_NAME + " TEXT " +
                ")";

        sqLiteDatabase.execSQL(CREATE_REPOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
