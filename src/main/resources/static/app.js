var stompClient = null;

var currentUser = null
var currentGame = null
var gameList = null

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
        currentUser = frame.headers['user-name']

        stompClient.subscribe('/topic/games', function (message) {
            var game = JSON.parse(message.body)

            if (game.player1 == currentUser || game.player2 == currentUser) {
                currentGame = game
                redrawCurrentGame();
            }
            if (game.player2 == null) { // game started
                addGame(game)
            } else { // game joined
                for (var i = 0; i < gameList.length; i++) {
                    if (gameList[i].id == game.id) {
                        gameList[i] = game
                    }
                }
                updateTable(games_table, gameList)
            }
        });
        stompClient.subscribe('/user/queue/games', function (currentGame) {
            showMove(JSON.parse(currentGame.body));
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

function redrawCurrentGame() {
    updateTable(current_game, [currentGame])
    // $("#current_game").innerHTML = "<tbody>\n" +
    //     "                <tr>\n" +
    //     "                    <th>id</th>\n" +
    //     "                    <th>player1</th>\n" +
    //     "                    <th>player2</th>\n" +
    //     "                    <th>nextTurn</th>\n" +
    //     "                    <th>sum</th>\n" +
    //     "                    <th>finished</th>\n" +
    //     "                    <th>winner</th>\n" +
    //     "                </tr>\n" +
    //     "                <tr>\n" +
    //     "                    <td>" + currentGame.id + "</td>\n" +
    //     "                    <td>" + currentGame.player1 + "</td>\n" +
    //     "                    <td>" + currentGame.player2 + "</td>\n" +
    //     "                    <td>" + currentGame.nextTurn + "</td>\n" +
    //     "                    <td>" + currentGame.sum + "</td>\n" +
    //     "                    <td>" + currentGame.finished + "</td>\n" +
    //     "                    <td>" + currentGame.winner + "</td>\n" +
    //     "                </tr>\n" +
    //     "                </tbody>"
}

function addGame(game) {
    gameList.push(game)
    updateTable(games_table, gameList)
    // $("#games").append("                <tr>\n" +
    //     "                    <td>" + game.gameId + "</td>\n" +
    //     "                    <td>" + game.player1 + "</td>\n" +
    //     "                    <td>" + game.player2 + "</td>\n" +
    //     "                    <td>" + game.nextTurn + "</td>\n" +
    //     "                    <td>" + game.sum + "</td>\n" +
    //     "                    <td>" + game.finished + "</td>\n" +
    //     "                    <td>" + game.winner + "</td>\n" +
    //     "                </tr>\n")

}

function showMove(moveResult) {
    $("#move_version").val(moveResult.nextMoveVersion)
    $("#current_game").append("<tr><td>" + moveResult + "</td></tr>"); // TODO
}

function sendDecrease() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'moveVersion': $("#move_version").val(),
        'action': "-1"
    }));
}

function sendDontChange() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'moveVersion': $("#move_version").val(),
        'action': "0"
    }));
}

function sendIncrease() {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'moveVersion': $("#move_version").val(),
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
        'gameId': $("#game_id").val()
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
            gameList = games
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
