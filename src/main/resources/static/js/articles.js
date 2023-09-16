app.service('ArticleService', function ($http) {
    const articlesApiPath = contextPath + '/api/v1/articles';
    return {
        getArticlesInfo: function (filterParams, pageNumber, pageSize) {
            const httpParams = generatePageParamsAndFilterParams(filterParams, pageNumber, pageSize);
            const httpQuery = {
                method: 'GET',
                url: articlesApiPath + '/info/all'
            };
            httpQuery["params"] = httpParams;
            return $http(httpQuery);
        },
        getArticle: function (articleId) {
            return $http.get(`${articlesApiPath}/${articleId}`);
        },
        postArticle: function (title, text) {
            return $http.post(articlesApiPath, {
                title: title,
                text: text
            });
        },
        openArticle: function (articleId) {
            document.location.hash = `!/articles/${articleId}`;
        },
        deleteArticle: function (articleId){
            return $http.delete(`${articlesApiPath}/${articleId}`);
        }
    };
});
app.controller('ArticlesController', function ($scope, $timeout, ArticleService, RoleService, AuthService) {
    $scope.articles = [];
    $scope.filterParams = {};
    $scope.filter = {};
    $scope.currentPage = 1;
    $scope.itemsPerPage = 5;
    $scope.totalPages = 5;
    $scope.contentLoading = false;
    $scope.loadingError = null;
    const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
    $scope.filterArticles = function () {
        $scope.filterParams = $scope.filter;
        $scope.getPage(1);
    };
    $scope.loadArticlesInfo = function (filterParams, pageNumber) {
        $scope.contentLoading = true;
        ArticleService.getArticlesInfo(filterParams, pageNumber, $scope.itemsPerPage)
            .then(function (response) {
                $scope.articles = response.data.content;
                $scope.totalPages = response.data.totalPages;
                $scope.currentPage = pageNumber;
                $timeout(function () {
                    $scope.contentLoading = false;
                    $scope.loadingError = null;
                }, 300);
            })
            .catch(function (error) {
                $scope.loadingError = 'Ошибка загрузки';
                console.log(error);
            });
    };
    $scope.resetFilter = function(){
        $scope.filterParams = {};
        $scope.filter = {};
        $scope.getPage(1);
    };
    $scope.getPage = function (pageNumber) {
        $scope.loadArticlesInfo($scope.filterParams, pageNumber);
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    };
    $scope.goToArticle = function (articleId) {
        ArticleService.openArticle(articleId)
    };
    $scope.deleteItem = function (article){
        ArticleService.deleteArticle(article.articleId)
            .then(function (){
                const index = $scope.articles.findIndex(a => a.articleId === article.articleId);
                // Удаляем пользователя с найденным индексом
                if (index !== -1) {
                    $scope.articles.splice(index, 1);
                    if($scope.articles.length === 0 && $scope.totalPages > 1){
                        $scope.getPage($scope.currentPage > 1 ? $scope.currentPage - 1 : 1);
                    }
                }
            })
            .catch(function (error) {
                showErrorToast("Ошибка", "Ошибка удаления комментария");
                console.log(error);
            });
    }
    $scope.editItem = function (comment){

    }
    $scope.canEditItem = function (article) {
        //TODO исправить эту функцию, сделать так, чтобы она реально проверяла, может ли пользователь редактировать статью
        return false;
    }
    $scope.canDeleteItem = function (article) {
        return RoleService.isAdmin() || (AuthService.isAuthenticated() && AuthService.getMyUserName() === article.publisher.name);
    }
    $scope.hasAnyActionsForItem = function (article){
        return $scope.canEditItem(article) || $scope.canDeleteItem(article);
    }
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function () {
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    $scope.getPage(1);
});
app.controller('ArticleController', function ($scope, $routeParams, ArticleService, RoleService) {
    if ($routeParams.articleId) {
        ArticleService.getArticle($routeParams.articleId).then(function (response) {
            $scope.article = response.data;
        });
    }
    $scope.sendingError = null;
    //переменная содержащая новую статью для публикации
    $scope.newArticle = {
        title: '',
        text: ''
    }
    //метод сообщающий о том, может ли пользователь писать статьи
    $scope.canPublish = () => RoleService.isPublisher();
    //метод публикующий статью
    $scope.publishArticle = function () {
        ArticleService.postArticle($scope.newArticle.title, $scope.newArticle.text)
            .then(function (response) {
                //здесь мы ожидаем что нам в ответ придёт как минимум id новой статьи
                //todo подумать над тем, что нужно делать после успешной публикации
                const articleId = response.data.articleId;
                $scope.newArticle.title = ''
                $scope.newArticle.text = ''
                ArticleService.openArticle(articleId);
            });
    };
});