//данный файл содержит необходимый код для авторизации
// Определение контроллера AuthController
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}
function deleteCookie(name) {
  document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}
app.service('AuthService',  function($http){
    var token = getCookie('token');
     function getTokenPayload(){
          const payloadBase64 = token.split('.')[1];
          const payloadJson = atob(payloadBase64);
          return JSON.parse(payloadJson);
     };
    return {
        getToken: function(){
            return token;
        },
        isValidToken: function(){
            if(!token)return false;
            else{
                const payload = getTokenPayload();
                const currentTime = Math.floor(Date.now() / 1000);
                return payload.exp > currentTime; // Если время истечения токена больше текущего времени, то считаем его действительным
            }
        },
        login: function(credentials){
            return $http.post('/blog/api/v1/auth', credentials).then(function(response){
                // В ответе сервера должен быть токен, который вы сохраняете в переменной $scope.token
                token = response.data.token;
                // Раскодировать полезную нагрузку (payload) токена
                const payloadBase64 = token.split('.')[1];
                const payloadJson = atob(payloadBase64);
                const payload = JSON.parse(payloadJson);
                // Получить дату истечения токена из поля 'exp' и преобразовать в дату
                const expirationDate = new Date(payload.exp * 1000); // Множим на 1000, т.к. 'exp' в секундах, а new Date() ожидает миллисекунды
                // Установить куку с временем истечения
                document.cookie = `token=${token}; expires=${expirationDate.toUTCString()}; path=/`;
                return token;
            });
        },
        removeToken: function(){
            token = null;
            deleteCookie('token');
        }
    };
});
app.controller('AuthController', function($scope, AuthService) {
  $scope.credentials = {
    username: '',
    password: ''
  };
  $scope.error = null;
  $scope.isValidToken = AuthService.isValidToken;
  $scope.login = function() {
    $scope.error = null; // Сбрасываем предыдущую ошибку
    // Предполагаем, что на сервере у вас есть маршрут для аутентификации и получения токена
    // Здесь отправляем POST-запрос с данными авторизации
    AuthService.login($scope.credentials).catch(function(error) {
        console.error('Ошибка авторизации:', error);
        if(error.status == 401)$scope.error = 'Ошибка авторизации. Пожалуйста, проверьте правильность введенных данных.';
        else $scope.error = 'Неизвестная ошибка при авторизации.'
    });
  };
});
