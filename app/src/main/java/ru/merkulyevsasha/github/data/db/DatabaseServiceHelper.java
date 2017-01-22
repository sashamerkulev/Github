package ru.merkulyevsasha.github.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Author;
import ru.merkulyevsasha.github.models.Commit;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Owner;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.AUTHOR;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.AVATAR_URL;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.COMMITS_TABLE_NAME;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.DATE;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.DESCRIPTION;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.FORKS_COUNT;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.ID;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.LOGIN_NAME;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.MESSAGE;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.OWNER;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.REPO_FULLNAME;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.REPO_ID;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.REPO_NAME;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.SHA;
import static ru.merkulyevsasha.github.data.db.RepoSQLiteOpenHelper.WATCHERS_COUNT;

public class DatabaseServiceHelper implements DatabaseServiceInterface {

    private final BriteDatabase mDb;

    public DatabaseServiceHelper(Context context, Scheduler scheduler){

        SqlBrite sqlBrite = new SqlBrite.Builder().build();

        RepoSQLiteOpenHelper openHelper = new RepoSQLiteOpenHelper(context);

        mDb = sqlBrite.wrapDatabaseHelper(openHelper, scheduler);
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
    public Observable<ArrayList<Repo>> getRepos(String login){
        return mDb.createQuery(RepoSQLiteOpenHelper.REPOS_TABLE_NAME, "SELECT * FROM "+RepoSQLiteOpenHelper.REPOS_TABLE_NAME
                +" WHERE "+LOGIN_NAME+"=@login" + " ORDER BY "+REPO_FULLNAME,
                new String[]{login})
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
        return mDb.createQuery(RepoSQLiteOpenHelper.REPOS_TABLE_NAME, "SELECT * FROM "+RepoSQLiteOpenHelper.REPOS_TABLE_NAME
                +" WHERE "+LOGIN_NAME+"=@login and "+REPO_NAME+" like @search" + " ORDER BY "+REPO_FULLNAME,
                new String[]{login, "%" + searchText + "%"})
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
                values.put(REPO_FULLNAME, repo.getFullName());
                values.put(REPO_NAME, repo.getName());
                values.put(DESCRIPTION, repo.getDescription());
                values.put(FORKS_COUNT, repo.getForksCount());
                values.put(WATCHERS_COUNT, repo.getWatchersCount());
                values.put(OWNER, repo.getOwner().getLogin());
                values.put(AVATAR_URL, repo.getOwner().getAvatarUrl());
                mDb.insert(RepoSQLiteOpenHelper.REPOS_TABLE_NAME, values);
            }
            transaction.markSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void cleanRepos(String login){
        mDb.delete(RepoSQLiteOpenHelper.REPOS_TABLE_NAME, LOGIN_NAME + " = ?", new String[]{login});
    }

    @Override
    public void cleanCommits(int repoId) {
        mDb.delete(COMMITS_TABLE_NAME, REPO_ID + " = ?", new String[]{String.valueOf(repoId)});
    }

    @Override
    public void saveCommits(int repoId, ArrayList<CommitInfo> commits) {
        BriteDatabase.Transaction transaction = mDb.newTransaction();
        try {
            for (CommitInfo commit : commits) {
                ContentValues values = new ContentValues();
                values.put(REPO_ID, repoId);
                values.put(SHA, commit.getSha());
                values.put(MESSAGE, commit.getCommit().getMessage());
                values.put(AUTHOR, commit.getCommit().getAuthor().getName());
                values.put(DATE, commit.getCommit().getAuthor().getDate());
                mDb.insert(COMMITS_TABLE_NAME, values);
            }
            transaction.markSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            transaction.end();
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
    public Observable<ArrayList<CommitInfo>> getCommits(int repoId) {
        return mDb.createQuery(RepoSQLiteOpenHelper.COMMITS_TABLE_NAME, "SELECT * FROM "+COMMITS_TABLE_NAME
                        +" WHERE "+REPO_ID+"=@repoId"
                        +" ORDER BY "+DATE + " DESC",
                new String[]{String.valueOf(repoId)})
                .flatMap(new Func1<SqlBrite.Query, Observable<ArrayList<CommitInfo>>>() {
                    @Override
                    public Observable<ArrayList<CommitInfo>> call(SqlBrite.Query query) {
                        ArrayList<CommitInfo> data = readCommitCursor(query.run());
                        return Observable.just(data);
                    }
                });
    }

    @Override
    public Observable<ArrayList<CommitInfo>> searchCommits(int repoId, String searchText) {
        return mDb.createQuery(RepoSQLiteOpenHelper.COMMITS_TABLE_NAME, "SELECT * FROM "+COMMITS_TABLE_NAME
                        +" WHERE "+REPO_ID+"=@repoId and ("+ MESSAGE +" like @search" + " or "+ AUTHOR +" like @search)"
                        +" ORDER BY "+DATE + " DESC",
                new String[]{String.valueOf(repoId), "%" + searchText + "%"})
                .flatMap(new Func1<SqlBrite.Query, Observable<ArrayList<CommitInfo>>>() {
                    @Override
                    public Observable<ArrayList<CommitInfo>> call(SqlBrite.Query query) {
                        ArrayList<CommitInfo> data = readCommitCursor(query.run());
                        return Observable.just(data);
                    }
                });
    }

}
