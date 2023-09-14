const commentsApiPath = '/api/v1/comments';
app.service('CommentService', ['$http', function ($http) {
    return {
        getComments: function (articleId, pageNumber, size) {
            const httpParams = {
                p: pageNumber,
                s: size
            };
            const getQuery = {
                method: 'GET',
                url: contextPath + commentsApiPath + '/article/' + articleId
            };
            getQuery["params"] = httpParams;
            return $http(getQuery);
        },
        postComment: function (articleId, text) {
            const httpParams = {
                articleId: articleId,
                text: text
            };
            const postQuery = {
                method: 'POST',
                url: contextPath + commentsApiPath + '/article'
            };
            postQuery["data"] = httpParams;
            return $http(postQuery);
        }
    }
}]);
// Функция для загрузки комментариев с сервера
app.controller('CommentController', function ($scope, $routeParams, CommentService, RoleService) {
    $scope.comments = [];
    $scope.currentPage = 1;
    $scope.itemsPerPage = 5;
    $scope.totalPages = 5;
    $scope.newComment = {
        text: ''
    }
    const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
    $scope.articleId = $routeParams.articleId;
    //функция для загрузки комментариев
    $scope.loadComments = function (pageNumber) {
        $scope.currentPage = pageNumber;
        CommentService.getComments($scope.articleId, pageNumber, $scope.itemsPerPage)
            .then(function (response) {
                $scope.comments = response.data.content;
                $scope.totalPages = response.data.totalPages;
            });
    };
    //функция для публикации комментария
    $scope.postComment = function () {
        CommentService.postComment($scope.articleId, $scope.newComment.text)
            .then(function (response) {
                const publishedComment = response.data;
                $scope.newComment.text = '';
                $scope.sendingError = null;
                if($scope.comments.length < $scope.itemsPerPage ) {
                    if($scope.totalPages === 0)$scope.totalPages = 1
                    $scope.comments.push(publishedComment);
                }
                else $scope.totalPages++;
            })
            .catch(function (error) {
                $scope.sendingError = 'Ошибка отправки!'
            });
    }
    //функция для получения страницы с заданным номером
    $scope.getPage = function (pageNumber) {
        $scope.loadComments(pageNumber);
        $scope.pageNumbers = calculatePageNumbers(pageNumber, $scope.totalPages, maxPagesToShow);
    }
    $scope.canPost = RoleService.isCommenter;
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function () {
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    $scope.getPage(1);
});