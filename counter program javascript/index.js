const decreasebtn = document.getElementById("decreasebutton");
const increasebtn = document.getElementById("increasebutton");
const resetbtn = document.getElementById("resetbutton");
const labelcount = document.getElementById("count");
let count = 0

increasebtn.onclick = function(){
    count++
    labelcount.textContent = count;
}

decreasebtn.onclick = function(){
    count -=1;     /* decrease by one */
    labelcount.textContent = count;
}

resetbtn.onclick = function(){
    count = 0;     /* resetto 0*/
    labelcount.textContent = count;
}