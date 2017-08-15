//某数据源部分主题统计结果，确定键激发，用到了controller中的两个全局变量
$(document).ready(function(){
$("#yes").click(function(){ 
var myChart = echarts.init(document.getElementById('pic1'));

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
                {value:quanju_textnum, name:'文本碎片'},
                {value:quanju_imgnum, name:'图片碎片'}
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
})



