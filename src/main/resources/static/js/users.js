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
    $scope.newRoles = {};//в этом объекте, по ИД пользователя, будет хранится: изменён он или нет, и какие роли у него установлены
    $scope.filterParams = {};//текущие параметры для фильтрации пользователей
    $scope.filter = {};//модель для фильтрационной формы
    $scope.filter.roles = {};//инициализируем роли для фильтра
    $scope.currentPage = 1;//текущая страница с пользователями
    $scope.itemsPerPage = 5;//количество пользователей на страницу
    $scope.totalPages = 1;//всего страниц(в дальнейшем будет оно будет обновлено, при получении страниц
    const maxPagesToShow = 3; // Максимальное количество отображаемых страниц
    $scope.isAuthenticated = AuthService.isAuthenticated;//метод для проверки на наличие авторизации
    $scope.canAdmin = RoleService.isAdmin;//метод для проверки является ли пользователь администратором
    $scope.availableRoles = RoleService.getAvailableRoles();//метод для получения доступных ролей
    $scope.newUser = {
        name: "",
        password: ""
    };// определяем модель для формы создания пользователя
    $scope.newUser.roles = {};//инициализируем роли для нового пользователя пустым массивом
    $scope.availableRoles.forEach(function(roleName){
        $scope.filter.roles[roleName] = false;//по умолчанию выключены требования всех ролей
        $scope.newUser.roles[roleName] = false;//выключаем требования всех ролей для создания пользователей
    });
    $scope.filterUsers = function(){//метод для применения фильтра по пользователям
         rolesForFilter = $scope.availableRoles.filter(roleName=>$scope.filter.roles[roleName]);
         $scope.filterParams = {
            name: $scope.filter.name,
            roles: rolesForFilter
         };
         $scope.getPage(1);
    }
    function setRolesData(userId, rolesNames){
        $scope.availableRoles.forEach(function(roleName){
            $scope.newRoles[userId][roleName] = rolesNames.includes(roleName);
        });
    }
    $scope.loadUsersInfo = function(filterParams, pageNumber) {//метод для получения информации о пользователях
      UserService.getUserInfoAboutAllUsers(filterParams, pageNumber, $scope.itemsPerPage)
        .then(function(response) {
          $scope.users = response.data.content;
          $scope.newRoles = {};
          $scope.users.forEach(function(user){
            $scope.newRoles[user.userId] = {};
            rolesNames = user.roles.map(r=>r.name);
            setRolesData(user.userId, rolesNames)
            $scope.newRoles[user.userId].isChanged = false;
          });
          $scope.totalPages = response.data.totalPages;
          $scope.currentPage = pageNumber;
        });
    };
    $scope.getPage = function(pageNumber){//метод для получения заданной страницы
         $scope.loadUsersInfo($scope.filterParams, pageNumber);
         $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    };
    $scope.deleteUser = function(userId){//метод для обработки кнопки удаления пользователя
        UserService.deleteUserById(userId)
            .then(function(response){
                 var index = $scope.users.findIndex(user => user.userId === userId);
                 // Удаляем пользователя с найденным индексом
                 if (index !== -1) {
                   $scope.users.splice(index, 1);
                 }
            });
    };
    $scope.createUser = function(){
        name = $scope.newUser.name;
        password = $scope.newUser.password;
        rolesNames = $scope.availableRoles.filter(roleName=>$scope.newUser.roles[roleName]);
        UserService.createUser(name, password, rolesNames)
        .then(function(response){
            userCreatedResponse = response.data;
            $scope.newUser.name = '';
            $scope.newUser.password = '';
            $scope.availableRoles.forEach(function(roleName){
                    $scope.newUser.roles[roleName] = false;
            });
            if($scope.users.length < $scope.itemsPerPage){
                createdUser = {
                    userId: userCreatedResponse.userId,
                    name: name,
                    password: password
                };
                createdUser.roles = rolesNames.map(rn=>{ return {name: rn}});
                rolesNames.forEach(function(roleName){
                    $scope.newRoles[createUser.userId][roleName] = true;
                });
                $scope.users.push(createdUser);
            }
            else if($scope.totalPages == $scope.currentPage){
                $scope.totalPages+=1;
            }
        });
    }
    $scope.isItMe = function(user){//метод для проверки является ли переданный пользователь, тем, под которым вошли
        return user.name === AuthService.getMyUserName();
    }
    $scope.hasRole = function (user, roleName) {//метод для проверки наличия роли у пользователя(роль ищется по имени)
         return user.roles.map(r=>r.name).includes(roleName);
    };
    $scope.updateRoles = function(user){//метод для обновления состояния о том, изменён ли пользователь
        countOfDifferences = 0;
        for(var i = 0; i < $scope.availableRoles.length; i++){
            roleName = $scope.availableRoles[i];
            if($scope.hasRole(user, roleName) != $scope.newRoles[user.userId][roleName]){
                countOfDifferences++;
            }
        }
        $scope.newRoles[user.userId].isChanged = countOfDifferences > 0;
    };
    $scope.applyChanges = function(user){//метод для обработки кнопки "применить изменения"
        newPrivileges = $scope.availableRoles.filter(r=>$scope.newRoles[user.userId][r]);
        $scope.newRoles[user.userId]
        UserService.updateRoles(user.userId, newPrivileges)
            .then(function(){
                user.roles = newPrivileges.map(rn=>{ return {name: rn}});
                $scope.newRoles[user.userId] = {};
                $scope.newRoles[user.userId].isChanged = false;
                setRolesData(user.userId, newPrivileges);
            });
    };
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function() {
      $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    if($scope.canAdmin())$scope.getPage(1);
});