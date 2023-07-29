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
      })
      .catch(function(error) {
        console.error('Ошибка авторизации:', error);
        if(error.status == 401)$scope.error = 'Ошибка авторизации. Пожалуйста, проверьте правильность введенных данных.';
        else $scope.error = 'Неизвестная ошибка при авторизации.'
      });
  };
});