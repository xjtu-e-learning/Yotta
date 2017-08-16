setCookie("sourceId","1","1000");
setCookie("sourceName","中文维基百科","1000");
setCookie("sourceType","百科类","1000");
alert("sourceId is " + getCookie("sourceId") +
    "sourceName is " + getCookie("sourceName") +
    "sourceType is " + getCookie("sourceType"));

// 加载整个文档
$(document).ready(function(){

    // 获取领域数据
    $.ajax(
        {type :"GET",
            url :'http://'+ip+"/getDomainBySourceID?sourceId="+getCookie("sourceId"),
            datatype :"json",
            async:false,
            success : function(data, status){
                domainInfo = data;
                console.log(domainInfo);
        }
    });

    // 显示领域数据
    domainInfoController();

});

// 显示所有领域数据
function domainInfoController() {
    var code = domainInfo.code;
    var data = domainInfo.data;
    var msg = domainInfo.msg;
    if (code == 200) {
        console.log("获取领域信息成功，code为：" + code + "，msg为：" + msg);
        for(var i = 0;i < data.length; i++){
            $("#domain-body").append(
                "<tr> " +
                "<th scope='row'>" + data[i].domainId + "</th>" +
                "<td><a href=" + data[i].domainUrl + " target='_blank'>" + data[i].domainName + "</a></td>" +
                "<td>" + data[i].sourceId + "</td>" +
                "<td>" + getCookie("sourceName") + "</td>" +
                "<td>" + getCookie("sourceType") + "</td>" +
                "<td>" +
                "<a href=''>详情</a> <a href=''>修改</a> <a href=''>删除</a>" +
                "</td> " +
                "</tr>"
            );
        }
    } else {
        console.log("获取领域信息失败，code为：" + code + "，msg为：" + msg);
    }
}
