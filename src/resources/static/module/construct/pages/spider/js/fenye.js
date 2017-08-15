//点确定键之后的初始化显示函数
function showDivNum(){ 
     var wen=document.getElementsByClassName("wenbensuipian").length;
     var tu=document.getElementsByClassName("img").length;
    var sum=wen>tu?wen:tu;
    console.log(wen);
    console.log(tu);
//    console.log(sum);
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(sum/count);
//    console.log(index);
    $("#page").text(index);
    $("#nowpage").text(1);
    var source=document.getElementById("topsel").value;
    $("#sourcefrom").text(source);
    if(wen>count){
        for(var i=0;i<count;i++){
            $(".wenbensuipian")[i].style.display="block";
        }
        for(var i=count;i<wen;i++){
            $(".wenbensuipian")[i].style.display="none";
        }
    }
    else{
        for(var i=0;i<wen;i++){
            $(".wenbensuipian")[i].style.display="block";
        }
    }
    if(tu>count){
        for(var i=0;i<count;i++){
            $(".tupiansuipian")[i].style.display="block";
        }
        for(var i=count;i<tu;i++){
            $(".tupiansuipian")[i].style.display="none";
        }
    }
    else{
        for(var i=0;i<tu;i++){
            $(".tupiansuipian")[i].style.display="block";
        }
    }
}
//根据页码跳转函数
function go(){
    var gopage=$("#gopage").val();
    var sumpage=$("#page").text();
    console.log(gopage);
    console.log(sumpage);
    var wen=document.getElementsByClassName("wenbensuipian").length;
    var tu=document.getElementsByClassName("img").length;
    var sum=wen>tu?wen:tu;
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(sum/count);
    if(sumpage==1){
        alert("无效页码");
    }
    else{
    if(gopage>index||gopage<0){
        alert("无效页码");
    }
    else{
    $("#nowpage").text(gopage);
    var begin=count*(gopage-1);
    var end=Number(begin)+Number(count);
    console.log(begin);
    console.log(end);
    if(begin<wen&&end<wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".wenbensuipian")[i].style.display="block";
        for(var i=end;i<wen;i++)
            $(".wenbensuipian")[i].style.display="none";
    }
    if(begin<wen&&end>wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<wen;i++)
            $(".wenbensuipian")[i].style.display="block";
    }
    if(begin>wen){
        for(var i=0;i<tu;i++)
            $(".wenbensuipian")[i].style.display="none";
    }
    if(begin<tu&&end<tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".img")[i].style.display="block";
        for(var i=end;i<tu;i++)
            $(".img")[i].style.display="none";
    }
    if(begin<tu&&end>tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<tu;i++)
            $(".img")[i].style.display="block";
    }
    if(begin>tu){
        for(var i=0;i<tu;i++)
            $(".img")[i].style.display="none";
    }
}
}
}
//跳转上一页函数
function pre(){
    var nowpage=$("#nowpage").text();
    console.log(nowpage);
    var wen=document.getElementsByClassName("wenbensuipian").length;
    var tu=document.getElementsByClassName("img").length;
    var sum=wen>tu?wen:tu;
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(sum/count);
    if(nowpage==1){
        alert("当前已经是第一页");
    }
    else{
    var begin=count*(nowpage-2);
    var end=Number(begin)+Number(count);
    if(begin<wen&&end<wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".wenbensuipian")[i].style.display="block";
        for(var i=end;i<wen;i++)
            $(".wenbensuipian")[i].style.display="none";
    }
    if(begin<wen&&end>wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<wen;i++)
            $(".wenbensuipian")[i].style.display="block";
    }
    if(begin<tu&&end<tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".img")[i].style.display="block";
        for(var i=end;i<tu;i++)
            $(".img")[i].style.display="none";
    }
    if(begin<tu&&end>tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<tu;i++)
            $(".img")[i].style.display="block";
    }
    if(begin>tu){
        for(var i=0;i<tu;i++)
            $(".img")[i].style.display="none";
    }
    nowpage=nowpage-1;
    $("#nowpage").text(nowpage);
    }
}
//跳转下一页函数
function nex(){
    var nowpage=$("#nowpage").text();
    var sumpage=$("#page").text();
    var wen=document.getElementsByClassName("wenbensuipian").length;
    var tu=document.getElementsByClassName("img").length;
    var sum=wen>tu?wen:tu;
    var count=document.getElementById("numsel").value;//每页显示个数
    var index=Math.ceil(sum/count);
     if(nowpage==sumpage){
         alert("当前已经是最后一页");
     }
     else{
    var begin=count*nowpage;
    var end=Number(begin)+Number(count);
    if(begin<wen&&end<wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".wenbensuipian")[i].style.display="block";
        for(var i=end;i<wen;i++)
            $(".wenbensuipian")[i].style.display="none";
    }
    if(begin<wen&&end>wen){
        for(var i=0;i<begin;i++)
            $(".wenbensuipian")[i].style.display="none";
        for(var i=begin;i<wen;i++)
            $(".wenbensuipian")[i].style.display="block";
    }
    if(begin<tu&&end<tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<end;i++)
            $(".img")[i].style.display="block";
        for(var i=end;i<tu;i++)
            $(".img")[i].style.display="none";
    }
    if(begin<tu&&end>tu){
        for(var i=0;i<begin;i++)
            $(".img")[i].style.display="none";
        for(var i=begin;i<tu;i++)
            $(".img")[i].style.display="block";
    }
    if(begin>tu){
        for(var i=0;i<tu;i++)
            $(".img")[i].style.display="none";
    }
    nowpage=Number(nowpage)+1;
    $("#nowpage").text(nowpage);
    }
}
//主题全选函数
function select_all(){
    var inputs=document.getElementsByTagName("input");
    for(var i=0;i<inputs.length;i++){
        if(inputs[i].getAttribute("type")=="checkbox"){
            inputs[i].checked="true";
        }
    }
}

function clear_all(){
    $(":checkbox").attr("checked",false);
/*    for(var i=0;i<inputs.length;i++){
        if(inputs[i].getAttribute("type")=="checkbox"){
            inputs[i].checked="false";
        }
    }*/
}
$(document).ready(function(){
          //滚动条函数
        $("#suipianqu").slimScroll({
            height: zidingyi_height*0.75+'px'
        });

        $("#topic0").slimScroll({
            height: '300px'
        });

})

