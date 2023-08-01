var commentsApiPath = '/api/v1/comments';
app.service('CommentService', ['$http', function($http){
    return {
            getComments: function(articleId, pageNumber, size){
                var httpParams = {
                    p: pageNumber,
                    s:size
                };
                var getQuery = {
                    method: 'GET',
                    url: contextPath + commentsApiPath + '/article/' + articleId
                }
                getQuery["params"] = httpParams;
                return $http(getQuery);
            },
            postComment: function(articleId, text){
                var httpParams = {
                    articleId: articleId,
                    text: text
                };
                var postQuery = {
                    method: 'POST',
                    url: contextPath + commentsApiPath + '/article'
                }
                postQuery["data"] = httpParams;
                return $http(postQuery);
            }
        }
}]);
// Функция для загрузки комментариев с сервера
app.controller('CommentController', function($scope, CommentService, CookieService) {
     $scope.comments = [];
     $scope.currentPage = 1;
     $scope.itemsPerPage = 5;
     $scope.totalPages = 5;
     const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
     var href = window.location.href.split('/');
     $scope.articleId = parseInt(href[href.length-1]);//таким нехитрым образом, мы получаем ИД статьи из PathVariable в url
     //функция для загрузки комментариев
     $scope.loadComments = function(pageNumber) {
       $scope.currentPage = pageNumber;
       CommentService.getComments($scope.articleId, pageNumber, $scope.itemsPerPage)
         .then(function(response) {
           $scope.comments = response.data.content;
           $scope.totalPages = response.data.totalPages;
         });
     };
     //функция для публикации комментария
     $scope.postComment = function(){
        CommentService.postComment($scope.articleId, $scope.newComment.text)
                .then(function(response){
                    $scope.newComment.text = '';
                    $scope.sendingError = null;
                })
                .catch(function(error){
                    $scope.sendingError = 'Ошибка отправки!'
                });
     }
     //функция для получения страницы с заданным номером
     $scope.getPage = function(pageNumber){
          $scope.loadComments(pageNumber);
          $scope.pageNumbers = calculatePageNumbers(pageNumber, $scope.totalPages, maxPagesToShow);
     }
     $scope.canPost = function () {
         const token = CookieService.getCookie('token');
         if (!token) return false; // Если токен не существует, считаем пользователя не COMMENTER
         const payloadBase64 = token.split('.')[1];
         const payloadJson = atob(payloadBase64);
         const payload = JSON.parse(payloadJson);
         const roles = payload.roles;
         return roles.includes('ROLE_COMMENTER'); // Проверяем наличие роли COMMENTER в массиве ролей
     };
     // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
     $scope.$watch('totalPages', function() {
       $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
     });
     $scope.getPage(1);
});