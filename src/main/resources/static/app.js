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
            processGameMessage(game)
        });
        stompClient.subscribe('/user/queue/games', function (currentGame) {
            processMove(JSON.parse(currentGame.body));
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

async function doMoveIfAuto(sum) {
    if ($("#auto_mode").is(':checked')) {
        await sleep(2000);
        var action = 1 - Math.abs(2 - sum) % 3
        sendAction(action)
    }
}

function processGameMessage(game) {
    if (game.player1 == currentUser || game.player2 == currentUser) {
        redrawCurrentGame(game);
        if (game.nextTurn == currentUser) {
            $("#status").text('It\'s our turn!');
            doMoveIfAuto(game.sum)
        } else {
            $("#status").text('Waiting for the enemy...')
        }
    } else if (currentGame == null) {
        $("#game_id").val(game.id)
    }
    if (game.player2 == null) { // game started
        addGame(game);
        if (game.player1 == currentUser) {
            $("#status").text('Waiting for others to join')
        }
    } else { // game joined
        for (var i = 0; i < gameList.length; i++) {
            if (gameList[i].id == game.id) {
                gameList[i] = game
            }
        }
        updateTable(games_table, gameList);
    }
}

function switchStatus(moveResult) {
    if (moveResult.finished) {
        if (currentUser == moveResult.winner) {
            $("#status").text('YOU WIN!');
        } else {
            $("#status").text('YOU LOST!');
        }
    } else {
        if (currentUser == moveResult.nextTurn) {
            $("#status").text('It\'s our turn!');
        } else {
            $("#status").text('Waiting for the enemy...');
        }
    }
}

function redrawCurrentGame(game) {
    currentGame = game
    updateTable(current_game, [currentGame]);
    $("#game_id").val(currentGame.id)
    $("#move_version").val(0)
}

function addGame(game) {
    gameList.push(game)
    updateTable(games_table, gameList)
}

function processMove(moveResult) {
    $("#move_version").val(moveResult.nextMoveVersion)
    currentGame.sum = moveResult.nextSum
    currentGame.finished = moveResult.finished
    if (moveResult.finished) {
        currentGame.winner = currentGame.nextTurn;
    }
    currentGame.nextTurn = moveResult.nextTurn
    redrawCurrentGame(currentGame)
    switchStatus(moveResult)
    if (!moveResult.finished && currentUser == moveResult.nextTurn) {
        doMoveIfAuto(moveResult.nextSum)
    }
}

function sendAction(action) {
    stompClient.send("/app/move", {}, JSON.stringify({
        'gameId': $("#game_id").val(),
        'moveVersion': $("#move_version").val(),
        'action': action
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
        sendAction(-1);
    });
    $("#send_dont_change").click(function () {
        sendAction(0);
    });
    $("#send_increase").click(function () {
        sendAction(1);
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
            gameList = games
            updateTable(games_table, games)
        }
    });
    connect()
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

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}