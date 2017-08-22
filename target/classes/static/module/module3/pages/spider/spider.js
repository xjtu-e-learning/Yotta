var myApp = angular.module("myApp", ['ui.bootstrap', 'ngAnimate']);
var sourceId = "";



// $(function () {
//     $("#example1").DataTable();
// });

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
        method : 'get'
    }).success(function(response) {
        $scope.totalItems = response.data.totalElements; // 记录的总条数
        $scope.domains = response.data.content;
        console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
        console.log(response.data);
    }).error(function(response){
        alert("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
            "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
        console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
            "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
    });

    // 页面切换时使用
    $scope.pageChanged = function() {
        $http({
            url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page='
            + $scope.currentPage + '&size=' + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
            method : 'get'
        }).success(function(response) {
            $scope.totalItems = response.data.totalElements;
            $scope.domains = response.data.content;
            console.log("获取领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
            console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
        });
    };

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
                "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
            console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
            $scope.sourceName = oldSourceName;
            $scope.sourceType = oldSourceType;
        });
    };

    // 添加领域信息 模态框
    $scope.openModalInsertDomain = function() {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalInsertDomain.html',//script标签中定义的id
            controller : 'modalCtrlmodalInsertDomain',//modal对应的Controller
            resolve : {
                sourceId : function() {//data作为modal的controller传入的参数
                    return sourceId;//用于传递数据
                },
                sourceName : function() {//data作为modal的controller传入的参数
                    return $scope.sourceName;//用于传递数据
                }
            }
        })
    };

    // 领域信息详情 模态框
    $scope.openModalDomainDetail = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalDomainDetail.html',//script标签中定义的id
            controller : 'modalCtrlmodalDomainDetail',//modal对应的Controller
            resolve : {
                domainId : function() {//data作为modal的controller传入的参数
                    console.log($scope.domains[$index]);
                    console.log($index);
                    return $scope.domains[$index].domainId;//用于传递数据
                },
                domainName : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].domainName;//用于传递数据
                },
                domainUrl : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].domainUrl;//用于传递数据
                },
                domainNote : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].note;//用于传递数据
                },
                sourceId : function() {//data作为modal的controller传入的参数
                    return sourceId;//用于传递数据
                },
                sourceName : function() {//data作为modal的controller传入的参数
                    return $scope.sourceName;//用于传递数据
                },
                sourceType : function() {//data作为modal的controller传入的参数
                    return $scope.sourceType;//用于传递数据
                }
            }
        })
    };

    // 修改领域信息 模态框
    $scope.openModalDomainModify = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalDomainModify.html',//script标签中定义的id
            controller : 'modalCtrlmodalDomainModify',//modal对应的Controller
            resolve : {
                domainId : function() {//data作为modal的controller传入的参数
                    console.log($scope.domains[$index]);
                    console.log($index);
                    return $scope.domains[$index].domainId;//用于传递数据
                },
                domainName : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].domainName;//用于传递数据
                },
                domainUrl : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].domainUrl;//用于传递数据
                },
                domainNote : function() {//data作为modal的controller传入的参数
                    return $scope.domains[$index].note;//用于传递数据
                },
                sourceName : function() {//data作为modal的controller传入的参数
                    return $scope.sourceName;//用于传递数据
                }
            }
        })

    };

});


// 领域插入 模态框对应的Controller
myApp.controller('modalCtrlmodalInsertDomain', function($scope, $http, $uibModalInstance, sourceId, sourceName) {

    $scope.sourceName = sourceName;

    //在这里处理要进行的操作
    $scope.ok = function() {
        $http({
            // http://localhost:8080/domain/insertDomain?domainId=11&domainName=aaa&domainUrl=aa&note=aa&sourceId=3
            url : 'http://' + ip + '/domain/insertDomain?domainName=' + $scope.insertDomainName + '&domainUrl='
            + $scope.insertDomainUrl + '&note=' + $scope.insertDomainNote + '&sourceId=' + sourceId,
            method : 'get'
        }).success(function(response) {
            alert("插入成功");
            console.log("插入领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("插入领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("插入领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});


// 领域详情 模态框对应的Controller
myApp.controller('modalCtrlmodalDomainDetail', function($scope, $http, $uibModalInstance,
                domainId, domainName, domainUrl, domainNote, sourceId, sourceName, sourceType) {

    $http({
        url : 'http://' + ip + '/topic/getTopicByDomainId?domainId=' + domainId,
        method : 'get'
    }).success(function(response) {
        $scope.detailTopic = "主题已爬取，主题数为：" + response.data; // 记录的总条数
        $scope.detailTopicSpiderDisabled = "disabled";
        console.log("领域下的主题数据已经爬取，code为：" + response.code + "，msg为：" + response.msg);
    }).error(function(response){
        $scope.detailTopic = "主题未爬取，需要重新爬取";
        $scope.detailTopicSpiderDisabled = "";
        console.log("领域下的主题数据还没有爬取，code为：" + response.code + "，msg为：" + response.msg);
    });


    $scope.detailDomainId = domainId;
    $scope.detailDomainName = domainName;
    $scope.detailDomainUrl = domainUrl;
    $scope.detailDomainNote = domainNote;
    $scope.detailSourceId = sourceId;
    $scope.detailSourceName = sourceName;
    $scope.detailSourceType = sourceType;

    //在这里处理要进行的操作
    $scope.ok = function() {
        $uibModalInstance.close();
    };

    // 点击“关闭”按钮
    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    };

    // 点击“爬取主题”后进行主题爬取
    $scope.spiderTopicByDomainId = function() {
        alert("开始爬取主题");
        $http({
            url : 'http://' + ip + '/topic/storeTopicByDomainId?domainId=' + domainId,
            method : 'get'
        }).success(function(response) {
            alert("爬取主题成功");
            console.log("领域下的主题数据爬取成功，code为：" + response.code + "，msg为：" + response.msg + "，主题信息为：" + response.data);
        }).error(function(response){
            alert("爬取主题失败");
            console.log("领域下的主题数据爬取失败，code为：" + response.code + "，msg为：" + response.msg);
        });
    }

});

// 领域修改 模态框对应的Controller
myApp.controller('modalCtrlmodalDomainModify', function($scope, $http, $uibModalInstance,
                                    domainId, domainName, domainUrl, domainNote, sourceName) {

    $scope.detailDomainId = domainId;
    $scope.detailDomainName = domainName;
    $scope.detailDomainUrl = domainUrl;
    $scope.detailDomainNote = domainNote;
    temp = domainSourceNameModify(sourceName);
    $("#eeee").value = new String(temp);
    console.log(temp);

    //在这里处理要进行的操作
    $scope.ok = function() {
        $http({
            // http://localhost:8080/domain/insertDomain?domainId=11&domainName=aaa&domainUrl=aa&note=aa&sourceId=3
            url : 'http://' + ip + '/domain/insertDomain?domainName=' + $scope.insertDomainName + '&domainUrl='
            + $scope.insertDomainUrl + '&note=' + $scope.insertDomainNote + '&sourceId=' + sourceId,
            method : 'get',
        }).success(function(response) {
            alert("插入成功");
            console.log("插入领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("插入领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("插入领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});



