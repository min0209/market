function selected(area){
    var $select =  document.getElementById("select-area");
    Array.prototype.forEach.call($select,function($sel){
        if($sel.value==area){
            $sel.selected=true;
            return;
        }
    })
}   