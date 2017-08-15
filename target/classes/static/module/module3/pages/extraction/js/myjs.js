$(document).ready(function(){
            $.ajax(
                    {type :"GET",
                        url :'http://'+ip+"/YOTTA/DomainTopicAPI/getDomainTopicAll?ClassName="+getCookie("NowClass"),
                        datatype :"json",
                        async:false,
                        success : function(data,status){
                            topic=data;
                            console.log("列表topic"+topic.length);
                        }
                    });
            for(var i=0;i<topic.length;i++){
            $("#li").append("<li class='list-group-item'>"+topic[i].TermName+"</li>");
            }
        })
