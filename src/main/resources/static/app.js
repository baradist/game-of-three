var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    // $("#games").html("");
}

function connect() {
    var socket = new SockJS('/game-of-three-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/games', function (game) {
            showGame(JSON.parse(game.body).content);
        });
        stompClient.subscribe('/user/queue/games', function (currentGame) {
            showCurrentGame(JSON.parse(currentGame.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGame(message) {
    $("#games").append("<tr><td>" + message + "</td></tr>");
}

function showCurrentGame(message) {
    $("#current_game").append("<tr><td>" + message + "</td></tr>"); // TODO
}

function sendDecrease() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'number': $("#number").val(),
        'action': "-1"
    }));
}

function sendDontChange() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'number': $("#number").val(),
        'action': "0"
    }));
}

function sendIncrease() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'number': $("#number").val(),
        'action': "1"
    }));
}

function sendCreate() {
    stompClient.send("/app/games/create", {}, JSON.stringify({
        'sum': $("#new_game_sum").val()
    }));
}

function sendJoin() {
    stompClient.send("/app/games/join", {}, JSON.stringify({
        'gameId': $("#game_id_to_join").val()
    }));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });

    $("#send_decrease").click(function () {
        sendDecrease();
    });
    $("#send_dont_change").click(function () {
        sendDontChange();
    });
    $("#send_increase").click(function () {
        sendIncrease();
    });
    $("#send_create").click(function () {
        sendCreate();
    });
    $("#send_join").click(function () {
        sendJoin();
    });
});

$(document).ready(function () {
    $.ajax({
        url: "/api/game",
        context: document.body,
        success: function (games) {
            // $("#games").html(games);
            updateTable(games_table, games)
        }
    });
});

function updateTable(updatableTable, jsonData) {

    var tableHTML = "<tr>";
    for (var headers in jsonData[0]) {
        tableHTML += "<th>" + headers + "</th>";
    }
    tableHTML += "</tr>";

    for (var eachItem in jsonData) {
        tableHTML += "<tr>";
        var dataObj = jsonData[eachItem];
        for (var eachValue in dataObj) {
            tableHTML += "<td>" + dataObj[eachValue] + "</td>";
        }
        tableHTML += "</tr>";
    }

    updatableTable.innerHTML = tableHTML;
}
