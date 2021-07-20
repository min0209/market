function bookmark(pid){
    $.ajax({
        type:"get",
        url:"/product/"+pid+"/bookmark",
        data:{"pid":pid},
        success:function(result){
            if(result.result_code == "OK"){
                var $heart = document.getElementById("heart_"+pid);
                console.log(result.data.bookmarked)
                if(result.data.bookmarked){
                    $heart.classList.remove("far")
                    console.log($heart.classList)
                    $heart.classList.add("fa")
                }else{
                    $heart.classList.remove("fa")
                    $heart.classList.add("far")
                }
                $heart.innerText=result.data.bookmark_count
                console.log($heart.classList)
            }else{
                alert(result.description)
            }
        }
    });
}

function setBookmark(products){
    var $hearts = document.getElementsByClassName("fa-heart");
    if(products != null){
        Array.prototype.forEach.call(products, function(product,index){
            if(product.bookmarked){
                $hearts[index].classList.remove("far")
                $hearts[index].classList.add("fa")
            }else{
                $hearts[index].classList.remove("fa")
                $hearts[index].classList.add("far")
            }
        })
    }

}

function setBookmarkOne(product){
    var $heart = document.getElementsByClassName("fa-heart")[0];
    if(product.bookmarked){
        $heart.classList.remove("far")
        $heart.classList.add("fa")
    }else{
        $heart.classList.remove("fa")
        $heart.classList.add("far")
    }
}