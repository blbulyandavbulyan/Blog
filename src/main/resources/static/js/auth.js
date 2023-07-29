//данный файл содержит необходимый код для авторизации
// Определение контроллера AuthController
app.controller('AuthController', function($scope, $http) {
  $scope.credentials = {
    username: '',
    password: ''
  };

  $scope.token = null;
  $scope.error = null;

  $scope.login = function() {
    $scope.error = null; // Сбрасываем предыдущую ошибку

    // Предполагаем, что на сервере у вас есть маршрут для аутентификации и получения токена
    // Здесь отправляем POST-запрос с данными авторизации
    $http.post('/blog/api/v1/auth', $scope.credentials)
      .then(function(response) {
        // В ответе сервера должен быть токен, который вы сохраняете в переменной $scope.token
        $scope.token = response.data.token;
        jwtToken = $scope.token;
        // Раскодировать полезную нагрузку (payload) токена
        const payloadBase64 = jwtToken.split('.')[1];
        const payloadJson = atob(payloadBase64);
        const payload = JSON.parse(payloadJson);
        // Получить дату истечения токена из поля 'exp' и преобразовать в дату
        const expirationDate = new Date(payload.exp * 1000); // Множим на 1000, т.к. 'exp' в секундах, а new Date() ожидает миллисекунды
        // Установить куку с временем истечения
        document.cookie = `token=${jwtToken}; expires=${expirationDate.toUTCString()}; path=/`;
      })
      .catch(function(error) {
        console.error('Ошибка авторизации:', error);
        if(error.status == 401)$scope.error = 'Ошибка авторизации. Пожалуйста, проверьте правильность введенных данных.';
        else $scope.error = 'Неизвестная ошибка при авторизации.'
      });
  };
});