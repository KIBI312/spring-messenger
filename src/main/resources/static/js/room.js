// Send new message
window.addEventListener("load", () => {
    function newMessage() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        formData.append("room", roomId);
        req.open("POST", "/channel/room/message/create");
        req.send(formData);
    }

    var form = document.getElementById("sendMessage");
    var roomId = document.getElementById("roomId").getAttribute("value");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        newMessage();
    });
});
// Remove message
window.addEventListener("load", () => {
    function removeMessage(messageId) {
        var req = new XMLHttpRequest;

        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });

        req.open("DELETE", "/channel/room/message/remove");
        req.send(messageId);
    }

    var messages = Array.from(document.getElementsByClassName("removeMessage"));

    messages.forEach(message => {
        message.addEventListener("click", (event) => {
            event.preventDefault();
            removeMessage(message.id);
        });
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


