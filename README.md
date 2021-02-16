# Simple turn-based multiuser game

Pretty simple application, based on stomp-websocket with an ugly frontend.

## Rules:

Every user can either create a game or join an existing one.
The initiator manually sets a sum, creates a game and waits until somebody joins.
The second user chooses the game from the list and joins it. After that, it's their turn. The first user waits.

To do a move the user chooses from [-1, 0, 1]. Current SUM + chosen value must be divided by 3.

The user who reaches 1 is the winner.

## Notes:

To "select" a game, fill its ID into "Selected Game ID" input (it happens automatically, when initiator creates a new game, if you hadn't joined/created a game before).

Move number - the number of sent move-action of the current game (also is being filled automatically).

Currently there are only two users in the system: admin and user (both have a password '1')

## Steps to run:

    git clone https://github.com/baradist/game-of-three.git
    
    cd game-of-three

- With Docker

  Prerequisites: installed docker.

        docker build -t game-of-three .
    
        docker run -p 8080:8080 game-of-three

- With gradle
  Prerequisites: installed java (tested on open-jdk 11).

        ./gradlew build && java -jar build/libs/game-of-three-0.0.1-SNAPSHOT.jar

## Steps to reproduce:

- open http://localhost:8080 in a browser
- enter user 'admin' and password '1'
- open the same page in another session
- enter user 'user' and password '1'
- enter initial sum into "Sum of a new game", press "Create New Game"-button in any session
- press "Join Existing Game" in another session
- switch between sessions and use buttons "-1", "0", "1" to do moves. Alternatively, check "Auto mode" and enjoy


## TO DO:

- Cover WS-components by integration-tests.
- Connect Postgres instead of H2.
- Configure UserDetailsService that stores users.
- Use some message broker  instead of local spring-events in order to make the App scalable.