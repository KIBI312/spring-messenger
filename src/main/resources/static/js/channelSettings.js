window.addEventListener("load", () => {
    // remove User
    function removeUser(username) {
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
    // promote User
    function promoteUser(username) {
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

        req.open("PATCH", "/channel/promote/member");
        req.setRequestHeader("Content-Type", "application/json");
        req.send(jsonData);
    }

     // demote User
    function demoteUser(username) {
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

        req.open("PATCH", "/channel/demote/member");
        req.setRequestHeader("Content-Type", "application/json");
        req.send(jsonData);
    }


    var channelId = document.getElementById("channelId").getAttribute("value");
    var modal = document.getElementById("manageUser");
    var modalButtonRemove = document.getElementById("removeUser");
    var modalButtonPromote = document.getElementById("promoteUser");
    var modalButtonDemote = document.getElementById("demoteUser");
    var target;

    modal.addEventListener("show.bs.modal", (event) => {
        target = document.activeElement;
    });

    modalButtonRemove.addEventListener("click", (event) => {
        removeUser(target.id);
    });

    modalButtonPromote.addEventListener("click", (event) => {
        promoteUser(target.id);
    });

    modalButtonDemote.addEventListener("click", (event) => {
        demoteUser(target.id);
    });

});

// Update channel profile picture
window.addEventListener("load", () => {
    function updateProfilePicture() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PATCH", "/channel/update/profilePic");
        req.send(formData);
    }

    var form = document.getElementById("updateProfilePic");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        updateProfilePicture();
    });
});

// Update channel details
window.addEventListener("load", () => {
    function updateChannelDetails() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PATCH", "/channel/update/details");
        req.send(formData);
    }

    var form = document.getElementById("updateChannel");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        updateChannelDetails();
    });
});

// Delete channel
window.addEventListener("load", () => {
    function deleteChannel() {
        var req = new XMLHttpRequest;

        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });

        req.open("DELETE", "/channel/delete");
        req.send(channelId);
    }

    var button = document.getElementById("deleteChannel");
    var channelId = document.getElementById("channelId").getAttribute("value");

    button.addEventListener("click", (event) => {
        event.preventDefault();
        deleteChannel();
    });
});
