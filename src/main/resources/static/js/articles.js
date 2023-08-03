app.service('ArticleService', function($http){
    const articlesApiPath = contextPath + '/api/v1/articles';
    return {
        getArticlesInfo: function(filterParams, pageNumber, pageSize){
            var httpParams = {
                p: pageNumber,
                s: pageSize
            };
            Object.keys(filterParams).forEach(function(key) {
                var value = filterParams[key];
                if(value === 'string') {
                   value = value.trim();
                }
                if (value !== '') {
                    httpParams[key] = value;
                }
            });
            var httpQuery = {
                method: 'GET',
                url: articlesApiPath + '/info/all'
            };
            httpQuery["params"] = httpParams;
            return $http(httpQuery);
        },
        getArticle: function(articleId){
            return $http.get(`${articlesApiPath}/${articleId}`);
        }
    };
});
app.controller('ArticlesController', function($scope, ArticleService){
      $scope.articles = [];
      $scope.filterParams = {};
      $scope.filter = {};
      $scope.currentPage = 1;
      $scope.itemsPerPage = 5;
      $scope.totalPages = 5;
      const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
      $scope.filterArticles = function(){
           $scope.filterParams = $scope.filter;
           $scope.getPage(1);
      }
      $scope.loadArticlesInfo = function(filterParams, pageNumber) {
        ArticleService.getArticlesInfo(filterParams, pageNumber, $scope.itemsPerPage)
          .then(function(response) {
            $scope.articles = response.data.content;
            $scope.totalPages = response.data.totalPages;
            $scope.currentPage = pageNumber;
          });
      };
      $scope.getPage = function(pageNumber){
           $scope.loadArticlesInfo($scope.filterParams, pageNumber);
           $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
      }
      $scope.goToArticle = function(articleId) {
           document.location.hash=`!/articles/${articleId}`;
      };
      // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
      $scope.$watch('totalPages', function() {
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
      });
      $scope.getPage(1);
});