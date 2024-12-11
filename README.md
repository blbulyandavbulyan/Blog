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
## Deployment
This application is possible to deploy in GKE using CloudBuild.
HELM is used here. You can find its templates and other stuff in `deploy-templates` folder

### Steps in cloud build pipeline:
* __Image build__,
  the image builds with two tags, one of them is __latest__ and another is your __commit hash__ (which triggered the pipeline in CloudBuild)
* __Image push__ to Google Artifact Registry(with those two tags)
* __Deploy image to GKE cluster__,
  latest version of the app will be deployed to GKE

### Parameters for cloudbuild.yaml
* `_IMAGE_REPO_LOCATION` - region of your Google Artifact Registry Repository
* `_IMAGE_REPO_NAME` - name of your GAR repository
* `-GKE_CLUSTER_NAME` - name of your GKE cluster
  For GKE cluster the region of your CloudBuild trigger will be used
* `_CHART_NAME` - HELM chart name of the application (you can choose any name)
* `_DB_USERNAME_SECRET_NAME` - secret version name in Google Secrets Manager for database username
* `_DB_PASSWORD_SECRET_NAME` - secret version name in Google Secrets Manager for database password

About secrets in Cloud Build you can find the docs [here](https://cloud.google.com/build/docs/securing-builds/use-secrets#:~:text=You%20can%20get%20the%20secret%20version%20by%20clicking%20on%20a,in%20the%20Google%20Cloud%20console.)

For deploying in GKE my custom image is used, which is a combination of __gcloud__, __kubectl__ and __helm__.
You can find `Dockerfile` for this image in `ci-cd-images` folder in the project


