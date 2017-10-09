var myApp = angular.module("myApp", ['ui.bootstrap', 'ngAnimate']);
var sourceId = "";

myApp.controller('domainCtrl', function($scope, $uibModal, $http) {

    /*******************************************************************************
     ********************************* 领域分页查询显示 *****************************
     *******************************************************************************/
    sourceId = "1";
    $scope.sourceName = "中文维基百科";
    $scope.sourceType = "百科类";
    // 领域分页设置
    var ascOrder = true;
    $scope.currentPage = 1; // 当前页
    $scope.numPerPage = 5; // 每页显示的条数
    $scope.maxSize = 100;
    // 主题分页设置
    var ascOrder = true;
    $scope.topicCurrentPage = 1; // 当前页
    $scope.topicNumPerPage = 5; // 每页显示的条数
    $scope.topicMaxSize = 100;
    var selectedDomainId = 1; // 保存需要查看的领域Id信息
    var selectedDomainName = '空'; // 保存需要查看的领域名信息
    // 主题关系分页设置
    $scope.topicRelationCurrentPage = 1; // 当前页
    $scope.topicRelationNumPerPage = 5; // 每页显示的条数
    $scope.topicRelationMaxSize = 100;

    var selectedTopicId = 1; // 保存需要查看的主题Id信息
    var selectedTopicName = '空'; // 保存需要查看的主题名信息

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
                },
                sourceType : function() {//data作为modal的controller传入的参数
                    return $scope.sourceType;//用于传递数据
                },
                ascOrder : function () {
                    return ascOrder;
                },
                numPerPage : function () {
                    return $scope.numPerPage;
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
     * 领域：显示该领域下的主题及主题关系
     */
    $scope.domainTopicInfo = function($index) {
        selectedDomainId = $scope.domains[$index].domainId; // 保存选中的领域信息，用于分页的时候使用
        selectedDomainName = $scope.domains[$index].domainName; // 保存选中的领域信息，用于分页的时候使用
        var oldDomainId = $scope.domains[$index].domainId; // 保存点击前的领域信息，用户恢复现场
        var oldDomainName = $scope.domains[$index].domainName;

        // 显示该领域下的主题
        $http({
            url : 'http://' + ip + '/topic/getTopicByDomainIdAndPagingAndSorting?page=1&size='
            + $scope.topicNumPerPage + '&ascOrder=' + ascOrder + '&domainId=' + $scope.domains[$index].domainId,
            method : 'get'
        }).success(function(response) {
            $scope.topicDomain = $scope.domains[$index].domainName;
            console.log("$scope.domains[$index].domainName: " + $scope.domains[$index].domainName);
            $scope.topicTotalItems = response.data.totalElements;
            $scope.topics = response.data.content;
            console.log("获取主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
            console.log("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
            $scope.topicDomain = oldDomainName;
        });

        // 显示该领域下的主题关系
        $http({
            url : 'http://' + ip + '/topic/getTopicRelationByDomainIdAndPagingAndSorting?page=1&size='
            + $scope.topicNumPerPage + '&ascOrder=' + ascOrder + '&domainId=' + $scope.domains[$index].domainId,
            method : 'get'
        }).success(function(response) {
            $scope.topicDomain = $scope.domains[$index].domainName;
            console.log("$scope.domains[$index].domainName: " + $scope.domains[$index].domainName);
            $scope.topicRelationTotalItems = response.data.totalElements;
            $scope.topicRelations = response.data.content;
            console.log("获取主题关系成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取主题关系失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
            console.log("获取主题关系失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
            $scope.topicDomain = oldDomainName;
        });

    };




    /*******************************************************************************
     ********************************* 主题 ****************************************
     *******************************************************************************/
    /**
     * selectedDomainId 为选中领域Id
     * selectedDomainName 为选中领域名
     * sourceId = "1";
     * $scope.sourceName = "中文维基百科";
     * $scope.sourceType = "百科类";
     */

    /**
     * 主题：主题分页点击下一页等的操作
     */
    $scope.topicPageChanged = function() {
        console.log("进入主题页面切换状态");
        $http({
            url : 'http://' + ip + '/topic/getTopicByDomainIdAndPagingAndSorting?page='
            + $scope.topicCurrentPage + '&size=' + $scope.topicNumPerPage + '&ascOrder=' + ascOrder + '&domainId=' + selectedDomainId,
            method : 'get'
        }).success(function(response) {
            $scope.topicTotalItems = response.data.totalElements;
            $scope.topics = response.data.content;
            console.log("获取主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + selectedDomainId + "，领域名为：" + selectedDomainName);
            console.log("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + selectedDomainId + "，领域名为：" + selectedDomainName);
        });
    };

    /**
     * 主题：主题分页点击下一页等的操作
     */
    $scope.topicRelationPageChanged = function() {
        console.log("进入主题关系页面切换状态");
        $http({
            url : 'http://' + ip + '/topic/getTopicRelationByDomainIdAndPagingAndSorting?page='
            + $scope.topicRelationCurrentPage + '&size=' + $scope.topicRelationNumPerPage + '&ascOrder=' + ascOrder + '&domainId=' + selectedDomainId,
            method : 'get'
        }).success(function(response) {
            $scope.topicRelationTotalItems = response.data.totalElements;
            $scope.topicRelations = response.data.content;
            console.log("获取主题关系成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("获取主题关系失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + selectedDomainId + "，领域名为：" + selectedDomainName);
            console.log("获取主题关系失败，code为：" + response.code + "，msg为：" + response.msg +
                "，领域ID为：" + selectedDomainId + "，领域名为：" + selectedDomainName);
        });
    };

    /**
     * 主题：添加主题信息 模态框（添加领域下的一级主题）
     */
    $scope.openModalInsertTopic = function() {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalInsertTopic.html',//script标签中定义的id
            controller : 'modalCtrlmodalInsertTopic',//modal对应的Controller
            resolve : {
                selectedDomainId : function () {
                  return selectedDomainId;
                },
                selectedDomainName : function() {//data作为modal的controller传入的参数
                    return selectedDomainName;//用于传递数据
                }
            }
        })
    };

    /**
     * 主题：添加子主题信息 模态框（添加主题下的子主题）
     */
    $scope.openModalInsertChildTopic = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalInsertChildTopic.html',//script标签中定义的id
            controller : 'modalCtrlmodalInsertChildTopic',//modal对应的Controller
            resolve : {
                selectedDomainId : function () {
                    return selectedDomainId;
                },
                selectedDomainName : function() {
                    return selectedDomainName;
                },
                selectedTopicName : function() {
                    return $scope.topics[$index].topicName;
                }
            }
        })
    };

    /**
     * 主题：主题信息详情 模态框
     */
    $scope.openModalTopicDetail = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalTopicDetail.html',//script标签中定义的id
            controller : 'modalCtrlmodalTopicDetail',//modal对应的Controller
            resolve : {
                topicId : function() { // 主题Id
                    console.log($scope.topics[$index]);
                    console.log($index);
                    return $scope.topics[$index].topicId;
                },
                topicName : function() { // 主题名
                    return $scope.topics[$index].topicName;
                },
                topicUrl : function() { // 主题链接
                    return $scope.topics[$index].topicUrl;
                },
                topicLayer : function() { // 主题层级
                    return $scope.topics[$index].topicLayer;
                },
                selectedDomainId : function() { // 领域Id
                    return selectedDomainId;
                },
                selectedDomainName : function() { // 领域名
                    return selectedDomainName;
                },
                sourceName : function() { // 数据源名
                    return $scope.sourceName;
                }
            }
        })
    };

    /**
     * 主题：修改主题信息 模态框
     */
    $scope.openModalTopicModify = function($index) {

        var modalInstance = $uibModal.open({
            templateUrl : 'modalTopicModify.html',//script标签中定义的id
            controller : 'modalCtrlmodalTopicModify',//modal对应的Controller
            resolve : {
            	sourceId : function() { // 数据源Id
					return sourceId;
            	},
                topicId : function() { // 主题Id
                    return $scope.topics[$index].topicId;
                },
                topicName : function() { // 主题名
                    return $scope.topics[$index].topicName;
                },
                topicUrl : function() { // 主题链接
                    return $scope.topics[$index].topicUrl;
                },
                topicLayer : function() { // 主题层级
                    return $scope.topics[$index].topicLayer;
                },
                selectedDomainName : function() { // 领域名
                    return selectedDomainName;
                },
            }
        })

    };

    /**
     * 主题：删除主题信息
     */
    $scope.topicDelete = function($index) {
        $http({
            url : 'http://' + ip + '/topic/deleteTopic?topicId=' + $scope.topics[$index].topicId,
            method : 'get'
        }).success(function(response) {
            alert("删除主题成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log("删除主题成功，code为：" + response.code + "，msg为：" + response.msg);
        }).error(function(response){
            alert("删除主题失败，code为：" + response.code + "，msg为：" + response.msg +
                "，主题Id为：" + $scope.topics[$index].topicId);
            console.log("获取主题失败，code为：" + response.code + "，msg为：" + response.msg +
                "，主题Id为：" + $scope.topics[$index].topicId);
        });
    };

    /**
     * 主题：删除主题关系信息
     */
    $scope.topicRelationDelete = function($index) {
        $http({
            url : 'http://' + ip + '/topic/deleteTopic?topicId=' + $scope.topics[$index].topicId,
            method : 'get'
        }).success(function(response) {
            alert("删除主题成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log("删除主题成功，code为：" + response.code + "，msg为：" + response.msg);
        }).error(function(response){
            alert("删除主题失败，code为：" + response.code + "，msg为：" + response.msg +
                "，主题Id为：" + $scope.topics[$index].topicId);
            console.log("获取主题失败，code为：" + response.code + "，msg为：" + response.msg +
                "，主题Id为：" + $scope.topics[$index].topicId);
        });
    };


    // /**
    //  * 主题：显示该主题下的分面信息
    //  */
    // $scope.topicFacetInfo = function($index) {
    //     selectedDomainId = $scope.domains[$index].domainId; // 保存选中的领域信息，用于分页的时候使用
    //     selectedDomainName = $scope.domains[$index].domainName; // 保存选中的领域信息，用于分页的时候使用
    //     var oldDomainId = $scope.domains[$index].domainId; // 保存点击前的领域信息，用户恢复现场
    //     var oldDomainName = $scope.domains[$index].domainName;
    //     $http({
    //         url : 'http://' + ip + '/topic/getTopicByDomainIdAndPagingAndSorting?page=1&size='
    //         + $scope.topicNumPerPage + '&ascOrder=' + ascOrder + '&domainId=' + $scope.domains[$index].domainId,
    //         method : 'get'
    //     }).success(function(response) {
    //         $scope.topicDomain = $scope.domains[$index].domainName;
    //         console.log("$scope.domains[$index].domainName: " + $scope.domains[$index].domainName);
    //         $scope.topicTotalItems = response.data.totalElements;
    //         $scope.topics = response.data.content;
    //         console.log("获取主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
    //         console.log(response.data);
    //     }).error(function(response){
    //         alert("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
    //             "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
    //         console.log("获取主题信息失败，code为：" + response.code + "，msg为：" + response.msg +
    //             "，领域ID为：" + oldDomainId + "，领域名为：" + oldDomainName);
    //         $scope.topicDomain = oldDomainName;
    //     });
    //
    // };


});





/*******************************************************************************
 ********************************* 领域 ****************************************
 *******************************************************************************/
// 领域插入 模态框对应的Controller
myApp.controller('modalCtrlmodalInsertDomain', function($scope, $http, $uibModalInstance, sourceId, sourceName, numPerPage, ascOrder) {

    $scope.sourceName = sourceName;

    //在这里处理要进行的操作
    $scope.ok = function() {
        $http({
            // http://localhost:8080/domain/insertDomain?domainId=11&domainName=aaa&domainUrl=aa&note=aa&sourceId=3
            url : 'http://' + ip + '/domain/insertDomain?domainName='
            + ($scope.insertDomainName == null ? '' : $scope.insertDomainName)
            + '&domainUrl=' + ($scope.insertDomainUrl == null ? '' : $scope.insertDomainUrl)
            + '&note=' + ($scope.insertDomainNote == null ? '' : $scope.insertDomainNote)
            + '&sourceId=' + sourceId,
            method : 'get'
        }).success(function(response) {
            $http({
                url : 'http://' + ip + '/domain/getDomainBySourceIdAndPagingAndSorting?page=1&size='
                + numPerPage + '&ascOrder=' + ascOrder + '&sourceId=' + sourceId,
                method : 'get'
            }).success(function(response) {
                $scope.totalItems = response.data.totalElements;
                $scope.domains = response.data.content;
                console.log($scope.domains);
                console.log("刷新页面显示插入的数据");
            }).error(function(response){
                console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg +
                    "，数据源ID为：" + sourceId + "，数据源名为：" + sourceName);
            });
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
        url : 'http://' + ip + '/topic/judgeTopicByDomainId?domainId=' + domainId,
        method : 'get'
    }).success(function(response) {
        $scope.detailTopic = response.data; // 记录的总条数
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



/*******************************************************************************
 ********************************* 主题 ****************************************
 *******************************************************************************/
// 主题及主题关系插入 模态框对应的Controller
myApp.controller('modalCtrlmodalInsertTopic', function($scope, $http, $uibModalInstance, selectedDomainId, selectedDomainName) {

    $scope.selectedDomainName = selectedDomainName;
    // 设置插入主题的父主题：获取目前所有主题，供其选择（删除该部分逻辑，这里添加的主题都是领域的子主题）
    // $scope.insertParentTopic = selectedDomainName;
    // $http({
    //     // http://localhost:8080/topic/getTopicByDomainId?domainId=28
    //     url : 'http://' + ip + '/topic/getTopicByDomainId?domainId=' + selectedDomainId,
    //     method : 'get'
    // }).success(function(response) {
    //     var topics = response.data; // 记录的总条数
    //     var insertParentTopicOpts = new Array(topics.length + 1);
    //     insertParentTopicOpts[0] = selectedDomainName;
    //     for (var i = 0; i < topics.length; i++) {
    //         insertParentTopicOpts[i + 1] = topics[i].topicName;
    //     }
    //     $scope.insertParentTopicOpt = insertParentTopicOpts;
    // });

    //在这里处理要进行的操作
    $scope.ok = function() {
        // 插入主题及主题关系
        $http({
            url : 'http://' + ip + '/topic/insertTopicUnderDomain?topicName='
            + ($scope.insertTopicName == null ? '' : $scope.insertTopicName)
            + '&topicUrl=' + ($scope.insertTopicUrl == null ? '' : $scope.insertTopicUrl)
            + '&domainId=' + selectedDomainId,
            method : 'get'
        }).success(function(response) {
            alert("插入成功");
            console.log("插入主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("插入主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("插入主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});


// 子主题及子主题关系插入 模态框对应的Controller
myApp.controller('modalCtrlmodalInsertChildTopic', function($scope, $http, $uibModalInstance, selectedDomainId, selectedDomainName, selectedTopicName) {

    $scope.selectedDomainName = selectedDomainName;
    $scope.selectedTopicName = selectedTopicName;

    //在这里处理要进行的操作
    $scope.ok = function() {
        // 插入主题及主题关系
        $http({
            url : 'http://' + ip + '/topic/insertTopicUnderTopic?topicName='
            + ($scope.insertChildTopicName == null ? '' : $scope.insertChildTopicName)
            + '&topicUrl=' + ($scope.insertChildTopicUrl == null ? '' : $scope.insertChildTopicUrl)
            + '&domainId=' + selectedDomainId + '&parentTopicName=' + selectedTopicName,
            method : 'get'
        }).success(function(response) {
            alert("插入成功");
            console.log("插入主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("插入主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("插入主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});


// 主题详情 模态框对应的Controller
myApp.controller('modalCtrlmodalTopicDetail', function($scope, $http, $uibModalInstance,
                                                       topicId, topicName, topicUrl, topicLayer, selectedDomainId, selectedDomainName, sourceName) {

    $http({
        url : 'http://' + ip + '/facet/judgeFacetByTopicId?topicId=' + topicId,
        method : 'get'
    }).success(function(response) {
        // 该主题对应的是目录页面，没有主题页面，不可爬取
        if (topicUrl.indexOf("Category:") >= 0) {
            $scope.detailFacetAndFragment = "分面和碎片无需爬取";
            $scope.detailTopicNote = "该主题是目录主题，页面是目录页面，无需爬取";
            $scope.detailFacetAndFragmentSpiderDisabled = "disabled";
            console.log("该主题是目录主题，页面是目录页面，无需爬取");
        } else {
            $scope.detailFacetAndFragment = response.data[2]; // 第3个元素是分面信息
            $scope.detailFacetAndFragmentSpiderDisabled = "disabled";
            $scope.detailTopicNote = "主题页面，需要爬取分面和碎片";
            console.log("主题下的分面和碎片数据已经爬取，code为：" + response.code + "，msg为：" + response.msg);
        }
        $scope.detailParentTopics = response.data[1]; // 第2个元素是父主题数量
        $scope.detailChildTopics = response.data[0]; // 第1个元素是子主题数量
    }).error(function(response){
        // 该主题对应的是目录页面，没有主题页面，不可爬取
        if (topicUrl.indexOf("Category:") >= 0) {
            $scope.detailFacetAndFragment = "分面和碎片无需爬取";
            $scope.detailTopicNote = "该主题是目录主题，页面是目录页面，无需爬取";
            $scope.detailFacetAndFragmentSpiderDisabled = "disabled";
            console.log("该主题是目录主题，页面是目录页面，无需爬取");
        } else {
            $scope.detailFacetAndFragment = "分面和碎片未爬取，需要重新爬取";
            $scope.detailFacetAndFragmentSpiderDisabled = "";
            $scope.detailTopicNote = "主题页面，需要爬取分面和碎片";
            console.log("主题下的分面和碎片数据还没有爬取，code为：" + response.code + "，msg为：" + response.msg);
        }
        $scope.detailParentTopics = response.data[1]; // 第2个元素是父主题数量
        $scope.detailChildTopics = response.data[0]; // 第1个元素是子主题数量
    });

    $scope.detailTopicId = topicId;
    $scope.detailTopicName = topicName;
    $scope.detailTopicUrl = topicUrl;
    $scope.detailTopicLayer = topicLayer;
    $scope.detailDomainId = selectedDomainId;
    $scope.detailDomainName = selectedDomainName;
    $scope.detailSourceName = sourceName;

    //在这里处理要进行的操作
    $scope.ok = function() {
        $uibModalInstance.close();
    };

    // 点击“关闭”按钮
    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    };

    // 点击“爬取分面和碎片”后进行主题爬取
    $scope.spiderFacetAndFragmentByTopic = function() {
        alert("开始爬取分面");
        $http({
            url : 'http://' + ip + '/facet/storeAllFacetAndContentByTopic?topicId=' + topicId + "&topicName=" + topicName + "&topicUrl=" + topicUrl,
            method : 'get'
        }).success(function(response) {
            alert("爬取分面和碎片成功");
            console.log("主题下的分面数据爬取成功，code为：" + response.code + "，msg为：" + response.msg + "，分面信息为：" + response.data);
        }).error(function(response){
            alert("爬取分面和碎片失败");
            console.log("主题下的分面数据爬取失败，code为：" + response.code + "，msg为：" + response.msg);
        });
    }

});


// 主题修改 模态框对应的Controller
myApp.controller('modalCtrlmodalTopicModify', function($scope, $http, $uibModalInstance,
               sourceId, topicId, topicName, topicUrl, topicLayer, selectedDomainName) {

    $scope.detailTopicId = topicId;
    $scope.detailTopicName = topicName;
    $scope.detailTopicUrl = topicUrl;
    $scope.detailTopicLayer = topicLayer;
    $scope.detailTopicDomain = selectedDomainName;
    // $scope.detailTopicDomainOpt = ["数据结构", "计算机科学史"];
    // 获取所有领域信息
    var domainIds = []; // 保存领域对应的领域id
    var domainNames = []; // 保存领域名
    $http({
        url : 'http://' + ip + '/domain/getDomainBySourceID?sourceId=' + sourceId,
        method : 'get'
    }).success(function(response) {
    	var data = response.data;
    	for (var i = 0; i < data.length; i++) {
    		domainNames.push(data[i].domainName);
    		domainIds.push(data[i].domainId);
    	}
    	$scope.detailTopicDomainOpt = domainNames; // 领域名数组
    }).error(function(response){
        console.log("获取领域信息失败，code为：" + response.code + "，msg为：" + response.msg);
    });

    // 在这里处理要进行的操作
    $scope.ok = function() {
        // 得到更新后的数据源Id
        var domainId = 1;
        for(var i = 0; i < domainNames.length; i++){
            if(domainNames[i] == $scope.detailTopicDomain){
                domainId = domainIds[i];
                console.log(domainId);
            }
        }
        $http({
            url : 'http://' + ip + '/topic/updateTopic?topicId=' + topicId
            + '&topicId=' + $scope.detailTopicId
            + '&topicName=' + $scope.detailTopicName
            + '&topicUrl=' + $scope.detailTopicUrl
            + '&topicLayer=' + $scope.detailTopicLayer
            + '&domainId=' + domainId,
            method : 'get'
        }).success(function(response) {
            alert("更新成功");
            console.log("更新主题信息成功，code为：" + response.code + "，msg为：" + response.msg);
            console.log(response.data);
        }).error(function(response){
            alert("更新主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
            console.log("更新主题信息失败，code为：" + response.code + "，msg为：" + response.msg);
        });
        $uibModalInstance.close();
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

});
