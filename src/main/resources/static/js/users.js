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
                url: `${usersApiPath}`
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
        },
        updateRoles: function(userId, rolesNames){
            httpData = {
                userId: userId,
                rolesNames: rolesNames
            };
            return $http.patch(`${usersApiPath}/roles`, httpData);
        }
    }
});
app.controller('UserController', function($scope, UserService, AuthService, RoleService){
    $scope.users = [];
    $scope.newRoles = {};
    $scope.filterParams = {};
    $scope.filter = {};
    $scope.currentPage = 1;
    $scope.itemsPerPage = 5;
    $scope.totalPages = 5;
    const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.canAdmin = RoleService.isAdmin;
    $scope.availableRoles = RoleService.getAvailableRoles();
    $scope.filterArticles = function(){
         $scope.filterParams = $scope.filter;
         $scope.getPage(1);
    }
    $scope.loadUsersInfo = function(filterParams, pageNumber) {
      UserService.getUserInfoAboutAllUsers(filterParams, pageNumber, $scope.itemsPerPage)
        .then(function(response) {
          $scope.users = response.data.content;
          $scope.newRoles = {};
          $scope.users.forEach(function(user){
            $scope.newRoles[user.userId] = {};
            user.roles.forEach(function(role){
               $scope.newRoles[user.userId][role.name] = true;
            });
            $scope.newRoles[user.userId].isChanged = false;
          });
          $scope.totalPages = response.data.totalPages;
          $scope.currentPage = pageNumber;
        });
    };
    $scope.getPage = function(pageNumber){
         $scope.loadUsersInfo($scope.filterParams, pageNumber);
         $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    };
    $scope.deleteUser = function(userId){
        UserService.deleteUserById(userId)
            .then(function(response){
                 var index = $scope.users.findIndex(user => user.userId === userId);
                 // Удаляем пользователя с найденным индексом
                 if (index !== -1) {
                   $scope.users.splice(index, 1);
                 }
            });
    };
    $scope.isItMe = function(user){
        return user.name === AuthService.getMyUserName();
    }
    $scope.hasRole = function (user, roleName) {
         return user.roles.map(r=>r.name).includes(roleName);
    };
    $scope.updateRoles = function(user){
        for(var i = 0; i < $scope.availableRoles.length; i++){
            roleName = $scope.availableRoles[i];
            if($scope.hasRole(user, roleName) != $scope.newRoles[user.userId][roleName]){
                $scope.newRoles[user.userId].isChanged  = true;
                return;
            }
            $scope.newRoles[user.userId].isChanged = false;
        }
    };
    $scope.applyChanges = function(user){
        newPrivileges = $scope.availableRoles.filter(r=>$scope.newRoles[user.userId][r]);
        $scope.newRoles[user.userId]
        UserService.updateRoles(user.userId, newPrivileges)
            .then(function(){
                user.roles = newPrivileges.map(rn=>{ return {name: rn}});
                $scope.newRoles[user.userId] = {};
                $scope.newRoles[user.userId].isChanged = false
                user.roles.forEach(function(role){
                   $scope.newRoles[user.userId][role.name] = true;
                });
            });
    };
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function() {
      $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    if($scope.canAdmin())$scope.getPage(1);
});