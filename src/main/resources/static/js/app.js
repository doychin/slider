let ws = null;

const state = {red: 0, green: 0, blue: 0};

function connect() {
    ws = new WebSocket("ws://localhost:8080/value");

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