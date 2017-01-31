# Github


Приложение для работы с Github REST API, с очень ограниченным функционалом (список репозитариев и список коммитов в репозитарии).

В приложение используется Basic авторизация, что конечно не очень хорошо, передавать логин/пароль простым текстом, но, может руки дойдут и переделаю на OAuth, ну, и, функционал можно будет добавить.

Для реализации основного функционала использовались RxJava, Retrofit2, Picasso, SqlBrite (RxJava обертка над SQLiteOpenHelper).

###RxJava+Retrofit2 
Данная связка используется для работы с Github REST API.

###Picasso 
Используется для отображения аватарки владельца репозитария.

###Sqlite
Sqlite используется для кеширования данных о репозитарии и коммитах.

###SqlBrite
RxJava обертка над SQLiteOpenHelper, используется для того, что бы работать одинаковым способом и с сетью и с БД.
Что позволяет написать такой код (пытаемся читать данные из БД, если их нет, читаем из сети и пишем в БД):
```java
mDatabase.getRepos(login)
                .flatMap(new Func1<ArrayList<Repo>, Observable<ArrayList<Repo>>>() {
                    @Override
                    public Observable<ArrayList<Repo>> call(ArrayList<Repo> repos) {
                        return repos.size()==0 ? getNewRepos(login, password)
                                : Observable.just(repos);
                    }
                }).cache();
```
Оператор switchIfEmpty() не подходит, потому что SqlBrite не посылает терминирующее сообщение, что позволяет использовать эту библиотеку для постоянного отслеживания изменений в БД и нотификации пользователя об этом.

https://github.com/square/sqlbrite

###MVP
Для MainActivity и DetailsActivity используется архитектурный шаблон MVP.

- UI уровень представлен парой классов Activity/Presenter (MainActivity/ReposPresenter, DetailsActivity/CommitsPresenter);
- Существует как минимум один DataModel класс  (ReposDataModelImpl, CommitsDataModelImpl) для каждой пары Activity/Presenter, который покрывает всю необходимую функциональность пары.

Уровень DataModel абстрагирует работу с БД и сетью через соответствующие интерфейсы. К примеру, не должно быть сложным сделать другую реализацию DatabaseServiceInterface и работать с другой БД, например, Realm.

###Dagger2

Используется для внедрения Presenters в Activities.

DataModel классы создаются/используются для создании Presenters в @Module.

###Анимация
Есть несложная анимация переходов от одной активити к другой, используется overridePendingTransition и ресурсы анимации.
