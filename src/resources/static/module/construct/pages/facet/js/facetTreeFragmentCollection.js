
function DisplayTrunk(dataset){

	//document.getElementById("RightfacetTree").innerHTML='';
   $("#RightfacetTree").empty();
	var datas = [];	
	multiple=1;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#RightfacetTree")
				.append("svg")
				.attr("width", $("#RightfacetTree").width() * multiple)
				.attr("height",$("#RightfacetTree").height() * multiple);
	//分面树的位置
	$("svg").draggable();	
	var seed4 = {x: $("#RightfacetTree").width()*0.5* multiple, y:($("#RightfacetTree").height()-60)* multiple, name:dataset.name}; 
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
	document.getElementById("RightfacetTree").innerHTML='';
	var datas = [];	
	multiple=0.9;
	datas.push(dataset);
	//分面树所占空间大小
	svg = d3.select("div#RightfacetTree")
				.append("svg")
				.attr("width", $("#RightfacetTree").width())
				.attr("height",$("#RightfacetTree").height());
	//分面树的位置	
	$("svg").draggable();
	var seed = {x: $("#RightfacetTree").width()*0.5, y: $("#RightfacetTree").height()-30, name:dataset.name}; 
	var tree = buildBranch(dataset, seed, multiple);
  draw_tree(tree, seed, svg, multiple);
}





