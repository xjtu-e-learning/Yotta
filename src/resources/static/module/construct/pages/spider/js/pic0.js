//所有主题碎片数据统计结果，需要从后台get信息
$(document).ready(function(){ 
    var myDate7=new Date();
    var myDate6=new Date(myDate7.getTime()-1*24*3600*1000);
    var myDate5=new Date(myDate7.getTime()-2*24*3600*1000);
    var myDate4=new Date(myDate7.getTime()-3*24*3600*1000);
    var myDate3=new Date(myDate7.getTime()-4*24*3600*1000);
    var myDate2=new Date(myDate7.getTime()-5*24*3600*1000);
    var myDate1=new Date(myDate7.getTime()-6*24*3600*1000);
    var shijian1=myDate1.toLocaleDateString();
    var shijian2=myDate2.toLocaleDateString();
    var shijian3=myDate3.toLocaleDateString();
    var shijian4=myDate4.toLocaleDateString();
    var shijian5=myDate5.toLocaleDateString();
    var shijian6=myDate6.toLocaleDateString();
    var shijian7=myDate7.toLocaleDateString();

 /*   var text;
    var img;

    $.ajax({
        type:"GET",
        timeout:10000,
        async:false,
        url:'http://'+ip+"/YOTTA/SpiderAPI/getCountByDomain2?ClassName="+getCookie("NowClass"),
        cache:false,
        data:{},
        dataType:"json",
        success:function(data){
            text=data[0].number;
            img=data[1].number;
        }
    });*/

var myChart = echarts.init(document.getElementById('pic0'));

        // 指定图表的配置项和数据
/*    option = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    color :['steelblue'],
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            data : [shijian1, shijian2, shijian3, shijian4, shijian5, shijian6, shijian7],
            axisTick: {
                alignWithLabel: true
            }
        }
    ],
    yAxis : [
        {
            type : 'value'
        }
    ],
    series : [
        {
            name:'直接访问',
            type:'bar',
            barWidth: '60%',
            data:[10, 52, 200, 334, 390, 330, 220]
        }
    ]
};*/
option = {
    tooltip : {
        trigger: 'axis'
    },
    color :['steelblue','#c3272b'],
    legend: {
        data:['文本','图片']
    },
    toolbox: {
        show : true,
        feature : {
            mark : {show: true},
            dataView : {show: true, readOnly: false},
            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
            restore : {show: true},
            saveAsImage : {show: true}
        }
    },
    calculable : true,
    xAxis : [
        {
            type : 'category',
            boundaryGap : false,
            data : [shijian1,shijian2,shijian3,shijian4,shijian5,shijian6,shijian7]
        }
    ],
    yAxis : [
        {
            type : 'value'
        }
    ],
    series : [
        {
            name:'文本',
            type:'line',
            stack: '总量',
            itemStyle: {normal: {areaStyle: {type: 'default'}}},
            data:[120, 132, 101, 134, 90, 230, 210]
        },
        {
            name:'图片',
            type:'line',
            stack: '总量',
            itemStyle: {normal: {areaStyle: {type: 'default'}}},
            data:[20, 32, 11, 34, 20, 30, 18]
        }
    ]
};


        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    })