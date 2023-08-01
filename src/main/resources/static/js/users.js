app.service('UserService', function($http){
    const usersApiPath = contextPath + '/api/v1/users';
    return {
        getUserProfile: function(username){
            return $http.get(`${usersApiPath}/${username}`);
        },
        //получение информации о пользователях для администраторов
        getUserInfoAboutAllUsers: function(filterParams, pageNumber, pageSize){
            var httpParams = {
                p: pageNumber,
                s: pageSize
            };
            //TODO вынести этот кусок в глобальную функцию, т.к. он повторяется в другом сервисе
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
                url: `${usersApiPath}/info/all`
            }
            httpQuery["params"] = httpParams;
            return $http(httpQuery);
        },
        createUser: function(username, password, rolesNames){
            var createUserRequest = {
                name: username,
                password: password,
                roleNames: rolesNames
            }
            return $http.post(usersApiPath, createUserRequest);
        },
        deleteUserById: function(userId){
            return $http.delete(`${usersApiPath}/${userId}`);
        }
    }
});
app.controller('UserController', function($scope, UserService){
    $scope.users = [];
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
    $scope.loadUsersInfo = function(filterParams, pageNumber) {
      UserService.getUserInfoAboutAllUsers(filterParams, pageNumber, $scope.itemsPerPage)
        .then(function(response) {
          $scope.users = response.data.content;
          $scope.totalPages = response.data.totalPages;
          $scope.currentPage = pageNumber;
        });
    };
    $scope.getPage = function(pageNumber){
         $scope.loadUsersInfo($scope.filterParams, pageNumber);
         $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    }
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function() {
      $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    $scope.getPage(1);
});