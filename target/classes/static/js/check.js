var isDuplicated = true;
var originUsername = "";

function signUpFormCheck(){
    const form = document.signUpForm;
    const username = form.username.value;
    const nick = form.nickname.value;
    const email = form.email.value;
    const pw = form.password.value;
    const chpw = form.checkPassword.value;
    const area = form.area.value;

    var ch = true;

    var $up =  document.getElementById("username_message");
    var $pp =  document.getElementById("password_message");
    var $cp =  document.getElementById("checkPassword_message");
    var $np =  document.getElementById("nickname_message");
    var $ep =  document.getElementById("email_message");

    if(username.length < 4 || username.length > 25){
        $up.textContent ="ID는 4자 이상 25자 이하여야 합니다";
        ch = false;
    }else{
        $up.textContent="";
    }
    if(pw.length< 8 || pw.length > 30){
        $pp.textContent ="password는 8자 이상 30자 이하여야 합니다";
        ch = false;
    }else{
        $pp.textContent="";
    }
    if(pw != chpw){
        $cp.textContent ="password와 다릅니다";
        ch = false;
    }else{
        $cp.textContent="";
    }
    if(nick.length< 4 || pw.length > 20){
        $np.textContent ="nickname은 4자 이상 20자 이하여야 합니다";
        ch = false;
    }else{
        $np.textContent="";
    }
    var regExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
    if(email.match(regExp) == null){
        $ep.textContent ="이메일 형식이 맞지 않습니다"; 
        ch = false;
    }else{  
        $ep.textContent="";
    }
    if(isDuplicated){
        $up.textContent ="ID 중복 확인이 필요합니다";
        ch = false;
    }
    if(ch ==true){
        document.getElementById("username").value=originUsername;
        alert("sucsess");
        form.submit();
    }
    return false;

}


function checkUsername(){
    
    const $username = document.getElementById("username");
    var $up =  document.getElementById("username_message");
    var data = {"username":$username.value};
    if($username.value.length < 4 || $username.value.length > 25){
        $up.textContent ="ID는 4자 이상 25자 이하여야 합니다";
    }else{
        $.ajax({
        type:"post",
        url:"/user/checkUsername",
        data:data,
        success:function(result){
            if(result){
                alert("이미 존재하는 ID입니다");
            }else{
                alert("사용 가능한 ID입니다");
                $username.readOnly = true;
                $username.style.backgroundColor = "#D8D8D8"
                isDuplicated = false;
                originUsername=$username.value;
            }
        }});
    }
    
}

var checkProduct = true
function checkProductForm(){
    const form = document.createProductForm;

    var $title =  document.getElementById("title");
    var $content =  document.getElementById("content");
    var $price =  document.getElementById("price");

    var $titleMessage = document.getElementById("title_message")
    var $contentMessage = document.getElementById("content_message")
    var $priceMessage = document.getElementById("price_message")


    if($title.value==""){
        $titleMessage.innerText="내용이 작성되지 않았습니다"
        checkProduct= false;
    }else{
        $titleMessage.innerText=""
        checkProduct = true
    }
    if($content.value==""){
        $contentMessage.innerText="내용이 작성되지 않았습니다"
        checkProduct= false;

    }else{
        $titleMessage.innerText=""
        checkProduct = true
    }
    if($price.value==""){
        $priceMessage.innerText="내용이 작성되지 않았습니다"
        checkProduct= false;

    }else{
        $titleMessage.innerText=""
        checkProduct = true
    }
    if(checkProduct==true){
        form.submit()
        return true;
    }
}