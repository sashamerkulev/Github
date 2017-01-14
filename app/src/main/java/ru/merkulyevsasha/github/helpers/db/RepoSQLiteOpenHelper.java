package ru.merkulyevsasha.github.helpers.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Author;
import ru.merkulyevsasha.github.models.Commit;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Owner;
import ru.merkulyevsasha.github.models.Repo;


public class RepoSQLiteOpenHelper extends SQLiteOpenHelper implements DbInterface {

    public final static String DATABASE_NAME = "repos.db";
    public final static int DATABASE_VERSION = 1;

    public final static String REPOS_TABLE_NAME = "repos";
    public final static String ID = "id";
    public final static String LOGIN_NAME = "username";
    public final static String REPO_NAME = "name";
    public final static String REPO_FULLNAME = "full_name";
    public final static String DESCRIPTION = "description";
    public final static String FORKS_COUNT = "forks_count";
    public final static String WATCHERS_COUNT = "watchers_count";
    public final static String OWNER = "login";
    public final static String AVATAR_URL = "avatar_url";

    public final static String COMMITS_TABLE_NAME = "commits";
    public final static String REPO_ID = "repo_id";
    public final static String SHA = "sha";
    public final static String MESSAGE = "message";
    public final static String AUTHOR = "name";
    public final static String DATE = "date";


    public RepoSQLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_REPOS_TABLE = "CREATE TABLE " + REPOS_TABLE_NAME +
                "( " +
                ID + " INTEGER PRIMARY KEY, " +
                LOGIN_NAME + "  TEXT, " +
                REPO_NAME + " TEXT, " +
                REPO_FULLNAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                OWNER + " TEXT, " +
                AVATAR_URL + " TEXT, " +
                FORKS_COUNT + " INTEGER, " +
                WATCHERS_COUNT + " INTEGER " +
                ")";

        String CREATE_COMMITS_TABLE = "CREATE TABLE " + COMMITS_TABLE_NAME +
                "( " +
                REPO_ID + " INTEGER, " +
                SHA + "  TEXT, " +
                MESSAGE + " TEXT, " +
                AUTHOR + " TEXT, " +
                DATE + " TEXT " +
                ")";


        sqLiteDatabase.execSQL(CREATE_REPOS_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private ArrayList<Repo> readCursor(Cursor cursor){

        ArrayList<Repo> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Repo item = new Repo();
                item.setFullName(cursor.getString(cursor.getColumnIndex(REPO_FULLNAME)));
                item.setName(cursor.getString(cursor.getColumnIndex(REPO_NAME)));
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
            String select = "SELECT * FROM "+REPOS_TABLE_NAME
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
            String select = "SELECT * FROM "+REPOS_TABLE_NAME
                    + " WHERE "+LOGIN_NAME+"=@login and "+ REPO_FULLNAME +" like @search";
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
                values.put(REPO_FULLNAME, repo.getFullName());
                values.put(REPO_NAME, repo.getName());
                values.put(DESCRIPTION, repo.getDescription());
                values.put(FORKS_COUNT, repo.getForksCount());
                values.put(WATCHERS_COUNT, repo.getWatchersCount());
                values.put(OWNER, repo.getOwner().getLogin());
                values.put(AVATAR_URL, repo.getOwner().getAvatarUrl());
                db.insert(REPOS_TABLE_NAME, null, values);
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
            db.delete(REPOS_TABLE_NAME, LOGIN_NAME + " = ?", new String[]{login});
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void cleanCommits(int repoId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(COMMITS_TABLE_NAME, REPO_ID + " = ?", new String[]{String.valueOf(repoId)});
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void saveCommits(int repoId, ArrayList<CommitInfo> commits) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (CommitInfo commit : commits) {
                ContentValues values = new ContentValues();
                values.put(REPO_ID, repoId);
                values.put(SHA, commit.getSha());
                values.put(MESSAGE, commit.getCommit().getMessage());
                values.put(AUTHOR, commit.getCommit().getAuthor().getName());
                values.put(DATE, commit.getCommit().getAuthor().getDate());
                db.insert(COMMITS_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private ArrayList<CommitInfo> readCommitCursor(Cursor cursor){

        ArrayList<CommitInfo> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CommitInfo item = new CommitInfo();
                item.setSha(cursor.getString(cursor.getColumnIndex(SHA)));

                Commit commit = new Commit();
                commit.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));

                Author author = new Author();
                author.setName(cursor.getString(cursor.getColumnIndex(AUTHOR)));
                author.setDate(cursor.getString(cursor.getColumnIndex(DATE)));

                commit.setAuthor(author);

                item.setCommit(commit);

                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    @Override
    public ArrayList<CommitInfo> getCommits(int repoId) {
        SQLiteDatabase db = getReadableDatabase();
        try{
            String select = "SELECT * FROM "+COMMITS_TABLE_NAME
                    +" WHERE "+REPO_ID+"=@repoId";
            Cursor cursor = db.rawQuery(select, new String[]{String.valueOf(repoId)});
            return readCommitCursor(cursor);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
