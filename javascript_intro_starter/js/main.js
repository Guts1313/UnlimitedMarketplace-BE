// An array of objects, which each represent a user account
const userAccounts = [
    {
        username: 'john@example.com',
        password: 'john'
    },
    {
        username: 'sarah@example.com',
        password: 'sarah'
    },
    {
        username: 'hector@example.com',
        password: 'hector'
    }
];

console.log('The user accounts have been initialised!');
const loginForm = document.getElementById(`login-form`);

loginForm.addEventListener("submit", () => {
    event.preventDefault()
    const username = document.getElementById("login-input-username").value
    const password = document.getElementById("login-input-password").value
    let isMatchFound = false;
    for (let i=0; i <userAccounts.length;i++){
        if (username === userAccounts[i].username && password === userAccounts[i].password) {
            isMatchFound = true
            break
        }
        else {
            console.log("Login failed");
        }
    }
    if (isMatchFound){
        console.log("We have a match,login success");
    }
    else {
        console.log("Failed login");
    }

})

const clearButton = document.getElementById("login-button-clear");
clearButton.addEventListener("click",() =>{
    document.getElementById("login-input-username").value = '';
    document.getElementById("login-input-password").value = '';
})

const logUsernames = document.getElementById("login-button-log-usernames");
logUsernames.addEventListener("click",() => {
    userAccounts.forEach((element) => console.log(element.username))
})
console.log(userAccounts)



