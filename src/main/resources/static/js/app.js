const app = angular.module('blog', ['ngRoute']);
app.config(function ($routeProvider) {
    $routeProvider
        .when('/articles', {
            templateUrl: 'articlesList.html',
        })
        .when('/articles/:articleId', {
            templateUrl: 'article.html',
        })
        .when('/new_article', {
            templateUrl: 'publish_article.html'
        })
        .when("/user_control",{
            templateUrl: 'users.html'
        })
        .when('/auth', {
            templateUrl: 'auth-form.html'
        })
        .otherwise({redirectTo: '/articles'});
});
// Определение интерцептора
const contextPath = window.location.origin + '/blog';

function calculatePageNumbers(currentPage, totalPages, maxPagesToShow) {
    const pageNumbers = [];
    if (totalPages <= maxPagesToShow) {
        for (let i = 1; i <= totalPages; i++) {
            pageNumbers.push(i);
        }
    } else {
        let startPage;
        let endPage;
        if (currentPage <= Math.ceil(maxPagesToShow / 2)) {
            startPage = 1;
            endPage = maxPagesToShow;
        } else if (currentPage >= totalPages - Math.floor(maxPagesToShow / 2)) {
            startPage = totalPages - maxPagesToShow + 1;
            endPage = totalPages;
        } else {
            startPage = currentPage - Math.floor(maxPagesToShow / 2);
            endPage = currentPage + Math.floor(maxPagesToShow / 2);
        }
        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(i);
        }
    }
    return pageNumbers;
}

function generatePageParamsAndFilterParams(filterParams, pageNumber, pageSize) {
    const httpParams = {
        p: pageNumber,
        s: pageSize
    };
    Object.keys(filterParams).forEach(function (key) {
        let value = filterParams[key];
        if (value === 'string') {
            value = value.trim();
        }
        if (value !== '') {
            httpParams[key] = value;
        }
    });
    return httpParams;
}