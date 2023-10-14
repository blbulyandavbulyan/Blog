# Blog
Это простенький движок для некоторого подобия блога, здесь можно писать статьи и комментарии к ним.
## Что здесь есть ?
* Подключен шаблонизатор thymeleaf
* Есть REST контроллеры
* Есть обычные контроллеры, возвращающие шаблоны thymeleaf
* Подключен Spring Data для более удобной работы с базой
* JWT авторизация для REST API
* Сущности:
  * Comment - комментарий, который могут написать пользователи с ролью _COMMENTER_
  * User - пользователь
  * Role - роль пользователя
  * Article - статья, которую могут написать пользователи с ролью _PUBLISHER_
## Возможности администраторов
Пользователь является администратором, если он имеет коль _ADMIN_, он может:
* создавать пользователей с любыми ролями
* удалять пользователей
* удалять статьи
* удалять комментарии
## Возможности пользователей
* Пользователи с ролью _PUBLISHER_ могут писать статьи
* Пользователи с ролью _COMMENTER_ могут писать комментарии к любым статьям
* Так же пользователи могут менять свой пароль
* Редактировать и удалять свою статью
* Редактировать и удалять свои комментарии
