//Send friend request
window.addEventListener("load", () => {
    function sendFriendRequest() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);

        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("POST", "/friend/add");
        req.send(formData.get("username"));
    }
    var form = document.getElementById("sendFriendRequest");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        sendFriendRequest();
    });
});

//Accept friend request
window.addEventListener("load", () => {
    function acceptFriend(username) {
        var req = new XMLHttpRequest;
    
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PATCH", "/friend/accept");
        req.send(username);
    }

    var accepts = Array.from(document.getElementsByClassName("acceptFriend"));

    accepts.forEach(accept => {
        accept.addEventListener("click", (event) => {
            event.preventDefault();
            acceptFriend(accept.id);
        });
    });
});

//Create new channel
window.addEventListener("load", () => {
    function createChannel() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("POST", "/channel/create");
        req.send(formData);
    }

    var form = document.getElementById("createChannel");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        createChannel();
    });
});
