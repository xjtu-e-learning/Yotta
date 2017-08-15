//所有主题碎片数据统计结果，需要从后台get信息
$(document).ready(function(){ 
    var text;
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
    });

var myChart = echarts.init(document.getElementById('pic2'));

        // 指定图表的配置项和数据
       option = {
    title : {
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    color :['steelblue','rgb(131,175,155)'],
    
    series : [
        {
            name: '统计结果',
            type: 'pie',
            radius : '70%',
            center: ['50%', '50%'],
            data:[
                {value:text, name:'文本碎片'},
                {value:img, name:'图片碎片'}
            ],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    })