function calculate_branch_xy(num_branchs, i, root, multiple,name_branch){
    var level = (i<(num_branchs/2))? (i+1):(num_branchs-i); // branch的高度， 1为最顶端, num_branchs/2为最底端
	var location = ((i<(num_branchs/2))?'left':'right');
	//(num_branchs/2-i) 和 (i+1-num_branchs/2)），从最顶端往下计算branch长度，保证树冠的形状
	var length_first_branch = ((location == "left") ? (length_top_branch*(num_branchs/2-i)):
							  (length_top_branch*(i+1-num_branchs/2))); 
	//(num_branchs/2-i) 和 (i+1-num_branchs/2)），从最顶端往下计算角度，保证树冠的形状
	var a = (location == "left")?(-Math.PI *(angle_top_branch+ angle_step_branch * (num_branchs/2-i))):
			(Math.PI * (angle_top_branch+angle_step_branch*(i+1-num_branchs/2))); 
	var location_center=root.x+width_trunk* multiple*num_branchs/2;
	var x2 = (location == "left") ? (location_center-(num_branchs/2-i)*width_trunk* multiple/2):(location_center + (i-num_branchs/2)*width_trunk* multiple/2);
	var y2 = root.y - (level * step_trunk_height + base_trunk_height) * multiple;
	var x3 = ((location == "left") ? (x2 - length_first_branch* multiple):(x2 + length_first_branch* multiple));
	var y3 = y2;
	//P4的位置在最短处，叶子往上生长	
	var x4 = x3 + length_x3x4 * multiple* Math.sin(a);
	var y4 = y3 - length_x3x4 * multiple* Math.cos(a);	
	var mx = x2;
	var my = y2 + length_trunk_to_branch* multiple;
	var cx1 = x2;
	var cy1 = y2; 
	var cx2 = x3;
	var cy2 = y3;	
	var cx3 = ((x3+x4)/2 + x4)/2;
	var cy3 = ((y3+y4)/2 + y4)/2;	
    var d,dx,dy=12;	
	var id = root.name+String(i), textpath = '#'+id;	
	var name = name_branch;
	if(location == "left"){ //左边的分支 文字的偏移程度
		d = "M"+cx3+" "+cy3+" C"+cx2+" "+cy2+" "+cx1+" "+cy1+" "+mx+" "+my;
		dx = 10*multiple;				
	}else{ //右边的分支
		d ="M"+mx+" "+my+" C"+cx1+" "+cy1+" "+cx2+" "+cy2+" "+cx3+" "+cy3;
		dx = 30*multiple;	
	}	
	//旋转顺序不同，确保文字的走向；
	return {width:width_branch,id:id,  type:'branch', name: name,textpath:textpath,d:d,x:root.x+i*width_trunk* multiple, y:root.y, x2:mx, y2:my, x3:x3, y3:y3, x4:x4, y4:y4, a:a, i:i, mx:mx,my:my,cx1:cx1,cy1:cy1,cx2:cx2, cy2:cy2,cx3:cx3,cy3:cy3,dx:dx,dy:dy};
	//return {id:'path'+root.name+'_'+String(i),d:"M"+(root.x + i)+" "+root.y+" L"+x2+" "+y2+" L"+x3+" "+y3+" L"+x4+" "+y4, x:root.x+i, y:root.y, x2:mx, y2:my, x3:x3, y3:y3, x4:x4, y4:y4, a:a,type:'branch', name: data['children'][i].name, i:i, mx:mx,my:my,cx1:cx1,cy1:cy1,cx2:cx2, cy2:cy2,cx3:cx3,cy3:cy3,dx:dx,dy:dy,transform:transform};
}
function calculate_twig_xy(parent, num_branchs, i, num_twig, j,root, multiple,name_twig){	
	if(num_twig==1){
		return {id:parent.id, d:parent.d, x3:(parent.x3+parent.x4)/2,y3:(parent.y3+parent.y4)/2,a:parent.a, type:'branch',name: parent.name, i:i};
	}
	var location_branch = ((i<(num_branchs/2))?'left':'right');
	var location_twig = ((j<(num_twig/2))?'bottom':'top');
	var count = (location_twig == "bottom")? (j*multiple):(num_twig-j-1)*multiple; //确定每片叶子的具体位置时用到count
	var a3 = (location_twig == 'top')?(parent.a-angle_twig_step_a3):(parent.a+angle_twig_step_a3);
	var a4 =(location_twig == 'top')?(parent.a -angle_twig_step_a4):(parent.a+angle_twig_step_a4);
	var start_twig_x, start_twig_y; 
	if(j==0 || j==num_twig-1){
		start_twig_x = parent.x4;
		start_twig_y = parent.y4;
	}else{
		start_twig_x = parent.x3 + (length_base_start_twig + twig_space*(count)) * multiple* Math.sin(parent.a);;//从最靠近主干的地方计算叶子位置
		start_twig_y = parent.y3 - (length_base_start_twig + twig_space*(count)) * multiple* Math.cos(parent.a);
	}	
	var x3 = start_twig_x + fixedTwig * multiple* Math.sin(a3);
	var y3 = start_twig_y - fixedTwig * multiple*  Math.cos(a3);
	var x4 = x3 + twigLength * multiple* Math.sin(a4);
	var y4 = y3 - twigLength * multiple* Math.cos(a4);	
	var mx, my, n;
	if (j==1 || j==num_twig-2){
		n = multiple;
	}else {
		n=multiple*2;
	}
	if(j==0 || j==num_twig-1){		
		mx = ((parent.x3+start_twig_x)/2 + start_twig_x)/2;
		my = ((parent.y3+start_twig_y)/2 + start_twig_y)/2;		
	}else{		
		mx = parent.x3 + (length_base_start_twig + twig_space*count-twig_space*n)* multiple * Math.sin(parent.a);//从最靠近主干的地方计算叶子位置
		my = parent.y3 - (length_base_start_twig + twig_space*count-twig_space*n)* multiple * Math.cos(parent.a);		
	}
	var cx1 = start_twig_x;
	var cy1 = start_twig_y;
	var cx2 = x3;
	var cy2 = y3;
	var cx3 = x4;
	var cy3 = y4;	
	var id = parent.id+'_'+String(j);
	var textpath = '#'+id;;
	var d, dx, dy = 0;
	var name = name_twig;	
	if(location_branch == 'left'){ //左边分枝上的twig，只有multiple！=1时才会显示tie上的文字
		dx = 5+3*multiple;	
		d = "M"+cx3+" "+cy3+" C"+cx2+" "+cy2+" "+cx1+" "+cy1+" "+mx+" "+my;
	}else{ //右边的分支
		//dx = 15*multiple*(count+1);
		dx = 8+3*multiple;
		d = "M"+mx+" "+my+" C"+cx1+" "+cy1+" "+cx2+" "+cy2+" "+cx3+" "+cy3;
	}	
	return {width:width_twig,d:d, id:id,type:'branch',name:name,textpath:textpath, 
			cx2:cx2, cy2:cy2,x3:x3,y3:y3,x4:x4,y4:y4, a:a4,dx:dx, dy:dy};	
	//return {d:d, type:'twig', id:id, name:name, cx2:cx2, cy2:cy2,x5:x5,y5:y5,x6:x6,y6:y6, a:a6,flag:flag};
}


function calculate_leaf_xy(parent, num_leaves, k, multiple,level,leaf_id,leaf_content,url){	
	var location_leaf = ((k<(num_leaves/2))?'bottom':'top');
	var count = (location_leaf == "bottom")? (k* multiple):(num_leaves-k-1)* multiple; //确定每片叶子的具体位置时用到count
	var a3 = (location_leaf == 'top')?(parent.a-angle_leaf_step_a3):(parent.a+angle_leaf_step_a3);
	var a4 =(location_leaf == 'top')?(parent.a -angle_leaf_step_a4):(parent.a+angle_leaf_step_a4);
	var start_leaf_x, start_leaf_y ;
	var mx, my, n = space_time_leaf*multiple;	
	if(level== 0){
		start_leaf_x = parent.x3 + (length_start_leaf_x_level_0+leaf_space*(count)) * multiple* Math.sin(parent.a);//从最靠近主干的地方计算叶子位置
		start_leaf_y = parent.y3 - (length_start_leaf_x_level_0+leaf_space*(count)) * multiple* Math.cos(parent.a);
		mx = parent.x3 + (length_start_leaf_x_level_0+ leaf_space*count-leaf_space*3.0) * multiple* Math.sin(branch.a);
		my = parent.y3 - (length_start_leaf_x_level_0 + leaf_space*count-leaf_space*3.0) * multiple* Math.cos(branch.a);
	//}else if(level== 1){
	}else{
		start_leaf_x = parent.x3 + (length_start_leaf_x_level_1 + leaf_space*(count)) * multiple* Math.sin(parent.a);//从最靠近主干的地方计算叶子位置
		start_leaf_y = parent.y3 - (length_start_leaf_x_level_1 + leaf_space*(count)) * multiple* Math.cos(parent.a);
		mx = parent.x3 + (length_start_leaf_x_level_1 + leaf_space*count-leaf_space*n) * multiple* Math.sin(parent.a);//从最靠近主干的地方计算叶子位置
		my = parent.y3 - (length_start_leaf_x_level_1 + leaf_space*count-leaf_space*n) * multiple* Math.cos(parent.a);	
	}
	if(k==0 || k==num_leaves-1){ 
		mx = ((parent.x3+start_leaf_x)/2 + start_leaf_x)/2;
		my = ((parent.y3+start_leaf_y)/2 + start_leaf_y)/2;
	}
	var x3 = start_leaf_x + fixedLeaf * multiple * Math.sin(a3);
	var y3 = start_leaf_y - fixedLeaf * multiple *  Math.cos(a3);
	var x4 = x3 + leafLength * multiple* Math.sin(a4);
	var y4 = y3 - leafLength * multiple* Math.cos(a4);	
	var cx1 = start_leaf_x;
	var cy1 = start_leaf_y;
	var cx2 = x3;
	var cy2 = y3;
	var cx3 = x4;
	var cy3 = y4;
	var d = "M"+mx+" "+my+" C"+cx1+" "+cy1+" "+cx2+" "+cy2+" "+cx3+" "+cy3;
	var id = parent.id+'_'+String(k);
	var name = leaf_content;	
	return {width:width_leaf*multiple,d:d, type:'leaf', id:id, name:name, cx2:cx2, cy2:cy2,parent:parent,url:url};
	//return {d:"M"+cx1+" "+cy1+" L"+x3+" "+y3+" L"+x4+" "+y4,type:'leaf'};
}
// functions for draw
function highlight(d) {	
	$(".qtip:hidden").remove();
	var colour, width;
	if(d.type=='leaf'){
	 colour= d3.event.type == 'mouseover' ? color_hilight_leaf : color_leaf;
	 width =  d3.event.type == 'mouseover' ? width_hilight_leaf*multiple : width_leaf*multiple;
	}else{
	 colour= d3.event.type == 'mouseover' ?color_hilight_twig : color_twig;
	 width =  d3.event.type == 'mouseover' ? width_hilight_twig*multiple : width_twig*multiple;
	}	
	var display = d3.event.type =='mouseover' ? null : 'none';
	var type = d.type;
	if(multiple<0.75){return;}//保证缩小时只显示分面树的主题，不高亮显示twig和叶子的内容	
	d3.select('#'+d.id).style('stroke', colour );
	d3.select('#'+d.id).style('stroke-width', width);	
	//显示超文本	
	$(this).qtip(
	  {
		 content: {						
			text: '<p>'+d.name+'</p><p><a href='+d.url+' target="_blank">Detail</a></p>',	
			title: { text: d.id } // Give the tooltip a title using each elements text				
		 },
		 position: {
			corner: {
			   target: 'middleMiddle', // Position the tooltip above the link
			   tooltip: 'bottomMiddle'			   
			},
			adjust: {  screen: true }// Keep the tooltip on-screen at all times			
		 },
		 show: { 
            when: 'click', 
            solo: true // Only show one tooltip at a time
         },
		 hide: 'unfocus',		
		 style: {
			tip: true, // Apply a speech bubble tip to the tooltip at the designated tooltip corner
			border: {
			   width: 0,
			   radius: 2
			},			
			width: width_qtip // Set the tooltip width
		 }
	  });		
}