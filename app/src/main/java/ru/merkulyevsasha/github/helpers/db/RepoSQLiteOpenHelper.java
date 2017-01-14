package ru.merkulyevsasha.github.helpers.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Owner;
import ru.merkulyevsasha.github.models.Repo;


public class RepoSQLiteOpenHelper extends SQLiteOpenHelper implements DbInterface {

    public final static String DATABASE_NAME = "repos.db";
    public final static int DATABASE_VERSION = 1;

    public final static String TABLE_NAME = "repos";
    public final static String ID = "id";
    public final static String LOGIN_NAME = "username";
    public final static String REPO_NAME = "full_name";
    public final static String DESCRIPTION = "description";
    public final static String FORKS_COUNT = "forks_count";
    public final static String WATCHERS_COUNT = "watchers_count";
    public final static String OWNER = "login";
    public final static String AVATAR_URL = "avatar_url";

    public RepoSQLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_REPOS_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " +
                ID + " INTEGER PRIMARY KEY, " +
                LOGIN_NAME + "  TEXT, " +
                REPO_NAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                OWNER + " TEXT, " +
                AVATAR_URL + " TEXT, " +
                FORKS_COUNT + " INTEGER, " +
                WATCHERS_COUNT + " INTEGER " +
                ")";

        sqLiteDatabase.execSQL(CREATE_REPOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private ArrayList<Repo> readCursor(Cursor cursor){

        ArrayList<Repo> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Repo item = new Repo();
                item.setFullName(cursor.getString(cursor.getColumnIndex(REPO_NAME)));
                item.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                item.setForksCount(cursor.getInt(cursor.getColumnIndex(FORKS_COUNT)));
                item.setWatchersCount(cursor.getInt(cursor.getColumnIndex(WATCHERS_COUNT)));

                Owner owner = new Owner();
                owner.setLogin(cursor.getString(cursor.getColumnIndex(OWNER)));
                owner.setAvatarUrl(cursor.getString(cursor.getColumnIndex(AVATAR_URL)));

                item.setOwner(owner);
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    @Override
    public ArrayList<Repo> getRepos(String login) {
        SQLiteDatabase db = getReadableDatabase();
        try{
            String select = "SELECT * FROM "+RepoSQLiteOpenHelper.TABLE_NAME
                    +" WHERE "+LOGIN_NAME+"=@login";
            Cursor cursor = db.rawQuery(select, new String[]{login});
            return readCursor(cursor);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Repo> searchRepos(String login, String searchText) {
        SQLiteDatabase db = getReadableDatabase();
        try{
            String select = "SELECT * FROM "+RepoSQLiteOpenHelper.TABLE_NAME
                    + " WHERE "+LOGIN_NAME+"=@login and "+REPO_NAME+" like @search";
            Cursor cursor = db.rawQuery(select, new String[]{login, "%"+searchText+"%"});
            return readCursor(cursor);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveRepos(String login, ArrayList<Repo> repos) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (Repo repo : repos) {
                ContentValues values = new ContentValues();
                values.put(ID, repo.getId());
                values.put(LOGIN_NAME, login);
                values.put(REPO_NAME, repo.getFullName());
                values.put(DESCRIPTION, repo.getDescription());
                values.put(FORKS_COUNT, repo.getForksCount());
                values.put(WATCHERS_COUNT, repo.getWatchersCount());
                values.put(OWNER, repo.getOwner().getLogin());
                values.put(AVATAR_URL, repo.getOwner().getAvatarUrl());
                db.insert(RepoSQLiteOpenHelper.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void cleanRepos(String login) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(RepoSQLiteOpenHelper.TABLE_NAME, LOGIN_NAME + " = ?", new String[]{login});
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
