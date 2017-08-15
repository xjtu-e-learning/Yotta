var leftApp = angular.module("leftApp", ['ui.bootstrap', 'ngAnimate']);
/**
 * 数据源显示
 */
leftApp.controller('sourceCtrl', function($scope, $uibModal, $http) {

    $http({
        url : 'http://'+ip+'/source/getSources',
        method : 'get',
    }).success(function(response) {
        $scope.sources = response.data;
        if (response.code == 200) {
            console.log("获取数据源信息成功，code为：" + response.code + "，msg为：" + response.msg);
            var baike = new Array();
            var wenda = new Array();
            var boke = new Array();
            var a = 0, b = 0, c = 0;
            for (var i = 0; i < response.data.length; i++) {
                var source = response.data[i];
                // console.log(i + "->" + source.sourceType);
                if (source.sourceType == "百科类") {
                    baike[a++] = source;
                } else if (source.sourceType == "问答类") {
                    wenda[b++] = source;
                } else if (source.sourceType == "博客类") {
                    boke[c++] = source;
                }
            }
            $scope.baike = baike;
            $scope.wenda = wenda;
            $scope.boke = boke;
            console.log(response.data);
            console.log("百科类：" + $scope.baike);
            console.log("问答类：" + $scope.wenda);
            console.log("博客类：" + $scope.boke);
        } else {
            console.log("获取数据源信息失败，code为：" + response.code + "，msg为：" + response.msg);
        }
    });

});


setCookie("sourceId","1","1000");
setCookie("sourceName","中文维基百科","1000");
setCookie("sourceType","百科类","1000");
// alert("sourceId is " + getCookie("sourceId") +
//     "sourceName is " + getCookie("sourceName") +
//     "sourceType is " + getCookie("sourceType"));

var rightApp = angular.module("rightApp", ['ui.bootstrap', 'ngAnimate']);

/**
 * 领域分页查询显示
 */
rightApp.controller('domainCtrl', function($scope, $uibModal, $http) {

    $scope.sourceId = getCookie("sourceId");
    $scope.sourceName = getCookie("sourceName");
    $scope.sourceType = getCookie("sourceType");

    var ascOrder = true;
    $scope.currentPage = 1; // 当前页
    $scope.numPerPage = 3; // 每页显示的条数
    $scope.maxSize = 100;
    $http({
        // url : 'http://'+ip+'/domain/getDomainBySourceID?sourceId='+getCookie("sourceId"),
        url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page='
                + $scope.currentPage + '&size=' + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + getCookie("sourceId"),
        method : 'get',
    }).success(function(response) {
        $scope.totalItems = response.data.totalElements; // 记录的总条数
        $scope.domains = response.data.content;
        if (response.code == 200) {
            console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        } else {
            console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
        }
    });

    // 页面切换时使用
    $scope.pageChanged = function() {
        $http({
            url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page='
            + $scope.currentPage + '&size=' + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + getCookie("sourceId"),
            method : 'get',
        }).success(function(response) {
            $scope.totalItems = response.data.totalElements;
            $scope.domains = response.data.content;
            if (response.code == 200) {
                console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
                console.log(response.data);
            } else {
                console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
            }
        });
    }


});


