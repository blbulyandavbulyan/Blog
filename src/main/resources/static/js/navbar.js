app.controller('NavbarController', function($scope, $location, RoleService){
    $scope.showPostArticleItem = ()=> RoleService.isPublisher();
    $scope.canAdministrate = ()=> RoleService.isAdmin();
    $scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };
});