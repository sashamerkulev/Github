package ru.merkulyevsasha.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;

import ru.merkulyevsasha.github.helpers.DataInterface;
import ru.merkulyevsasha.github.helpers.DatabaseInterface;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.repolist.MvpListView;
import ru.merkulyevsasha.github.mvp.repolist.ReposPresenter;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.*;

public class ReposPresenterTest {

    private static final String TEST_LOGIN = "testlogin";

    @Mock
    MvpListView view;

    @Mock
    DatabaseInterface db;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.setOnNewThreadScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return Schedulers.immediate();
            }
        });

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    private ArrayList<Repo> getTestReposCollectionWithItem(final int id){
        final ArrayList<Repo> collection = new ArrayList<>();
        final Repo item = new Repo();
        item.setId(id);
        collection.add(item);
        return collection;
    }

    private DataInterface getDataInterface(final ArrayList<Repo> testRepos){
        return new DataInterface(){
            @Override
            public Observable<ArrayList<Repo>> getRepos() {
                return Observable.just(testRepos);
            }
        };
    }

    private DatabaseInterface getDataSearchInterface(final ArrayList<Repo> testRepos){
        return new DatabaseInterface(){
            @Override
            public Observable<ArrayList<Repo>> getRepos(String login) {
                return Observable.just(testRepos);
            }

            @Override
            public Observable<ArrayList<Repo>> searchRepos(String login, String searchText) {
                return Observable.just(testRepos);
            }

            @Override
            public void saveRepos(String login, ArrayList<Repo> repos) {

            }

            @Override
            public void cleanRepos() {

            }

        };
    }

    @Test
    public void load_getdatafromdb_if_nohttpdata() throws Exception {

        final ArrayList<Repo> dbRepos = getTestReposCollectionWithItem(1);
        final ArrayList<Repo> httpRepos = new ArrayList<>();

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, getDataSearchInterface(dbRepos), getDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(dbRepos);

    }

    @Test
    public void load_getdatafromdb_if_httpdata() throws Exception {

        final ArrayList<Repo> dbRepos = getTestReposCollectionWithItem(1);
        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, getDataSearchInterface(dbRepos), getDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(dbRepos);

    }

    @Test
    public void load_getdatafromhttp_if_dbcollectiondataempty() throws Exception {

        final ArrayList<Repo> dbRepos = new ArrayList<>();
        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, getDataSearchInterface(dbRepos), getDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(httpRepos);

    }

    @Test
    public void load_getdatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, db, getDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(httpRepos);

    }

    @Test
    public void load_savedatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, db, getDataInterface(httpRepos));
        presenter.load();

        verify(db).saveRepos(TEST_LOGIN, httpRepos);

    }

    @Test
    public void loadFromHttp_savedatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(TEST_LOGIN, view, db, getDataInterface(httpRepos));
        presenter.loadFromHttp();

        verify(db).saveRepos(TEST_LOGIN, httpRepos);

    }


}