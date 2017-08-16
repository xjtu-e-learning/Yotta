var myApp = angular.module("myApp", ['ui.bootstrap', 'ngAnimate']);
var sourceId = "";

$(function () {
    $("#example1").DataTable();
});

/**
 * 领域分页查询显示
 */
myApp.controller('domainCtrl', function($scope, $uibModal, $http) {

    sourceId = "2";
    $scope.sourceName = "英文维基百科";
    $scope.sourceType = "百科类";

    var ascOrder = true;
    $scope.currentPage = 1; // 当前页
    $scope.numPerPage = 5; // 每页显示的条数
    $scope.maxSize = 100;

    $http({
        url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page='
                + $scope.currentPage + '&size=' + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
        method : 'get',
    }).success(function(response) {
        $scope.totalItems = response.data.totalElements; // 记录的总条数
        $scope.domains = response.data.content;
        console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
        console.log(response.data);
    }).error(function(response){
        alert("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
            "，领域ID为：" + sourceId + "，领域名为：" + sourceName);
    });

    // 页面切换时使用
    $scope.pageChanged = function() {
        $http({
            url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page='
            + $scope.currentPage + '&size=' + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
            method : 'get',
        }).success(function(response) {
            $scope.totalItems = response.data.totalElements;
            $scope.domains = response.data.content;
            console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + sourceId + "，领域名为：" + sourceName);
        });
    }

    // 领域切换时使用
    $scope.sourceChanged = function(sourceID, sourceName, sourceType) {
        var oldSourceName = $scope.sourceName;
        var oldSourceType = $scope.sourceType;
        sourceId = sourceID;
        $http({
            url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page=1&size='
            + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
            method : 'get',
        }).success(function(response, status, headers, config) {
            $scope.sourceName = sourceName;
            $scope.sourceType = sourceType;
            console.log("$scope.sourceId: " + sourceId);
            console.log("$scope.sourceName: " + $scope.sourceName);
            console.log("$scope.sourceType: " + $scope.sourceType);
            $scope.totalItems = response.data.totalElements;
            $scope.domains = response.data.content;
            console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + sourceId + "，领域名为：" + sourceName);
            $scope.sourceName = oldSourceName;
            $scope.sourceType = oldSourceType;
        });
    }

});


