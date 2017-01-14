package ru.merkulyevsasha.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;

import ru.merkulyevsasha.github.helpers.db.DbInterface;
import ru.merkulyevsasha.github.helpers.http.HttpDataInterface;
import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Credentials;
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

public class ReposPresenterTests {

    private static final String TEST_LOGIN = "testlogin";
    private static final Credentials CREDENTIALS_TEST = new Credentials(TEST_LOGIN, TEST_LOGIN);

    @Mock
    MvpListView view;

    @Mock
    DbInterface db;

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
        RxJavaHooks.clear();
    }

    private ArrayList<Repo> getTestReposCollectionWithItem(final int id){
        final ArrayList<Repo> collection = new ArrayList<>();
        final Repo item = new Repo();
        item.setId(id);
        collection.add(item);
        return collection;
    }

    private HttpDataInterface getHttpDataInterface(final ArrayList<Repo> testRepos){
        return new HttpDataInterface(){
            @Override
            public Observable<ArrayList<Auth>> auth(String login, String password) {
                return null;
            }

            @Override
            public Observable<ArrayList<CommitInfo>> getCommits(String login, String password, String owner, String repo) {
                return null;
            }

            @Override
            public Observable<ArrayList<Repo>> getRepos(String login, String password) {
                return Observable.just(testRepos);
            }
        };
    }

    private DbInterface getDataSearchInterface(final ArrayList<Repo> testRepos){
        return new DbInterface(){
            @Override
            public ArrayList<Repo> getRepos(String login) {
                return testRepos;
            }

            @Override
            public ArrayList<Repo> searchRepos(String login, String searchText) {
                return testRepos;
            }

            @Override
            public void saveRepos(String login, ArrayList<Repo> repos) {

            }

            @Override
            public void cleanRepos(String login) {

            }

            @Override
            public void cleanCommits(int repoId) {

            }

            @Override
            public void saveCommits(int repoId, ArrayList<CommitInfo> commits) {

            }

            @Override
            public ArrayList<CommitInfo> getCommits(int repoId) {
                return null;
            }

        };
    }

    @Test
    public void load_getdatafromdb_if_nohttpdata() throws Exception {

        final ArrayList<Repo> dbRepos = getTestReposCollectionWithItem(1);
        final ArrayList<Repo> httpRepos = new ArrayList<>();

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, getDataSearchInterface(dbRepos), getHttpDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(dbRepos);

    }

    @Test
    public void load_getdatafromdb_if_httpdata() throws Exception {

        final ArrayList<Repo> dbRepos = getTestReposCollectionWithItem(1);
        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, getDataSearchInterface(dbRepos), getHttpDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(dbRepos);

    }

    @Test
    public void load_getdatafromhttp_if_dbcollectiondataempty() throws Exception {

        final ArrayList<Repo> dbRepos = new ArrayList<>();
        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, getDataSearchInterface(dbRepos), getHttpDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(httpRepos);

    }

    @Test
    public void load_getdatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, db, getHttpDataInterface(httpRepos));
        presenter.load();

        verify(view).showList(httpRepos);

    }

    @Test
    public void load_savedatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, db, getHttpDataInterface(httpRepos));
        presenter.load();

        verify(db).saveRepos(TEST_LOGIN, httpRepos);

    }

    @Test
    public void loadFromHttp_savedatafromhttp_if_nodbdata_atall() throws Exception {

        final ArrayList<Repo> httpRepos = getTestReposCollectionWithItem(2);

        ReposPresenter presenter = new ReposPresenter(CREDENTIALS_TEST, view, db, getHttpDataInterface(httpRepos));
        presenter.loadFromHttp();

        verify(db).saveRepos(TEST_LOGIN, httpRepos);

    }


}