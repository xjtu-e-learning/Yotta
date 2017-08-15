var dep;
var layer;
    $(document).ready(function(){
            $.ajax(
                    {type :"GET",
                        url :'http://'+ip+"/YOTTA/DependencyAPI/getDependencyByDomain?ClassName="+getCookie("NowClass"),
                        datatype :"json",
                        async:false,
                        success : function(data,status){
                            dep=data;
                             console.log(dep.length);
                        }
                    })
            // $.ajax(
            //         {type :"GET",
            //             url :'http://'+ip+"/YOTTA/DomainTopicAPI/getDomainTopicAll?ClassName="+getCookie("NowClass"),
            //             datatype :"json",
            //             async:false,
            //             success : function(data,status){
            //                 layer=data;
            //                 // console.log(layer.length);
            //             }
            //         })
      
             //定义edges[]和nodes[]
    var edges=new Array();
    for(var i=0;i<dep.length;i++){
        edges[i]={source:Number(dep[i].StartID)-1,sourceName:dep[i].Start,targetName:dep[i].End,target:Number(dep[i].EndID)-1,conf:Number(dep[i].Confidence)};
        }
    // var nodes=new Array();
    // for(var i=0;i<layer.length;i++){
    //     nodes[i]={name:layer[i].TermName};
    //   }
    //向table中添加关系
    for(var i=0;i<edges.length;i++){
        $("#table").append(
          "<tr class='tr1' id="+i+"><td>"+edges[i].sourceName+"</td><td>"+edges[i].targetName+"</td></tr>"
          );
        }

        


    var xml;
$.ajax({
    url: 'a1.xml',
    type: 'get',
    async:false,
    dataType: 'xml',
    data: {param1: 'value1'},
})
.done(function(data) {
    console.log("success");
    xml=data
    console.log(xml);
})
.fail(function() {
    console.log("error");
})


   

  //画力关系图
 var dom = document.getElementById("echarts1");
    var myChart = echarts.init(dom);
    var app = {};
    var option = null;
        var graph = echarts.dataTool.gexf.parse(xml);
        var categories = [];


        categories[0] = {
                name: '(Start)数据结构'
            };
        categories[1] = {
                name: '树'
            };
        categories[2] = {
                name: '数组'
            };
        categories[3] = {
                name: '正则图'
            };            
        categories[4] = {
                name: '链表'
            }; 
        categories[5] = {
                name: '关联数组'
            }; 
        categories[6] = {
                name: '抽象资料'
            }; 

        
        graph.nodes.forEach(function (node) {
            node.itemStyle = null;
            node.value = node.symbolSize;
            node.label = {
                normal: {
                    show: node.symbolSize > 25
                }
            };
            node.category = node.attributes.modularity_class;
        });
        option = {
            title: {
                text: '数据结构',
                subtext: 'Default layout',
                top: 'bottom',
                left: 'right'
            },
            tooltip: {},
            legend: [{
                // selectedMode: 'single',
                data: categories.map(function (a) {
                    return a.name;
                })
            }],
            animationDuration: 1500,
            animationEasingUpdate: 'quinticInOut',
            series : [
                {
                    name: '数据结构',
                    type: 'graph',
                    layout: 'none',
                    data: graph.nodes,
                    links: graph.links,
                    categories: categories,
                    roam: true,
                    label: {
                        normal: {
                            position: 'right',
                            formatter: '{b}'
                        }
                    },
                    lineStyle: {
                        normal: {
                            curveness: 0.3
                        }
                    }
                }
            ]
        };

        myChart.setOption(option);
     })