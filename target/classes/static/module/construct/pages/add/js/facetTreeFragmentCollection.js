

//变量
var SUBJECTNAME = "抽象资料型别";


//文本图片碎片栏滚动条设置
$(function() {
    $(".textSlimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '290px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});


$(function() {
    $(".pictureSlimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '130px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});

//主题单选框、分面复选框滚动条设置
$(function() {
    $(".model-slimscroll").slimScroll({
        width: 'auto', //可滚动区域宽度
        height: '200px', //可滚动区域高度
        size: '10px', //组件宽度
        color: '#000', //滚动条颜色
        position: 'right', //组件位置：left/right
        distance: '0px', //组件与侧边之间的距离
        start: 'top', //默认滚动位置：top/bottom
        opacity: .4, //滚动条透明度
        alwaysVisible: true, //是否 始终显示组件
        disableFadeOut: true, //是否 鼠标经过可滚动区域时显示组件，离开时隐藏组件
        railVisible: true, //是否 显示轨道
        railColor: '#333', //轨道颜色
        railOpacity: .2, //轨道透明度
        railDraggable: true, //是否 滚动条可拖动
        railClass: 'slimScrollRail', //轨道div类名 
        barClass: 'slimScrollBar', //滚动条div类名
        wrapperClass: 'slimScrollDiv', //外包div类名
        allowPageScroll: false, //是否 使用滚轮到达顶端/底端时，滚动窗口
        wheelStep: 20, //滚轮滚动量
        touchScrollStep: 200, //滚动量当用户使用手势
        borderRadius: '7px', //滚动条圆角
        railBorderRadius: '7px' //轨道圆角
    });
});



/*一、页面加载要做的事*/
//1、选择主题模态框加载
//2、显示第一个主题的树干
//3、右侧文本碎片栏初始化【碎片内容、碎片时间】
//4、右侧图片栏初始化显示标准图片

//1
//添加主题单选框进模态框
function AppendSubjectNameIntoModal(subjectName){
	var div_head='<div class="col-md-4" style="padding:10px;">';
	var div_tail='</div>';
	var input='<input type="radio" name="subject" class="subjectRadio" value='+subjectName+'>'+subjectName;

	var div=div_head+input+div_tail;
	$("#subjectModalBody").append(div);
}

var ykapp = angular.module('subjectApp', []);
ykapp.controller('subjectController', function($scope,$http) {
    console.log(getCookie("NowClass"))
    $http.get('http://'+ip+'/YOTTA/DomainTopicAPI/getDomainTopicAll?ClassName='+getCookie("NowClass")).success(function(response){
        $scope.Topics=response;

        //console.log($("#rightDiv").height());
        //console.log($('.box-header').height());
        var height=$("#rightDiv").height()-$('.box-header').height()-8;
        //var height=$(window).height()*0.9;
        $("#facetedTreeDiv").css("height",height+"px")
        //console.log($(window).height());
    });
    /*$.ajax({
         type: "GET",
         url: "http://202.117.54.39:8080/YOTTA/DomainTopicAPI/getDomainTopicAll",
         data: {
            ClassName:"数据结构"
         },
         dataType: "json",
         success: function(data){
                    $scope.Topics=data;
                    console.log($scope.Topics);
                    console.log("yangkuan");
                 },
         error:function(XMLHttpRequest, textStatus, errorThrown){
                //通常情况下textStatus和errorThrown只有其中一个包含信息
                alert(textStatus);
                }
    });*/

});

//2
function DisplayTrunk(dataset){

	document.getElementById("facetedTreeDiv").innerHTML='';
	var datas = [];	
	multiple=1;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#facetedTreeDiv")
				.append("svg")
				.attr("width", "100%")
				.attr("height","100%");
	//分面树的位置
	$("svg").draggable();
    var root_x=$("#facetedTreeDiv").width()/2;
    var root_y=$("#facetedTreeDiv").height()-30;
	var seed4 = {x: root_x* multiple, y: root_y* multiple, name:dataset.name}; 
	var tree4 = buildTree(dataset, seed4, multiple);
    draw_trunk(tree4, seed4, svg, multiple);	
}


function ObtainTrunk(subjectName){
	$.ajax({
             type: "GET",
             url: 'http://'+ip+"/YOTTA/AssembleAPI/getTreeByTopic",
             data: {
             	ClassName:getCookie("NowClass"),
         		TermName:subjectName
             },
             dataType: "json",
             success: function(data){
             			DisplayTrunk(data);
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
 
}

//3
function InitTextFragment(){
	appendTextFragment("文本碎片内容1","文本碎片爬取时间1");
	appendTextFragment("文本碎片内容2","文本碎片爬取时间2");
}

//4
function InitPictureFragment(){
	appendPictureFragment("http://pic1.cxtuku.com/00/13/03/b0968e72e10f.jpg");
	appendPictureFragment("http://img.juimg.com/tuku/yulantu/140214/330686-140214105F352.jpg");
}

$(document).ready(function(){
	//获取所有主题
	$.ajax({
             type: "GET",
             url: 'http://'+ip+"/YOTTA/DomainTopicAPI/getDomainTopicAll",
             data: {
             	ClassName:getCookie("NowClass")
             },
             dataType: "json",
             success: function(data){  
             			//ObtainTrunk("抽象资料型别");
                        //生成树枝
                        LoadBranch();
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
	InitTextFragment();
	InitPictureFragment();
});

//二、提交所选主题
//
$(document).ready(function(){
	$("button#subjectSubmit").click(function(){
		//获取被选中主题
		$("input.subjectRadio").each(function(index,value){
			if($(this).prop("checked")===true){
				SUBJECTNAME=$(this).val();
				console.log($(this));

				//提交主题，获取对应主题的分面及json数据
				//画树干
				//ObtainTrunk(SUBJECTNAME);
				//LoadFacetModal(SUBJECTNAME);
      
                //生成树枝
                LoadBranch();
				//关闭主题模态框
				$("#subjectModal").modal("hide");
				return;
			}
		});
	});
});

function LoadFacetModal(subjectName){
	// 清空分面模态框
	$("#facetModalBody").empty();

	$.ajax({
             type: "GET",
             url: 'http://'+ip+"/YOTTA/FacetAPI/getFacet",
             data: {
             	ClassName:getCookie("NowClass"),
             	TermName:subjectName,
             	FacetLayer:1
             },
             dataType: "json",
             success: function(data){
             			$.each(data,function(index,value){
             				AppendFacetNameIntoModal(value.facetName);
             			});
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
}

//添加分面复选框进模态框
function AppendFacetNameIntoModal(facetName){
	var div_head='<div class="col-md-3" style="padding:5px;">';
	var div_tail='</div>';
	var input='<input type="checkbox" class="facetCheckbox" checked="true" value='+facetName+'>'+facetName;

	var div=div_head+input+div_tail;
	$("#facetModalBody").append(div);
}


//三、提交分面选择信息
//


//分面复选框的全选
$(document).ready(function(){
	$("button#selectAll").click(function(){
		$("input.facetCheckbox").each(function(){
			$(this).prop("checked",true);
		}); 
	});
});

$(document).ready(function(){
	var facetList=[];
	$("button#facetSubmit").click(function(){
		//获取被选中主题
		$("input.facetCheckbox").each(function(index,value){
			if($(this).prop("checked")===true){
				facetList.push($(this).val());

				//提交所选分面，获取对应的json数据
				//生成树枝
				LoadBranch();

				//关闭主题模态框
				$("#facetModal").modal("hide");
			}
		});
		console.log(facetList);
	});
});

function LoadBranch(){
	$.ajax({
             type: "GET",
             url: 'http://'+ip+"/YOTTA/AssembleAPI/getTreeByTopic",
             data: {
             	ClassName:getCookie("NowClass"),
             	TermName:SUBJECTNAME
             },
             dataType: "json",
             success: function(data){
             			DisplayBranch(data);
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
}

function DisplayBranch(dataset){
	document.getElementById("facetedTreeDiv").innerHTML='';
	var datas = [];	
	multiple=1;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#facetedTreeDiv")
				.append("svg")
				.attr("width", "100%")
				.attr("height","100%");
	//分面树的位置	
    var root_x=$("#facetedTreeDiv").width()/2;
    var root_y=$("#facetedTreeDiv").height()-30; //
	$("svg").draggable();
	var seed = {x: root_x* multiple, y: root_y* multiple, name:dataset.name}; 
	var tree = buildBranch(dataset, seed, multiple);
    draw_tree(tree, seed, svg, multiple);
}



//四、点击装配按钮，在右侧栏显示所有文本碎片和图片碎片
//显示文本、图片碎片比例
function DisplayAllFragment(){
	//清空文本和图片碎片
	$("#textFragmentDiv").empty();
	$("#pictureFragmentDiv").empty();
	var picNum=0;
	var textNum=0;

	$.ajax({
             type: "GET",
             url: 'http://'+ip+"/YOTTA/AssembleAPI/getTreeByTopic",
             data: {
             	ClassName:getCookie("NowClass"),
             	TermName:SUBJECTNAME
             },
             dataType: "json",
             success: function(data){
             			//进入一级分面
						$.each(data.children,function(index1,value1){
							//进入二级分面
							$.each(value1.children,function(index2,value2){
								if (value2.type==="branch"){
									//遍历树叶
									$.each(value2.children,function(index3,value3){
										if(value3.flag==="text"){
											appendTextFragment(value3.content,value3.scratchTime);
											textNum++;
										}
										else{
											appendPictureFragment(value3.content);
											picNum++;
										}
									});
								} 
								else{
									if(value2.flag==="text"){
										appendTextFragment(value2.content,value2.scratchTime);
										textNum++;
									}
									else{
										appendPictureFragment(value2.content);
										picNum++;
									}
								}
							});
							showFragmentRatio(textNum,picNum);
							//找到所有叶子，结束
							//return;
						});
                     },
             error:function(XMLHttpRequest, textStatus, errorThrown){
          			//通常情况下textStatus和errorThrown只有其中一个包含信息
          			alert(textStatus);
       				}
        });
}

//展示文本碎片和图形碎片的数量
function showFragmentRatio(text,picture){
	//获得文本碎片和图片碎片数量
    var text_ratio=text/(text+picture)*100;
    var piture_ratio=picture/(text+picture)*100;
	//显示碎片
	document.getElementById("textCount").innerHTML=text; 
	document.getElementById('textRatio').style.width=""+text_ratio+"%";
	document.getElementById("pictureCount").innerHTML=picture; 
	document.getElementById('pictureRatio').style.width=""+piture_ratio+"%";
}



