app.service('UserService', function ($http) {
    const usersApiPath = contextPath + '/api/v1/users';
    return {
        getUserProfile: function (username) {
            return $http.get(`${usersApiPath}/${username}`);
        },
        //получение информации о пользователях для администраторов
        getUserInfoAboutAllUsers: function (filterParams, pageNumber, pageSize) {
            const httpParams = generatePageParamsAndFilterParams(filterParams, pageNumber, pageSize);
            const httpQuery = {
                method: 'GET',
                url: `${usersApiPath}`
            };
            httpQuery["params"] = httpParams;
            return $http(httpQuery);
        },
        createUser: function (username, password, rolesNames) {
            const createUserRequest = {
                name: username,
                password: password,
                roleNames: rolesNames
            };
            return $http.post(usersApiPath, createUserRequest);
        },
        deleteUserById: function (userId) {
            return $http.delete(`${usersApiPath}/${userId}`);
        },
        updateRoles: function (userId, rolesNames) {
            const httpData = {
                userId: userId,
                rolesNames: rolesNames
            };
            return $http.patch(`${usersApiPath}/roles`, httpData);
        },
        changePassword: function (password){
            return $http.patch(`${usersApiPath}/password`, null, {params: {password: password}});
        }
    }
});
app.controller('PasswordChangeController', function ($scope, UserService){
    $scope.password = '';
    $scope.confirmPassword = '';
    $scope.apply = function (){
        if($scope.password === $scope.confirmPassword && $scope.password !== '') {
            UserService.changePassword($scope.password);
            $scope.reset();
        }
    }
    $scope.reset = function (){
        $scope.password = '';
        $scope.confirmPassword = '';
    }
})
app.controller('UserController', function ($scope, $timeout, UserService, AuthService, RoleService) {
    $scope.users = [];
    $scope.newRoles = {};//в этом объекте, по ИД пользователя, будет храниться: изменён он или нет, и какие роли у него установлены
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
    $scope.contentLoading = false;//показывает, загружаются ли пользователи
    $scope.loadingError = null;//содержит текст ошибки загрузки, если таковая возникла
    $scope.newUser = {
        name: "",
        password: ""
    };// определяем модель для формы создания пользователя
    $scope.newUser.roles = {};//инициализируем роли для нового пользователя пустым массивом
    $scope.availableRoles.forEach(function (roleName) {
        $scope.filter.roles[roleName] = false;//по умолчанию выключены требования всех ролей
        $scope.newUser.roles[roleName] = false;//выключаем требования всех ролей для создания пользователей
    });
    $scope.filterUsers = function () {//метод для применения фильтра по пользователям
        let rolesForFilter = $scope.availableRoles.filter(roleName => $scope.filter.roles[roleName]);
        $scope.filterParams = {
            name: $scope.filter.name,
            roles: rolesForFilter
        };
        $scope.getPage(1);
    }
    $scope.resetFilter = function (){
        $scope.filterParams = {};
        $scope.filter = {};
        $scope.getPage(1);
    }
    function setRolesData(userId, rolesNames) {
        $scope.availableRoles.forEach(function (roleName) {
            $scope.newRoles[userId][roleName] = rolesNames.includes(roleName);
        });
    }

    $scope.loadUsersInfo = function (filterParams, pageNumber) {//метод для получения информации о пользователях
        $scope.contentLoading = true;
        UserService.getUserInfoAboutAllUsers(filterParams, pageNumber, $scope.itemsPerPage)
            .then(function (response) {
                $scope.users = response.data.content;
                $scope.newRoles = {};
                $scope.users.forEach(function (user) {
                    $scope.newRoles[user.userId] = {};
                    let rolesNames = user.roles.map(r => r.name);
                    setRolesData(user.userId, rolesNames)
                    $scope.newRoles[user.userId].isChanged = false;
                });
                $scope.totalPages = response.data.totalPages;
                $scope.currentPage = pageNumber;
                $timeout(function () {
                    $scope.contentLoading = false;
                    $scope.loadingError = null;
                }, 300);
            })
            .catch(function (error){
                $scope.contentLoading = false;
                $scope.loadingError = 'Ошибка загрузки'
                console.log(error);
            });
    };
    $scope.getPage = function (pageNumber) {//метод для получения заданной страницы
        $scope.loadUsersInfo($scope.filterParams, pageNumber);
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    };
    $scope.deleteUser = function (userId) {//метод для обработки кнопки удаления пользователя
        UserService.deleteUserById(userId)
            .then(function (response) {
                const index = $scope.users.findIndex(user => user.userId === userId);
                // Удаляем пользователя с найденным индексом
                if (index !== -1) {
                    $scope.users.splice(index, 1);
                }
                //TODO добавить здесь обработку ситуации, когда это был последний пользователь на странице и при этом, страницы ещё есть
                //здесь не учтено, что мы можем удалить пользователя с текущей странице, а на следующей странице был только один пользователь
                //и тогда этой следующей страницы уже не будет
                let pageToLoad;
                if ($scope.users.length === 0 && $scope.totalPages > 1) {//Массив с пользователями стал пустым
                    //для первой страницы мы просто заново будем загружать первую страницу (но уже с учётом того что это будет как бы другая страница)
                    //для всех последующий страниц, мы просто будем загружать предыдущую страницу, т.к. следующей может не быть
                    pageToLoad = $scope.currentPage === 1 ? 1 : $scope.currentPage - 1;
                    $scope.getPage(pageToLoad);
                }
            });
        //TODO написать обработку ошибки удаления пользователя
    };
    $scope.createUser = function () {
        let name = $scope.newUser.name;
        let password = $scope.newUser.password;
        let rolesNames = $scope.availableRoles.filter(roleName => $scope.newUser.roles[roleName]);
        UserService.createUser(name, password, rolesNames)
            .then(function (response) {
                let userCreatedResponse = response.data;
                $scope.newUser.name = '';
                $scope.newUser.password = '';
                $scope.availableRoles.forEach(function (roleName) {
                    $scope.newUser.roles[roleName] = false;
                });
                if ($scope.users.length < $scope.itemsPerPage) {//в случае если количество элементов на текущей странице меньше чем максимальное количество элементов на странице
                    //то мы просто вставляем пользователя на эту страницу
                    let createdUser = {
                        userId: userCreatedResponse.userId,
                        name: name,
                        password: password
                    };
                    createdUser.roles = rolesNames.map(rn => {
                        return {name: rn}
                    });
                    $scope.newRoles[createdUser.userId] = {}
                    rolesNames.forEach(function (roleName) {
                        $scope.newRoles[createdUser.userId][roleName] = true;
                    });
                    $scope.users.push(createdUser);
                } else if ($scope.totalPages === $scope.currentPage) {//в случае если мы находимся на последней странице и на ней уже максимальное количество элементов
                    //увеличиваем количество страниц
                    $scope.totalPages += 1;
                }
                $scope.userCreationError = null;
            })
            .catch(function (error) {
                $scope.userCreationError = "Ошибка создания пользователя!";
                console.log(error);
            })
        //TODO написать обработку ошибки создания пользователя
    }
    $scope.isItMe = function (user) {//метод для проверки является ли переданный пользователь, тем, под которым вошли
        return user.name === AuthService.getMyUserName();
    }
    $scope.hasRole = function (user, roleName) {//метод для проверки наличия роли у пользователя(роль ищется по имени)
        return user.roles.map(r => r.name).includes(roleName);
    };
    $scope.updateRoles = function (user) {//метод для обновления состояния о том, изменёны ли роли у пользователя
        let countOfDifferences = 0;
        let roleName;
        for (let i = 0; i < $scope.availableRoles.length; i++) {
            roleName = $scope.availableRoles[i];
            if ($scope.hasRole(user, roleName) !== $scope.newRoles[user.userId][roleName]) {
                countOfDifferences++;
            }
        }
        $scope.newRoles[user.userId].isChanged = countOfDifferences > 0;
    };
    $scope.applyChanges = function (user) {//метод для обработки кнопки "применить изменения"
        let newPrivileges = $scope.availableRoles.filter(r => $scope.newRoles[user.userId][r]);
        UserService.updateRoles(user.userId, newPrivileges)
            .then(function () {
                user.roles = newPrivileges.map(rn => {
                    return {name: rn}
                });
                $scope.newRoles[user.userId] = {};
                $scope.newRoles[user.userId].isChanged = false;
                setRolesData(user.userId, newPrivileges);
            });
        //TODO написать обработку ошибок обновления пользователя
    };
    // Обработчик изменения общего количества страниц (возможно, при загрузке данных с сервера)
    $scope.$watch('totalPages', function () {
        $scope.pageNumbers = calculatePageNumbers($scope.currentPage, $scope.totalPages, maxPagesToShow);
    });
    if ($scope.canAdmin()) $scope.getPage(1);
});