package ru.merkulyevsasha.github.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.merkulyevsasha.github.helpers.RepoSQLiteOpenHelper.ID;
import static ru.merkulyevsasha.github.helpers.RepoSQLiteOpenHelper.LOGIN_NAME;
import static ru.merkulyevsasha.github.helpers.RepoSQLiteOpenHelper.REPO_NAME;

public class DbHelper implements DatabaseInterface {

    private final BriteDatabase mDb;

    public DbHelper(Context context, Scheduler scheduler){

        SqlBrite sqlBrite = new SqlBrite.Builder().build();

        RepoSQLiteOpenHelper openHelper = new RepoSQLiteOpenHelper(context);

        mDb = sqlBrite.wrapDatabaseHelper(openHelper, scheduler);
    }

    private ArrayList<Repo> readCursor(Cursor cursor){

        ArrayList<Repo> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Repo item = new Repo();
                item.setFullName(cursor.getString(cursor.getColumnIndex(REPO_NAME)));
                item.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    @Override
    public Observable<ArrayList<Repo>> getRepos(String login){
        return mDb.createQuery(RepoSQLiteOpenHelper.TABLE_NAME, "SELECT * FROM "+RepoSQLiteOpenHelper.TABLE_NAME
                +" WHERE "+LOGIN_NAME+"=@login", new String[]{login})
        .flatMap(new Func1<SqlBrite.Query, Observable<ArrayList<Repo>>>() {
            @Override
            public Observable<ArrayList<Repo>> call(SqlBrite.Query query) {
                ArrayList<Repo> data = readCursor(query.run());
                return Observable.just(data);
            }
        });
    }

    @Override
    public Observable<ArrayList<Repo>> searchRepos(String login, String searchText){
        return mDb.createQuery(RepoSQLiteOpenHelper.TABLE_NAME, "SELECT * FROM "+RepoSQLiteOpenHelper.TABLE_NAME
                +" WHERE "+LOGIN_NAME+"=@login and "+REPO_NAME+" like @search", new String[]{login, "%" + searchText + "%"})
                .flatMap(new Func1<SqlBrite.Query, Observable<ArrayList<Repo>>>() {
                    @Override
                    public Observable<ArrayList<Repo>> call(SqlBrite.Query query) {
                        ArrayList<Repo> data = readCursor(query.run());
                        return Observable.just(data);
                    }
                });
    }

    @Override
    public void saveRepos(String login, ArrayList<Repo> repos) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            for (Repo repo : repos) {
                ContentValues values = new ContentValues();
                values.put(ID, repo.getId());
                values.put(LOGIN_NAME, login);
                values.put(REPO_NAME, repo.getFullName());
                mDb.insert(RepoSQLiteOpenHelper.TABLE_NAME, values);
            }
            transaction.markSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void cleanRepos(){
        mDb.delete(RepoSQLiteOpenHelper.TABLE_NAME, null);
    }

}
