var app = angular.module('myApp', [ ]);

app.controller('menu', function($scope, $http) {
 

    $http.get("ClassListData.json").success(
            function(json)
             { 
                $scope.ClassList = json
                $scope.NowClass=json[0]
                setCookie("NowClass",json[0].ClassName,"d900")
            });
})
function resetCookie(){
    setCookie("stepNumber",1,"s900")
    console.log(getCookie("stepNumber"))
     window.location.reload();
}


 // <!-- 设置图片 点击事件  跳转到不同的地方 -->
 function NavImgClick(number){
    if (number==1) {
        setCookie("stepNumber",number+1,"d900")
        setImgSrc()
        setTimeout('window.location.href="pages/extraction/index.html"', 1500);
    }else if (number==2) {
        setCookie("stepNumber",number+1,"d900")
        setImgSrc()
        setTimeout('window.location.href="pages/facet/index.html"', 1500);
    }
    else if (number==3) {
        setCookie("stepNumber",number+1,"d900")
        setImgSrc()
        setTimeout('window.location.href="pages/spider/index.html"', 1500);
    }
    else if (number==4) {
        setCookie("stepNumber",number+1,"d900")
        setImgSrc()
        setTimeout('window.location.href="pages/add/index.html"', 1500);
    }else if (number==5) {
        setCookie("stepNumber",number+1,"d900")
        setImgSrc()
        setTimeout('window.location.href="pages/relationship/index.html"', 1500);
    }
    else if (number==6) {
        setCookie("stepNumber",number,"d900")
        setImgSrc()
        window.location.href="pages/kg/index.html"
    }
 }

 // <!-- 设置图片src -->
function setImgSrc(){
    var contentheader=$(".content-header")
    var footer=$(".main-footer")
    var srcheight = footer.offset().top-contentheader.offset().top-100;   //画布的高度
    var img=$("#ImgNavigation")
    var step=getCookie("stepNumber")

    if (step=='') {
        // alert("空")
        step=1;
        setCookie("stepNumber",1,"d900")
    }
    console.log(srcheight)
    img.attr('height', srcheight);
    img.attr('src', 'dist/img/step'+step+'.jpg');
    img.load(function(){
            setImgArea()
    })
};


 // <!-- 设置图片热区 -->
function setImgArea(){
    var img=$("#ImgNavigation")
    console.log(img);
    console.log(img.width())
    var area1=$('#area1')
    area1.attr("coords",""+71/1900*img.width()+","+591/1080*img.height()+","+65/1900*img.width())
    var area2=$('#area2')
    area2.attr("coords",""+534/1900*img.width()+","+265/1080*img.height()+","+65/1900*img.width())
    var area3=$('#area3')
    area3.attr("coords",""+529/1900*img.width()+","+474/1080*img.height()+","+65/1900*img.width())
    var area4=$('#area4')
    area4.attr("coords",""+942/1900*img.width()+","+353/1080*img.height()+","+65/1900*img.width())
    var area5=$('#area5')
    area5.attr("coords",""+704/1900*img.width()+","+885/1080*img.height()+","+65/1900*img.width())
    var area6=$('#area6')
    area6.attr("coords",""+1332/1900*img.width()+","+595/1080*img.height()+","+80/1900*img.width())
};

$(document).ready(function(){
  setImgSrc()
});
