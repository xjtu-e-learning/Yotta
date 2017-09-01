var myApp = angular.module("myApp", ['ui.bootstrap', 'ngAnimate']);
var sourceId = "";



// $(function () {
//     $("#example1").DataTable();
// });

/**
 * 领域分页查询显示
 */
myApp.controller('domainCtrl', function($scope, $uibModal, $http) {

    sourceId = "1";
    $scope.sourceName = "中文维基百科";
    $scope.sourceType = "百科类";

    var ascOrder = true;
    $scope.currentPage = 1; // 当前页
    $scope.numPerPage = 5; // 每页显示的条数
    $scope.maxSize = 100;

    /**
     * 页面加载时默认显示的数据源下的领域信息
     */
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

    /**
     *  数据源：数据源切换时使用，点击不同数据源显示其下的领域信息
     */
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

    /**
     * 领域：添加领域信息 模态框
     */
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

    /**
     * 领域：领域信息详情 模态框
     */
    $scope.openModalDomainDetail = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalDomainDetail.html',//script标签中定义的id
            controller : 'modalCtrlmodalDomainDetail',//modal对应的Controller
            resolve : {
                domainId : function() { // 领域Id
                    console.log($scope.domains[$index]);
                    console.log($index);
                    return $scope.domains[$index].domainId;
                },
                domainName : function() { // 领域名
                    return $scope.domains[$index].domainName;
                },
                domainUrl : function() { // 领域链接
                    return $scope.domains[$index].domainUrl;
                },
                domainNote : function() { // 领域说明
                    return $scope.domains[$index].note;
                },
                sourceId : function() { // 数据源Id
                    return sourceId;
                },
                sourceName : function() { // 数据源名
                    return $scope.sourceName;
                },
                sourceType : function() { // 数据源类型
                    return $scope.sourceType;
                }
            }
        })
    };

    /**
     * 领域：修改领域信息 模态框
     */
    $scope.openModalDomainModify = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalDomainModify.html',//script标签中定义的id
            controller : 'modalCtrlmodalDomainModify',//modal对应的Controller
            resolve : {
                domainId : function() { // 领域Id
                    console.log($scope.domains[$index]);
                    console.log($index);
                    return $scope.domains[$index].domainId;
                },
                domainName : function() { // 领域名
                    return $scope.domains[$index].domainName;
                },
                domainUrl : function() { // 领域链接
                    return $scope.domains[$index].domainUrl;
                },
                domainNote : function() { // 领域说明
                    return $scope.domains[$index].note;
                },
                sourceName : function() { // 数据源名
                    return $scope.sourceName;
                },
                domain : function() { // 领域对象
                    return $scope.domains[$index];
                }
            }
        })

    };

    /**
     * 领域：删除领域信息
     */
    $scope.domainDelete = function($index) {
        $http({
            url : 'http://' + ip + '/domain/deleteDomain?domainId=' + $scope.domains[$index].domainId,
            method : 'get'
        }).success(function(response) {
            alert("删除领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log("删除领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
        }).error(function(response){
            alert("删除领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + $scope.domains[$index].domainId);
            console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + $scope.domains[$index].domainId);
        });
    };


    /**
     * 领域：显示该领域下的主题信息
     **/
    $scope.domainTopicInfo = function($index) {
        var domain = $scope.domains[$index];
        var oldDomainId = domain.domainId;
        var oldDomainName = domain.domainName;
        $http({
            url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page=1&size='
            + $scope.numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
            method : 'get',
        }).success(function(response) {
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
                                    domainId, domainName, domainUrl, domainNote, sourceName, domain) {

    $scope.detailDomainId = domainId;
    $scope.detailDomainName = domainName;
    $scope.detailDomainUrl = domainUrl;
    $scope.detailDomainNote = domainNote;
    $scope.detailDomainSource = sourceName;
    $scope.detailDomainSourceOpt = ["中文维基百科", "英文维基百科", "百度百科", "知乎", "Quora", "StackOverflow", "CSDN"];

    //在这里处理要进行的操作
    $scope.ok = function() {
        // console.log(domainId);
        // console.log($scope.detailDomainId);
        // console.log($scope.detailDomainName);
        // console.log($scope.detailDomainUrl);
        // console.log($scope.detailDomainNote);
        // console.log($scope.detailDomainSource);
        // 得到更新后的数据源Id
        var sourceId = 1;
        for(var i = 0; i < $scope.detailDomainSourceOpt.length; i++){
            if($scope.detailDomainSourceOpt[i] == $scope.detailDomainSource){
                sourceId = i + 1;
            }
        }
        // console.log(sourceId);
        $http({
            url : 'http://' + ip + '/domain/updateDomain?domainId=' + domainId
            + '&domainId=' + $scope.detailDomainId
            + '&domainName=' + $scope.detailDomainName
            + '&domainUrl=' + $scope.detailDomainUrl
            + '&note=' + $scope.detailDomainNote
            + '&sourceId=' + sourceId,
            method : 'get'
        }).success(function(response) {
            alert("更新成功");
            console.log("更新领域信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("更新领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("更新领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});



