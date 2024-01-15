//данный файл содержит необходимый код для авторизации
app.service('CookieService', function () {
    return {
        getCookie: function (name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
        },
        deleteCookie: function (name) {
            document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        },
        setCookie: function (name, value, expirationDate) {
            document.cookie = `${name}=${value}; expires=${expirationDate.toUTCString()}; path=/`;
        }
    };
})
app.service('TokenService', function (CookieService) {
    let token = null;
    let tokenPayload = null;

    function init(newToken) {
        token = newToken;
        if (token) {
            // Раскодировать полезную нагрузку (payload) токена
            const payloadBase64 = token.split('.')[1];
            const payloadJson = atob(payloadBase64);
            tokenPayload = JSON.parse(payloadJson);
        }
    }

    init(CookieService.getCookie('token'));
    return {
        setToken: function (newToken) {
            init(newToken);
            // Получить дату истечения токена из поля 'exp' и преобразовать в дату
            const expirationDate = new Date(tokenPayload.exp * 1000); // Множим на 1000, т.к. 'exp' в секундах, а new Date() ожидает миллисекунды
            // установить куку с временем истечения
            CookieService.setCookie('token', token, expirationDate);
        },
        getToken: function () {
            return token;
        },
        isValidToken: function () {
            if (!token) return false;
            else {
                const currentTime = Math.floor(Date.now() / 1000);
                return tokenPayload.exp > currentTime; // Если время истечения токена больше текущего времени, то считаем его действительным
            }
        },
        removeToken: function () {
            token = null;
            tokenPayload = null;
            CookieService.deleteCookie('token');
        },
        getTokenPayload: function () {
            return tokenPayload;
        },
        getMyUserName: function () {
            if (tokenPayload) {
                return tokenPayload["sub"];
            } else throw Error('Нет токена!')
        }
    };
})
app.service('AuthService', function ($http, TokenService) {
    const authUrl = '/blog/api/v1/auth';
    let tfaToken = null;
    return {
        login: function (credentials) {
            return $http.post(authUrl, credentials).then(function (response) {
                // В ответе сервера должен быть токен, который вы сохраняете в переменной $scope.token
                const data = response.data;
                const token = data.token;
                const tfaRequired = data.tfaRequired;
                if(!tfaRequired) {
                    TokenService.setToken(token);
                }
                else {
                    tfaToken = token;
                }
                return tfaRequired;
            });
        },
        verifyAuth: function (code) {
            return $http.post(`${authUrl}/verify`, {
                jwtToken: tfaToken,
                code: code
            }).then(function (response) {
                const data = response.data;
                const token = data.token;
                const tfaRequired = data.tfaRequired;
                if(!tfaRequired) {
                    TokenService.setToken(token);
                }
                else {
                    console.error("TFA still required! This behavior is not supported")
                }
            });
        },
        isAuthenticated: TokenService.isValidToken,
        logout: function () {
            TokenService.removeToken();
        },
        getMyUserName: function () {
            return TokenService.getMyUserName();
        }
    };
});
app.service('TfaSettingsService', function ($http, AuthService) {
    const tfaURL = "/blog/api/v1/tfa";
    return {
        beginTfaSetup: function () {
            return $http({
                method: 'POST',
                url: tfaURL
            });
        },
        finishTFASetup: function (code) {
            return $http({
                method: 'PATCH',
                url: tfaURL,
                params: {
                    code: code
                }
            });
        },
        isTfaEnabled: function () {
            return $http({
                method: 'GET',
                url: `${tfaURL}/${AuthService.getMyUserName()}`
            });
        },
        disableTfa: function (code) {
            return $http({
                method: 'DELETE',
                url: tfaURL,
                params: {
                    code: code
                }
            });
        }
    };
})
app.factory('authInterceptor', ['$injector', '$q', function ($injector, $q) {
    const tokenService = $injector.get('TokenService');
    return {
        request: function (config) {
            // Ваш код интерцептора
            if (tokenService.isValidToken()) {
                config.headers['Authorization'] = 'Bearer ' + tokenService.getToken();
            }
            return config;
        },
        responseError: function (rejection) {
            // Проверить, является ли ошибка ошибкой 401 (Unauthorized)
            if (rejection.status === 401) {
                // Если токен существует и его время действия истекло, удалить его из cookie
                if (tokenService.isValidToken()) {
                    tokenService.removeToken();
                }
            }
            // Продолжить обработку ошибки
            return $q.reject(rejection);
        }
    };
}]);
// Добавление интерцептора к конфигурации $httpProvider
app.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
});
app.controller('AuthController', function ($scope, $timeout, AuthService) {
    const authFormModal = bootstrap.Modal.getOrCreateInstance("#loginModal", {});
    const authVerificationModal = bootstrap.Modal.getOrCreateInstance("#authVerifyModal", {});
    $scope.requestProcessed = false;
    $scope.credentials = {
        username: '',
        password: ''
    };
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.login = function () {
        // Предполагаем, что на сервере у вас есть маршрут для аутентификации и получения токена
        // Здесь отправляем POST-запрос с данными авторизации
        $scope.requestProcessed = true;
        AuthService.login($scope.credentials)
            .then((isTfaRequired) =>
                $timeout(function () {
                    authFormModal.hide();
                    $scope.requestProcessed = false;
                    $scope.credentials.username = '';
                    $scope.credentials.password = '';
                    if(isTfaRequired){
                        $timeout(function () {
                            authVerificationModal.show()
                        }, 300);
                    }
                }, 300))
            .catch(function (error) {
                $timeout(function () {
                    $scope.requestProcessed = false;
                    if (error.status === 401) showErrorToast("Ошибка авторизации", "Пожалуйста, проверьте правильность введенных данных");
                    else {
                        showErrorToast("Ошибка авторизации", "Неизвестная ошибка!");
                        console.error('Ошибка авторизации:', error);
                    }
                }, 300);
            });
    };
    $scope.logout = AuthService.logout;
});
app.controller('AuthVerificationController', function ($scope, $timeout, AuthService) {
    const authVerificationModal = bootstrap.Modal.getOrCreateInstance("#authVerifyModal", {});
    $scope.verificationCode = "";
    $scope.requestProcessed = false;
    $scope.verifyAuth = function () {
        $scope.requestProcessed = true;
        AuthService.verifyAuth($scope.verificationCode).then(() => {
            $timeout(function () {
                $scope.verificationCode = "";
                $scope.requestProcessed = false;
                authVerificationModal.hide();
            }, 300)
        }).catch(function (error) {
            $timeout(function () {
                $scope.requestProcessed = false;
                if (error.status === 401) {
                    showErrorToast("Ошибка верификации", "Неверный проверочный код!");
                }
                else {
                    showErrorToast(error.data.message);
                }
            }, 300);
        });
    };
});
app.controller('TFASettingsController', function ($scope, $timeout, TfaSettingsService, AuthService) {
    $scope.tfaEnabled = false;
    $scope.enableCheckboxStateTFA = $scope.tfaEnabled;
    $scope.verificationCode = "";
    $scope.qrCode = null;
    $scope.requestProcessed = false;
    $scope.updateTfaEnabled = function () {
        if (AuthService.isAuthenticated()) {
            TfaSettingsService.isTfaEnabled().then(function (response) {
                $scope.tfaEnabled = response.data.enabled;
                $scope.enableCheckboxStateTFA = $scope.tfaEnabled;
            }).catch(function (error) {
                $timeout(function () {
                    let message = `Статус код ${error.status}.`;
                    if (error.data && error.data.message) {
                        message = `${message} ${error.data.message}`;
                    }
                    showErrorToast("Ошибка получения статуса TFA", message);
                }, 300);
            });
        }
    };
    $scope.switchTfaEnabled = function () {
        if (!$scope.tfaEnabled && $scope.enableCheckboxStateTFA) {
            $scope.requestProcessed = true;
            TfaSettingsService.beginTfaSetup()
                .then(function (response) {
                    $timeout(function () {
                        $scope.qrCode = response.data.qrCode;
                        $scope.requestProcessed = false;
                    }, 300);
                }).catch(function (error) {
                    $timeout(function () {
                        console.error(error);
                        $scope.requestProcessed = false;
                        showErrorToast(error.data.message);
                    }, 300);
                });
        }
    };
    $scope.configure = function () {
        if ($scope.tfaEnabled !== $scope.enableCheckboxStateTFA) {
            if (!$scope.tfaEnabled) {
                $scope.requestProcessed = true;
                TfaSettingsService.finishTFASetup($scope.verificationCode).then(function () {
                    $timeout(function () {
                        $scope.tfaEnabled = $scope.enableCheckboxStateTFA;
                        $scope.qrCode = null;
                        $scope.verificationCode = "";
                        $scope.requestProcessed = false;
                    }, 300);
                }).catch(function (error) {
                    $timeout(function () {
                        console.error(error)
                        $scope.requestProcessed = false;
                        showErrorToast("Ошибка включения двухфакторной аутентификации!", error.data.message);
                    }, 300);
                });
            }
            else {
                $scope.requestProcessed = true;
                TfaSettingsService.disableTfa($scope.verificationCode)
                    .then(function () {
                        $timeout(function () {
                            $scope.requestProcessed = false;
                            $scope.tfaEnabled = false;
                        }, 300);
                    })
                    .catch(function (error) {
                        $timeout(function () {
                            $scope.requestProcessed = false;
                            if (error.data && error.data.message) {
                                showErrorToast("Ошибка отключения TFA", error.data.message);
                            }
                            else console.error(error);
                        }, 300);
                    });
            }
        }
    };
    $scope.updateTfaEnabled();
})
app.service('RoleService', function (TokenService) {
    return {
        isCommenter: function () {
            if (TokenService.isValidToken()) {
                return TokenService.getTokenPayload().roles.includes('ROLE_COMMENTER');
            }
        },
        isPublisher: function () {
            if (TokenService.isValidToken()) {
                return TokenService.getTokenPayload().roles.includes('ROLE_PUBLISHER');
            }
        },
        isAdmin: function () {
            if (TokenService.isValidToken()) {
                return TokenService.getTokenPayload().roles.includes('ROLE_ADMIN');
            }
        },
        canReactOnArticle: function (){
            return TokenService.isValidToken();
        },
        canReactOnComment: function (){
            return TokenService.isValidToken();
        },
        getAvailableRoles: function () {
            return ['ROLE_COMMENTER', 'ROLE_PUBLISHER', 'ROLE_ADMIN'];
        }
    }
})