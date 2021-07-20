
var start
var end

function setPage(totalPageNum,currentPageNum,query,n) {
    var ul = document.getElementById("pagination")

    start=(parseInt((currentPageNum+4)/5)*5)-4 +(n*5)
    end=start+5

    if(end>totalPageNum+1){
        end=totalPageNum+1
    } 
    if(start < 1){
        start = 1
    }
    if(end==start){
        end+=1
    }
    for(i=start;i<end;i++){


        var li = document.createElement("li")
        li.classList.add("page-item")
        li.classList.add("pages")

        var link = document.createElement("a")
        link.classList.add("page-link")
        link.innerText=i
        if(query ==""||query==null){
            link.href="?page="+i
        }else{
            link.href="?query="+query+"&page="+i
        }
        li.appendChild(link)
        ul.insertBefore(li,ul.lastChild)
    }

}

function plusPage(totalPageNum,query,n){
    
    var ul = document.getElementById("pagination")
    var li = document.getElementsByClassName("pages")
    var len = li.length
    page = (parseInt((start+4)/5)+n)*5-4
    if(page < 1){
        alert ("첫 번째 페이지입니다.")
    }else  if(page > totalPageNum){
        alert ("마지막 페이지입니다.")

    }else{
        for(i=0;i<len;i++) {
            li[0].remove()
        }
        if(query ==""|| query ==null){
            window.location.href="?page="+page
        }else{
            window.location.href="?query="+query+"&page="+page
        }
    }
}