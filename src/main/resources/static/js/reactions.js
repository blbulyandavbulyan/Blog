app.service('ArticleReactionsService', function ($http) {
    const articleReactionsApiPath = `${contextPath}/api/v1/reactions/articles`
    return {
        react: function (articleId, liked) {
            return $http.post(articleReactionsApiPath, {
                articleId: articleId,
                liked: liked
            });
        },
        removeReaction: function (articleId) {
            return $http.delete(`${articleReactionsApiPath}/${articleId}`);
        }
    }
});
app.service('CommentReactionService', function ($http) {
    const commentReactionsApiPath = `${contextPath}/api/v1/reactions/comments`;
    return {
        react: function (commentId, liked){
            return $http.post(commentReactionsApiPath, {
                commentId: commentId,
                liked: liked
            });
        },
        removeReaction(commentId){
            return $http.delete(`${commentReactionsApiPath}/${commentId}`);
        }
    }
});