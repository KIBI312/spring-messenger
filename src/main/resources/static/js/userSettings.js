// Update profile picture
window.addEventListener("load", () => {
    function updateProfilePic() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PATCH", "/user/update/profilePic");
        req.send(formData);
    }

    var form = document.getElementById("updateProfilePic");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        updateProfilePic();
    });
});

// Update password
window.addEventListener("load", () => {
    function updatePassword() {
        var req = new XMLHttpRequest;
        var formData = new FormData(form);
        req.addEventListener("load", (event) => {
            alert(event.target.responseText);
        });

        req.addEventListener("error", (event) => {
            alert("OOPS");
        });
        req.open("PATCH", "/user/update/password");
        req.send(formData);
    }

    var form = document.getElementById("updatePassword");

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        updatePassword();
    });
});