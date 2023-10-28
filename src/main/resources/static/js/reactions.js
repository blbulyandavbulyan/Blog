app.service('ArticleReactionsService', function ($http) {
    const articleReactionsApiPath = `${contextPath}/api/v1/reactions/article`
    return {
        react: function (articleId, liked) {
            return $http.post(articleReactionsApiPath, {
                articleId: articleId,
                liked: liked
            });
        },
        removeReaction: function (articleId) {
            return $http.delete(`${articleReactionsApiPath}/${articleId}`);
        },
        getStatistics: function (articleId){
            return $http.get(`${articleReactionsApiPath}/statistics/${articleId}`);
        },
        getMyReaction: function (articleId){
            return $http.get(`${articleReactionsApiPath}/${articleId}`);
        }
    }
});
app.service('CommentReactionService', function ($http) {
    const commentReactionsApiPath = `${contextPath}/api/v1/reactions/comment`;
    return {
        react: function (commentId, liked){
            return $http.post(commentReactionsApiPath, {
                commentId: commentId,
                liked: liked
            });
        },
        removeReaction(commentId){
            return $http.delete(`${commentReactionsApiPath}/${commentId}`);
        },
        getStatistics: function (commentId){
            return $http.get(`${commentReactionsApiPath}/statistics/${commentId}`);
        },
        getMyReaction: function (commentId){
            return $http.get(`${commentReactionsApiPath}/${commentId}`);
        }
    }
});
app.controller('ArticleReactionController', function ($scope, $routeParams, ArticleReactionsService, RoleService){
    $scope.liked = null;
    $scope.reactionStatistics = {
        likesCount: 0,
        dislikesCount: 0
    }
    if($routeParams.articleId) {
        ArticleReactionsService.getStatistics($routeParams.articleId)
            .then(
                function (response) {
                    const data = response.data;
                    $scope.reactionStatistics.likesCount = data.likesCount;
                    $scope.reactionStatistics.dislikesCount = data.dislikesCount;

                }
            );
        ArticleReactionsService.getMyReaction($routeParams.articleId)
            .then(
                function (response) {
                    const reaction = response.data;
                    $scope.liked = reaction.hasReaction ? reaction.liked : null;
                }
            );
    }
    $scope.canReact = ()=> RoleService.canReactOnArticle();
    $scope.setLiked = (liked)=>{
        const articleId = $routeParams.articleId;
        if($scope.liked === liked){
            //удаляем реакцию на эту статью
            ArticleReactionsService.removeReaction(articleId)
                .then(function (){
                    if($scope.liked)$scope.reactionStatistics.likesCount--;
                    else $scope.reactionStatistics.dislikesCount--;
                    $scope.liked = null;
                });
        }
        else{
            ArticleReactionsService.react(articleId, liked)
                .then(function (){
                    if($scope.liked != null){
                        if($scope.liked && $scope.reactionStatistics.likesCount > 0)
                            $scope.reactionStatistics.likesCount--;
                        else if($scope.reactionStatistics.dislikesCount > 0)
                            $scope.reactionStatistics.dislikesCount--;
                    }
                    if(liked)$scope.reactionStatistics.likesCount++;
                    else $scope.reactionStatistics.dislikesCount++;
                    $scope.liked = liked;
                });
        }
    }
});