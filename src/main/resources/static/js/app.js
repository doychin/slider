let ws = null;

const state = {red: 0, green: 0, blue: 0};

function connect() {
    function calculateWsUrl() {
        let protocol = window.location.protocol === "https:" ? "wss" : "ws";
        let path = window.location.pathname;
        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }
        return protocol + "://" + window.location.host + path + "value";
    }

    let url = calculateWsUrl();
    ws = new WebSocket(url);

    ws.onopen = function () {
        ["red", "green", "blue"].forEach(function (value) {
            document.getElementById(value).disabled = false;
            document.getElementById(value).value = state[value];
        })
    }

    ws.onmessage = function (event) {
        let panel = document.getElementById("panel");

        let data = event.data.toString();
        if (data !== "error") {
            panel.style.backgroundColor = data;
            panel.innerHTML = "";
        } else {
            panel.innerHTML = "server error";
        }
    }

    ws.onerror = function () {
        alert("connection error");
        ws.close();
    }
}

function sendState() {
    ws.send(JSON.stringify(state));
}

function update(name) {
    const element = document.getElementById(name);
    if (!element.checkValidity()) {
        return;
    }
    state[name] = parseInt(element.value);
    sendState();
}

connect();