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
            return $http.get(`${commentReactionsApiPath}/${commentId}`);
        }
    }
});