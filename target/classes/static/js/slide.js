function readMultipleImage(input) {
 
    const multipleContainer = document.getElementById("slideshow-container")
    const dotContainer = document.getElementById("prev_image_dot")
    // 인풋 태그에 파일들이 있는 경우
    if(input.files) {
        // 이미지 파일 검사 (생략)
        console.log(input.files)
        // 유사배열을 배열로 변환 (forEach문으로 처리하기 위해)
        const fileArr = Array.from(input.files)
        fileArr.forEach((file, index) => {
            const $colDiv = document.createElement("div")
            $colDiv.classList.add("mySlides")
            const reader = new FileReader()
            const $img = document.createElement("img")
            $img.classList.add("product_upload_image")

            reader.onload = e => {
                $img.src = e.target.result
            }
            
            console.log(file.name)
            $colDiv.appendChild($img)

            reader.readAsDataURL(file)
            multipleContainer.appendChild($colDiv)

            // create dot
            if(index<fileArr.length){
            
                const $span = document.createElement("span")
                $span.classList.add("dot")
                $span.onclick = function(){currentSlide(index+1)}

                dotContainer.appendChild($span)
            }
        })
    }
    currentSlide(1)
}

const inputMultipleImage = document.getElementById("input-multiple-image")
if(inputMultipleImage){
    inputMultipleImage.addEventListener("change", e => {
        readMultipleImage(e.target)
    })
}


var slideIndex = 1;
createDot()
showSlides(slideIndex);

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if (n > slides.length) {slideIndex = 1}    
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";  
  }

  for (i = 0; i < dots.length; i++) {
      dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";  
  dots[slideIndex-1].className += " active";

}
function createDot(){
    var slides = document.getElementsByClassName("mySlides");
    var dots = document.getElementsByClassName("dot");
    const dotContainer = document.getElementById("prev_image_dot")
    if(!dots || dots.length == 0){
        const slideArr = Array.from(slides)
        slideArr.forEach((slide,index) =>{
            const $span = document.createElement("span")
            $span.classList.add("dot")
            $span.onclick = function(){currentSlide(index)}
            
            dotContainer.appendChild($span)
        })
    }    
}

function delImage(pid){
    $.ajax({
        type:"post",
        url:"/product/deleteImage",
        data:{'pid':pid},
        success:function(result){
            if(result){           
                const $reset = document.getElementsByClassName("mySlides")
                Array.prototype.forEach.call($reset, function(el){
                    el.remove();
                })
                const $dots = document.getElementsByClassName("dot")
                Array.prototype.forEach.call($dots,function(el){
                    el.remove();
                })
            }else{
                alert("오류 발생");
            }
        }
    });
}
function reUp(pid){
    $.ajax({
        type:"get",
        url:"/product/"+pid+"/reUp",
        success:function(result){
            if(result){           
                alert("성공")
            }else{
                alert("오류 발생");
            }
        }
    });
}
function deleteProduct(pid){
    $.ajax({
        type:"delete",
        url:"/product/"+pid,
        data:{"pid":pid},
        success:function(result){
            if(result){         
                alert("성공");  
                window.location.href="/product"
            }else{
                alert("오류 발생");
            }
        }
    });
}