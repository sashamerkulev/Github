# Github


Приложение для работы с Github REST API, с очень ограниченным функционалом (список репозитариев и список коммитов в репозитарии).

В приложение используется Basic авторизация, что конечно не очень хорошо, передавать логин/пароль простым текстом, но, может руки дойдут и переделаю на OAuth, ну, и, функционал можно будет добавить.

Для реализации основного функционала использовались RxJava, Retrofit2, Picasso.

###RxJava+Retrofi2 
Данная связка используется для работы с Github REST API.

###Picasso 
Используется для отображения аватарки владельца репозитария.

###Sqlite
Для кеширования данных о репозитарии и коммитах используется Sqlite.

###MVP
Для MainActivity и DetailsActivity используется архитектурный шаблон MVP, для LoginActivity и SplashActivity не используется.

MVP реализовано ручками (это значит, что такие популярные библиотеки как moxy или mosby не используются).

Есть несложная анимация переходов от одной активити к другой.
