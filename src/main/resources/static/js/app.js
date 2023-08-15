var app = angular.module('blog', ['ngRoute']);
app.config(function($routeProvider, $locationProvider) {
  $routeProvider
    .when('/', {
      templateUrl: 'articlesList.html',
    })
    .when('/articles/:articleId', {
      templateUrl: 'article.html',
    })
    .otherwise({ redirectTo: '/' });
//  $locationProvider.html5Mode(true);
});
// Определение интерцептора
const contextPath = window.location.origin + '/blog';
function calculatePageNumbers(currentPage, totalPages, maxPagesToShow) {
  var pageNumbers = [];
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