document.onkeydown = function(e) {
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
            $("#search").click();
        }
    }  


    //定义edges[]和nodes[]
var treeicon="M537.804,174.688c0-44.772-33.976-81.597-77.552-86.12c-12.23-32.981-43.882-56.534-81.128-56.534   c-16.304,0-31.499,4.59-44.514,12.422C319.808,17.949,291.513,0,258.991,0c-43.117,0-78.776,31.556-85.393,72.809   c-3.519-0.43-7.076-0.727-10.71-0.727c-47.822,0-86.598,38.767-86.598,86.598c0,2.343,0.172,4.638,0.354,6.933   c-24.25,15.348-40.392,42.333-40.392,73.153c0,27.244,12.604,51.513,32.273,67.387c-0.086,1.559-0.239,3.107-0.239,4.686   c0,47.822,38.767,86.598,86.598,86.598c14.334,0,27.817-3.538,39.723-9.696c16.495,11.848,40.115,26.67,51.551,23.715   c0,0,4.255,65.905,3.337,82.64c-1.75,31.843-11.303,67.291-18.025,95.979h104.117c0,0-15.348-63.954-16.018-85.307   c-0.669-21.354,6.675-60.675,6.675-60.675l36.118-37.36c13.903,9.505,30.695,14.908,48.807,14.908   c44.771,0,81.597-34.062,86.12-77.639c32.98-12.23,56.533-43.968,56.533-81.214c0-21.994-8.262-41.999-21.765-57.279   C535.71,195.926,537.804,185.561,537.804,174.688z M214.611,373.444c6.942-6.627,12.766-14.372,17.212-22.969l17.002,35.62   C248.816,386.096,239.569,390.179,214.611,373.444z M278.183,395.438c-8.798,1.597-23.782-25.494-34.416-47.517   c11.791,6.015,25.102,9.477,39.254,9.477c3.634,0,7.201-0.296,10.72-0.736C291.006,374.286,286.187,393.975,278.183,395.438z    M315.563,412.775c-20.35,5.651-8.167-36.501-2.334-60.904c4.218-1.568,8.301-3.413,12.183-5.604   c2.343,17.786,10.069,33.832,21.516,46.521C337.011,401.597,325.593,409.992,315.563,412.775z";

var pd2 = 0;
var pd1 = 0;
$(document).ready(function(){
	var edges=new Array();
	$.ajax({
             statusCode: {
                200: function(){
                    console.log("success")
                }
             },
             type: "GET",
             url: 'http://'+ip+"/YOTTA/DependencyAPI/getDependencyByDomain?ClassName="+getCookie("NowClass"),
             data: {},
             dataType: "json",
             success: function(data){
			    for(var i=0;i<data.length;i++){
			        edges[i]={source:Number(data[i].StartID)-1,sourceName:data[i].Start,targetName:data[i].End,target:Number(data[i].EndID)-1,conf:Number(data[i].Confidence)};
			        //console.log(data[i].Start);
			 	}
			 	var nodes=new Array();
			    $.ajax({
			             statusCode: {
			                200: function(){
			                    console.log("success")
			                }
			             },
			             type: "GET",
			             url: 'http://'+ip+"/YOTTA/DomainTopicAPI/getDomainTopicAll?"+getCookie("NowClass"),
			             data: {},
			             dataType: "json",
			             success: function(data){
						    for(var i=0;i<data.length;i++){
						        nodes[i]={name:data[i].TermName};
						        //console.log(data[i].TermName);
						      }
						    
							
							    //向table中添加关系
							/*
							    for(var i=0;i<edges.length;i++){
							        $("#table").append(
							          "<tr><td>"+edges[i].sourceName+"</td><td>"+edges[i].targetName+
							          "</td><td><button id="+i+" type='button' class='btn btn-info'>Focus</button></td></tr>");
							        }
							*/
							// 动态计算画布   author：石磊
							var mysvgdiv=$("#mysvg")
							var footer=$(".main-footer")
							var mysvgTop=$("#mysvgTop")
							  //画力导向图
							  var width = mysvgdiv.width();  //画布的宽度
							  var height = footer.offset().top-mysvgTop.offset().top-100;   //画布的高度
							  console.log(width+"   "+height)
							  var svg = d3.select("#echarts1")     //选择文档中的body元素
							      .append("svg")          //添加一个svg元素
							      .attr("width", width)       //设定宽度
							      .attr("height", height);    //设定高度
							  var force = d3.layout.force()
							      .nodes(nodes) //指定节点数组
							      .links(edges) //指定连线数组
							      .size([width,height]) //指定作用域范围
							      .linkDistance(270) //指定连线长度
							      .charge([-45])//相互之间的作用力
							      .start();    //开始作用
							
							  //添加连线 
							  var svg_edges = svg.selectAll("line")
							      .data(edges)
							      .enter()
							      .append("line")
							      .attr("sourceid",function(d){
								      return Number(d.source);
							      })
							      .attr("targetid",function(d){
								      return Number(d.target);
							      })
							      .style("stroke","#ccc")
							      .style("stroke-width",0.6);
							     
							  //添加节点 
							/*
							  var svg_nodes = svg.selectAll("circle")
							      .data(nodes)
							      .enter()
							      .append("circle")
							      .attr("r",6)
							      .style("fill",function(d,i){
							         return "#6AB0B8";
							      })
							      .attr("id",function(d,i){
							        return "n"+i;
							     })
							      .call(force.drag);  //使得节点能够拖动
							*/
							
								var svg_nodes = svg.selectAll(".node")
									.data(nodes)
									.enter()
									.append("g")
									.attr("class","node")
									.attr("id",function(d,i){
										return "n"+i;
									})
									.call(force.drag);
								svg_nodes.append("path")
									.attr("d",treeicon)
									.attr("fill","#C4C400")
									.attr("transform","scale(0.05)");
							  //添加描述节点的文字
							  // var svg_texts = svg.selectAll("text")
							  //     .data(nodes)
							  //     .enter()
							  //     .append("text")
							  //     .style("fill", "black")
							  //     .attr("dx", 20)
							  //     .attr("dy", 8)
							  //     .text(function(d){
							  //        return d.name;
							  //     });
							  	svg_nodes.on("mouseover",function(d,i){
								  	d3.select(this)
								  		.append("text")
								  		.style("fill","black")
								  		.attr("dx",30)
								  		.attr("dy",8)
								  		.text(function(d){
									  		return d.name;
								  		});
							  	})
							  	.on("mouseout",function(){
								  	d3.select(this).select("text")
								  		.remove();
							  	});
							  	
							  	svg_nodes.selectAll("path")
							  		.on("mouseover.first",function(d,i){
										d3.select(this)
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
									})
									.on("mouseout",function(d,i){
										d3.select(this)
											.transition()
											.duration(500)
											.attr("fill","#C4C400")
											.attr("transform","scale(0.05)");			
									});	
								var drag=force.drag()
											.on("drag",function(d,i){
													d.fixed=true;
											});
/*
								svg_nodes.on("mouseover.second",function(d,i){
									d.fixed=false;
								});
*/
								svg_nodes.on("dblclick",function(d,i){
									
									$.ajax({
							             statusCode: {
							                200: function(){
							                    console.log("success")
							                }
							             },
							             type: "GET",
							             url: 'http://'+ip+"/YOTTA/AssembleAPI/getTreeByTopic?ClassName="+getCookie("NowClass")+"&TermName="+d.name,
							             data: {},
							             dataType: "json",
							             success: function(data){
										 		if(pd2 == 0){
													pd2=1;
													
													var seed4 = {x: d.x, y: d.y, name:data.name}; 
													var tree4 = buildTree(data, seed4, 0.75);
													draw_tree(tree4, seed4, svg, 0.75);	
													}
											    else{
												    d3.select("g.tree").remove();
												    pd2=0;
											    }
											    										    

								         }
								    });
								});
								d3.select("body")
									.on("keydown",function(){
										if(d3.event.keyCode == 27){
											d3.select("g.tree").remove();
											$("line").attr("style","stroke:#ccc;stroke-width:1;");
// 							d3.select("body").select("#echarts1").select("svg").selectAll("circle").style("fill","#6AB0B8").attr("r",6);
											svg_nodes.selectAll("path")
												.attr("fill","#C4C400")
												.attr("transform","scale(0.05)");
												}
									});	
							  svg_nodes.on("click",function(d,i){
								  console.log(i);
								  
							      var num2=i;
							       //如果非第一次点击则返回初始设置
							      if(pd1==1){
							        $("line").attr("style","stroke:#ccc;stroke-width:1;");
// 							d3.select("body").select("#echarts1").select("svg").selectAll("circle").style("fill","#6AB0B8").attr("r",6);
									svg_nodes.selectAll("path")
										.attr("fill","#C4C400")
										.attr("transform","scale(0.05)");
							      };
							      pd1=1;
							
							      //连线的设置
							      for(var i=0;i<edges.length;i++){
							          if(edges[i].source.index==num2){
							            $("#"+edges[i].source.index+"a"+edges[i].target.index)
							            	.attr("style","stroke:28ff28;stroke-width:1;");
							            d3.select("body")
							            	.select("#echarts1")
											.select("svg")
											.select("#n"+edges[i].source.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							            d3.select("body")
								            .select("#echarts1")
								            .select("svg")
								            .select("#n"+edges[i].target.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							          }
							          if(edges[i].target.index==num2){
							            $("#"+edges[i].source.index+"a"+edges[i].target.index)
							            	.attr("style","stroke:black;stroke-width:1;");
							            d3.select("body")
							            	.select("#echarts1")
											.select("svg")
											.select("#n"+edges[i].source.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							            d3.select("body")
								            .select("#echarts1")
								            .select("svg")
								            .select("#n"+edges[i].target.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							          }
							          if(edges[i].source.index==num2){
							            $("#"+edges[i].source.index+"a"+edges[i].target.index)
							            	.attr("style","stroke:black;stroke-width:1;");
							            d3.select("body")
							            	.select("#echarts1")
											.select("svg")
											.select("#n"+edges[i].source.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							            d3.select("body")
								            .select("#echarts1")
								            .select("svg")
								            .select("#n"+edges[i].target.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							          }
							          if(edges[i].target.index==num2){
							            $("#"+edges[i].source.index+"a"+edges[i].target.index)
							            	.attr("style","stroke:black;stroke-width:1;");
							            d3.select("body")
							            	.select("#echarts1")
											.select("svg")
											.select("#n"+edges[i].source.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							            d3.select("body")
								            .select("#echarts1")
								            .select("svg")
								            .select("#n"+edges[i].target.index)
											.select("path")
											.attr("fill","#28ff28")
											.attr("transform","scale(0.07)");
							          }

							        }																	});
							  force.on("tick", function(){ //对于每一个时间间隔
							      //更新连线坐标
							      svg_edges.attr("x1",function(d){ return d.source.x; })
							          .attr("y1",function(d){ return d.source.y; })
							          .attr("x2",function(d){ return d.target.x; })
							          .attr("y2",function(d){ return d.target.y; })
							          .attr("id",function(d,i){ return ""+d.source.index+"a"+d.target.index});
							      //更新节点坐标
							/*
							      svg_nodes.attr("x",function(d){ return d.x; })
							          .attr("y",function(d){ return d.y; });
							*/
								svg_nodes.attr("transform", function(d) { return "translate(" + (d.x-12) + "," + (d.y-20) + ")"; });
							      // //更新文字坐标
							      // svg_texts.attr("x", function(d){ return d.x; })
							      //    .attr("y", function(d){ return d.y; });
						 });
			 }
			 });
			 }
    

  });
  	$("#search").on("click",function(){
		$.ajax({
             statusCode: {
                200: function(){
                    console.log("success")
                }
             },
             type: "GET",
             url: 'http://'+ip+"/YOTTA/AssembleAPI/getTreeByTopic?ClassName="+getCookie("NowClass")+"&TermName="+$("#subjectSearch").val(),
             data: {},
             dataType: "json",
             success: function(data){
	             var ID = data.term_id-1;
	            d3.select("body")
	            	.select("#echarts1")
					.select("svg")
					.select("#n"+ID)
					.select("path")
					.attr("fill","#28ff28")
					.attr("transform","scale(0.07)");
	             }			
		});
	});

	
});
