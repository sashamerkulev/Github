package ru.merkulyevsasha.github;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ru.merkulyevsasha.github.helpers.db.RepoSQLiteOpenHelper;
import ru.merkulyevsasha.github.models.Owner;
import ru.merkulyevsasha.github.models.Repo;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DbInstrumentedTest {

    private static final String TEST_LOGIN = "testlogin";

    private ArrayList<Repo> getTestReposCollectionWithItem(final int id, String name){
        final ArrayList<Repo> collection = new ArrayList<>();
        final Repo item = new Repo();
        item.setId(id);
        item.setFullName(name);
        item.setOwner(new Owner());
        collection.add(item);
        return collection;
    }

    @Test
    public void saveRepos_works() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        RepoSQLiteOpenHelper db = new RepoSQLiteOpenHelper(appContext);
        db.cleanRepos(TEST_LOGIN);
        ArrayList<Repo> collectionTest = getTestReposCollectionWithItem(1, "name");
        db.saveRepos(TEST_LOGIN, collectionTest);

        ArrayList<Repo> repos = db.getRepos(TEST_LOGIN);

        Assert.assertEquals(collectionTest.size(), repos.size());




    }

    @Test
    public void searchRepos_works() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        RepoSQLiteOpenHelper db = new RepoSQLiteOpenHelper(appContext);
        db.cleanRepos(TEST_LOGIN);

        ArrayList<Repo> collectionTest = getTestReposCollectionWithItem(1, "name");

        db.saveRepos(TEST_LOGIN, collectionTest);

        ArrayList<Repo> repos = db.searchRepos(TEST_LOGIN, "nam");

        Assert.assertEquals(collectionTest.size(), repos.size());

    }

}
