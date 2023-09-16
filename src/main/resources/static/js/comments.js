app.service('CommentService', ['$http', function ($http) {
    const commentsApiPath = contextPath + '/api/v1/comments';
    return {
        getComments: function (articleId, pageNumber, size) {
            const httpParams = {
                p: pageNumber,
                s: size
            };
            const getQuery = {
                method: 'GET',
                url: commentsApiPath + '/article/' + articleId
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
                url: commentsApiPath + '/article'
            };
            postQuery["data"] = httpParams;
            return $http(postQuery);
        },
        deleteComment: function(commentId){
            return $http.delete(`${commentsApiPath}/${commentId}`);
        }
    }
}]);
// Функция для загрузки комментариев с сервера
app.controller('CommentController', function ($scope, $routeParams, $timeout, CommentService, RoleService, AuthService) {
    $scope.comments = [];
    $scope.currentPage = 1;
    $scope.itemsPerPage = 5;
    $scope.totalPages = 5;
    $scope.contentLoading = false;
    $scope.loadingError = null;
    $scope.newComment = {
        text: ''
    }
    const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
    $scope.articleId = $routeParams.articleId;
    //функция для загрузки комментариев
    $scope.loadComments = function (pageNumber) {
        $scope.currentPage = pageNumber;
        $scope.contentLoading = true;
        CommentService.getComments($scope.articleId, pageNumber, $scope.itemsPerPage)
            .then(function (response) {
                $scope.comments = response.data.content;
                $scope.totalPages = response.data.totalPages;
                $timeout(function(){
                    $scope.contentLoading = false;
                    $scope.loadingError = null;
                }, 300);
            })
            .catch(function (error) {
                $scope.loadingError = 'Ошибка загрузки'
                console.log(error);
            });
    };
    //функция для публикации комментария
    $scope.postComment = function () {
        CommentService.postComment($scope.articleId, $scope.newComment.text)
            .then(function (response) {
                const publishedComment = response.data;
                $scope.newComment.text = '';
                if($scope.comments.length < $scope.itemsPerPage ) {
                    if($scope.totalPages === 0)$scope.totalPages = 1
                    $scope.comments.push(publishedComment);
                }
                else $scope.totalPages++;
            })
            .catch(function (error) {
                showErrorToast("Ошибка отправки", "Не удалось отправить комментарий!");
                console.error(error);
            });
    }
    //функция для получения страницы с заданным номером
    $scope.getPage = function (pageNumber) {
        $scope.loadComments(pageNumber);
        $scope.pageNumbers = calculatePageNumbers(pageNumber, $scope.totalPages, maxPagesToShow);
    }
    $scope.deleteItem = function (comment){
        CommentService.deleteComment(comment.commentId)
            .then(() => deleteItemAndGetNewPage($scope.comments, $scope.totalPages, $scope.currentPage, c => c.commentId === comment.commentId, $scope.getPage))
            .catch(function (error) {
                showErrorToast("Ошибка", "Ошибка удаления комментария");
                console.log(error);
            });
    }
    $scope.editItem = function (comment){
        //TODO реализовать метод редактирования комментария
    }
    $scope.canPost = RoleService.isCommenter;
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.canEditItem = function (comment) {
        //TODO исправить эту функцию, сделать так, чтобы она реально проверяла, может ли пользователь редактировать комментарий
        return false;
    }
    $scope.canDeleteItem = function (comment) {
        return RoleService.isAdmin() || (AuthService.isAuthenticated() && AuthService.getMyUserName() === comment.authorName);
    }
    $scope.hasAnyActionsForItem = function (comment){
        return $scope.canEditItem(comment) || $scope.canDeleteItem(comment);
    }
    $scope.$watch('totalPages', function () {
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    $scope.getPage(1);
});