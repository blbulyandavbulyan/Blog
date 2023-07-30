app.service('UserService', function($http){
    const usersApiPath = contextPath + '/api/v1/users';
    return {
        getUserProfile: function(username){
            return $http.get(`${usersApiPath}/${username}`);
        },
        //получение информации о пользователях для администраторов
        getUserInfoAboutAllUsers: function(pageNumber, pageSize){
            return $http.get(`${usersApiPath}/info/all`);
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