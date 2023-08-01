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
                url: ${usersApiPath}/info/all`
            }
            httpQuery["params"] = httpQuery;
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