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
app.controller('ArticleController', function($scope, ArticleService){
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
      // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
     $scope.$watch('totalPages', function() {
       $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
     });
     $scope.getPage(1);
});