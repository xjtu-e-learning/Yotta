var labelType, useGradients, nativeTextSupport, animate;
$(document).ready(function(){
            $.ajax(
                    {type :"GET",
                        url :"http://123.139.159.38:9218/YOTTA/DomainTopicAPI/getTopicRelation?ClassName=%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84&initTopic=%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84",
                        datatype :"json",
                        async:false,
                        success : function(data,status){
                            topic1=data;
                            console.log(topic1);
                        }
                    })

        })

function init(){
    //init data
    var json=topic1;
    //end
    //init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        orientation: 'left', 
        align:"center" ,
        //id of viz container element
        injectInto: 'infovis',
        //set duration for the animation
        duration: 300,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 40,
        //enable panning
        Navigation: {
          enable:true,
          panning:true
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            height: 40,
            width: 170,
            type: 'rectangle',
            color: '#23A4FF', 
            overridable: true
        },
        
        Edge: {
            type: 'bezier',
            overridable: true,
            color: '#23A4FF',
        },
        
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){
            label.id = node.id;            
            label.innerHTML = node.name;
            label.onclick = function(){
                // if(normal.checked) {
                  st.onClick(node.id);
                // } else {
             //    st.setRoot(node.id, 'animate');
                // }
            };
            //set label styles
            var style = label.style;
            style.width = 170 + 'px';
            style.height = 50 + 'px';            
            style.cursor = 'pointer';
            style.color = 'black';
            style.fontSize = '1.5em';
            style.textAlign= 'center';
            style.paddingTop = '2px';
        },
        
        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            // if (node.selected) {
                node.data.$color = "#23A4FF";
            // }
            // else {
            //     delete node.data.$color;
            //     //if the node belongs to the last plotted level
            //     if(!node.anySubnode("exist")) {
            //         //count children number
            //         var count = 0;
            //         node.eachSubnode(function(n) { count++; });
            //         //assign a node color based on
            //         //how many children it has
            //         node.data.$color = ['#23A4FF', '#23A4FF', '#23A4FF', '#23A4FF', '#23A4FF', '#23A4FF'][count];                    
            //     }
            // }
        },
        
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#23A4FF";
                adj.data.$lineWidth = 4;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    //load json data
    st.loadJSON(json);
    //compute node positions and layout
    st.compute();
    //optional: make a translation of the tree
    st.geom.translate(new $jit.Complex(-200, 0), "current");
    //emulate a click on the root node.
    st.onClick(st.root);
    //end
    //Add event handlers to switch spacetree orientation.


}