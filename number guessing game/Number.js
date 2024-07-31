const minum = 1;
const maxnum = 100;
const answer = Math.floor(Math.random()*(100)) +1;
let attempts = 0;
let guess;
let running = true;
while(running){
    guess = window.prompt(`GUESS A NUMBER FROM 1 TO 100`);
    guess= Number(guess);

    if(isNaN(guess)){ //isNAN Is an inbuilt function which checks if something is not a number
       window.alert(`please enter a vaid number`);
    }
    else if(guess < 0 || guess > 100){
        window.alert(`please enter a vaid number`);
    }
    else{
        attempts++;
        if(guess < answer){
            window.alert(`to low ,try again , `);
        }
        else if(guess > answer){
            window.alert(`too high,try again`);
        }
        else{
            window.alert(`you got the answer correct which is ${answer}`);
            running = false;
        }
    }
}
console.log(answer);