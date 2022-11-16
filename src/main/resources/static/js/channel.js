//add member
window.addEventListener("load", () => {
    function addMember() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        var jsonData = JSON.stringify({
            "channelId": channelId,
            "username": formData.get("username")
        });
        
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PUT", "/channel/add/member");
        req.setRequestHeader("Content-Type", "application/json");
        req.send(jsonData);
    }

    var form = document.getElementById("addMember");
    var channelId = document.getElementById("channelId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        addMember();
    });
});
//remove member from channel
window.addEventListener("load", () => {
    function removeMember(username) {
        var req = new XMLHttpRequest;
        var jsonData = JSON.stringify({
            "channelId": channelId,
            "username": username
        });

        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });

        req.open("DELETE", "/channel/remove/member");
        req.setRequestHeader("Content-Type", "application/json");
        req.send(jsonData);
    }

    var members = Array.from(document.getElementsByClassName("removeMember"));
    var channelId = document.getElementById("channelId").getAttribute("value");

    members.forEach(member => {
        member.addEventListener("click", (event) => {
            event.preventDefault();
            removeMember(member.id);
        });
    });
});
//join channel
window.addEventListener("load", () => {
    function joinChannel() {
        var req = new XMLHttpRequest;
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        
        req.open("PUT", "/channel/join");
        req.send(channelId);
    }

    var form = document.getElementById("joinChannel");
    var channelId = document.getElementById("channelId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        joinChannel();
    });
});
//Leave channel
window.addEventListener("load", () => {
    function leaveChannel() {
        var req = new XMLHttpRequest;
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        
        req.open("DELETE", "/channel/leave");
        req.send(channelId);
    }

    var form = document.getElementById("leaveChannel");
    var channelId = document.getElementById("channelId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        leaveChannel();
    });
});
//Create new article
window.addEventListener("load", () => {
    function newArticle() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        formData.append("channel", channelId);
        req.open("POST", "/channel/article/create");
        req.send(formData);
    }

    var form = document.getElementById("createArticle");
    var channelId = document.getElementById("channelId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        newArticle();
    });
});
// Create new room
window.addEventListener("load", () => {
    function newRoom() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        formData.append("channel", channelId);
        req.open("POST", "/channel/room/create");
        req.send(formData);
    }

    var form = document.getElementById("createRoom");
    var channelId = document.getElementById("channelId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        newRoom();
    });
});
