var app = angular.module('blog', []);
// Определение интерцептора
app.factory('authInterceptor', function () {
  return {
    request: function (config) {
      // Ваш код интерцептора
      const token = getCookie('token');
      if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
      }
      return config;
    }
  };
});

// Добавление интерцептора к конфигурации $httpProvider
app.config(function ($httpProvider) {
  $httpProvider.interceptors.push('authInterceptor');
});
const contextPath = window.location.origin + '/blog';
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
app.controller('CommentController', function($scope, CommentService) {
     $scope.comments = [];
     $scope.currentPage = 1;
     $scope.itemsPerPage = 5;
     $scope.totalPages = 5;
     const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
     var href = window.location.href.split('/');
     $scope.articleId = parseInt(href[href.length-1]);
     $scope.loadComments = function(pageNumber) {
       $scope.currentPage = pageNumber;
       CommentService.getComments($scope.articleId, pageNumber, $scope.itemsPerPage)
         .then(function(response) {
           $scope.comments = response.data.content;
           $scope.totalPages = response.data.totalPages;
         });
     };
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
     // Функция для рассчета списка номеров страниц с учетом многоточий
     $scope.getPage = function(pageNumber){
          $scope.loadComments(pageNumber);
          calculatePageNumbers();
     }
     $scope.canPost = function () {
         const token = getCookie('token');
         if (!token) return false; // Если токен не существует, считаем пользователя не COMMENTER
         const payloadBase64 = token.split('.')[1];
         const payloadJson = atob(payloadBase64);
         const payload = JSON.parse(payloadJson);
         const roles = payload.roles;
         return roles.includes('ROLE_COMMENTER'); // Проверяем наличие роли COMMENTER в массиве ролей
     };
     function calculatePageNumbers() {
       $scope.pageNumbers = [];
       const currentPage = $scope.currentPage;
       const totalPages = $scope.totalPages;
       if (totalPages <= maxPagesToShow) {
         for (let i = 1; i <= totalPages; i++) {
           $scope.pageNumbers.push(i);
         }
       } else {
         let startPage;
         let endPage;

         if (currentPage <= Math.ceil(maxPagesToShow / 2)) {
           startPage = 1;
           endPage = maxPagesToShow;
         } else if (currentPage >= totalPages - Math.floor(maxPagesToShow / 2)) {
           startPage = totalPages - maxPagesToShow + 1;
           endPage = totalPages;
         } else {
           startPage = currentPage - Math.floor(maxPagesToShow / 2);
           endPage = currentPage + Math.floor(maxPagesToShow / 2);
         }
         for (let i = startPage; i <= endPage; i++) {
           $scope.pageNumbers.push(i);
         }
       }
     }
     // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
     $scope.$watch('totalPages', function() {
       calculatePageNumbers();
     });
     $scope.getPage(1);
});