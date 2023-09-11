app.controller('NavbarController', function($scope, RoleService){
    $scope.showPostArticleItem = ()=> RoleService.isPublisher();
    $scope.canAdministrate = ()=> RoleService.isAdmin();
});