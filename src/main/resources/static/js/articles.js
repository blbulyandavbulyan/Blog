app.service('ArticleService', function($http){
    const articlesApiPath = contextPath + '/api/v1/articles';
    return {
        getArticlesInfo: function(pageNumber, pageSize){
            var httpParams = {
                p: pageNumber,
                s: pageSize
            };
            var httpQuery = {
                method: 'GET',
                url: articlesApiPath + '/info/all'
            };
            httpQuery["params"] = httpParams;
            return $http(httpQuery);
        }
    };
});
app.controller('ArticleController', function($scope, $window, ArticleService){
      $scope.articles = [];
      $scope.currentPage = 1;
      $scope.itemsPerPage = 5;
      $scope.totalPages = 5;
      const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
      $scope.loadArticlesInfo = function(pageNumber) {
        ArticleService.getArticlesInfo(pageNumber, $scope.itemsPerPage)
          .then(function(response) {
            $scope.articles = response.data.content;
            $scope.totalPages = response.data.totalPages;
            $scope.currentPage = pageNumber;
          });
      };
      $scope.getPage = function(pageNumber){
           $scope.loadArticlesInfo(pageNumber);
           $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
      }
      $scope.generateArticleLink = function(articleId){
          const currentUrl = $window.location.href;
          // Добавить параметр articleId к текущему пути URL и вернуть его
          return `${currentUrl}/${articleId}`;
      }
      // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
     $scope.$watch('totalPages', function() {
       $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
     });
     $scope.getPage(1);
});