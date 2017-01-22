# Github


Приложение для работы с Github REST API, с очень ограниченным функционалом (список репозитариев и список коммитов в репозитарии).

В приложение используется Basic авторизация, что конечно не очень хорошо, передавать логин/пароль простым текстом, но, может руки дойдут и переделаю на OAuth, ну, и, функционал можно будет добавить.

Для реализации основного функционала использовались RxJava, Retrofit2, Picasso, SqlBrite (RxJava обертка над SQLiteOpenHelper).

###RxJava+Retrofi2 
Данная связка используется для работы с Github REST API.

###Picasso 
Используется для отображения аватарки владельца репозитария.

###Sqlite
Sqlite используется для кеширования данных о репозитарии и коммитах.

###SqlBrite

https://github.com/square/sqlbrite

###MVP
Для MainActivity и DetailsActivity используется архитектурный шаблон MVP.

- UI уровень представлен соответствующими Activities и Presenters (MainActivity/ReposPresenter, DetailsActivity/CommitsPresenter);
- DataModel представлен соотвествующитми классами (ReposDataModelImpl, CommitsDataModelImpl).

Уровень DataModel абстрагирует работу с БД и сетью через соответствующие интерфейсы. К примеру, не должно быть сложным сделать другую реализацию DatabaseServiceInterface и работать с другой БД, например, Realm.

###Dagger2

Используется для внедрения DataModel классов в соответствующие Activities.

###Анимация
Есть несложная анимация переходов от одной активити к другой, используется overridePendingTransition и ресурсы анимации.
