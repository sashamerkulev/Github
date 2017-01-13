package ru.merkulyevsasha.github;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ru.merkulyevsasha.github.helpers.db.DbHelper;
import ru.merkulyevsasha.github.models.Repo;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;


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
        collection.add(item);
        return collection;
    }

    @Test
    public void saveRepos_works() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DbHelper db = new DbHelper(appContext, Schedulers.immediate());
        db.cleanRepos(TEST_LOGIN);
        ArrayList<Repo> collectionTest = getTestReposCollectionWithItem(1, "name");

        db.saveRepos(TEST_LOGIN, collectionTest);

        TestSubscriber<ArrayList<Repo>> testSubscriber = new TestSubscriber<>();

        db.getRepos(TEST_LOGIN).subscribe(testSubscriber);


        testSubscriber.assertValueCount(collectionTest.size());


    }

    @Test
    public void searchRepos_works() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DbHelper db = new DbHelper(appContext, Schedulers.immediate());
        db.cleanRepos(TEST_LOGIN);

        ArrayList<Repo> collectionTest = getTestReposCollectionWithItem(1, "name");

        db.saveRepos(TEST_LOGIN, collectionTest);

        TestSubscriber<ArrayList<Repo>> testSubscriber = new TestSubscriber<>();

        db.searchRepos(TEST_LOGIN, "nam").subscribe(testSubscriber);

        testSubscriber.assertValueCount(collectionTest.size());

    }

}
