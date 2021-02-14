package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.event.MoveResult;
import cf.baradist.gameofthree.exception.GameNotFoundException;
import cf.baradist.gameofthree.exception.GameStartedException;
import cf.baradist.gameofthree.exception.WrongMoveException;
import cf.baradist.gameofthree.exception.WrongTurnException;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.Move;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.repository.GameRepository;
import cf.baradist.gameofthree.repository.MoveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    public static final int DELIMITER = 3;
    public static final int LAST_SUM = 1;
    private final GameRepository repository;
    private final MoveRepository moveRepository;

    public List<Game> getAvailableGameSessions() {
        return repository.findAllByPlayer2IsNull();
    }

    public Optional<Game> getById(String id) {
        return repository.findById(id);
    }

    public String startGame(String initiatorPlayer, int sum) {
        Game game = new Game(
                UUID.randomUUID().toString(), initiatorPlayer, null, null, sum, false, null);
        repository.save(game);
        return game.getId();
    }

    public void joinGame(String gameId, String player) {
        Game game = repository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
        if (game.getPlayer2() != null) {
            throw new GameStartedException(game, player);
        }
        game.setPlayer2(player);
        game.setNextTurn(player);
        repository.save(game);
        // TODO: notify players
    }

    public MoveResult move(String gameId, int number, String player, MoveAction action) {
        Game game = repository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
        if (!player.equals(game.getNextTurn())) {
            throw new WrongTurnException(game, player);
        }
        // TODO: check a number of the move?
        int sum = game.getSum() + action.getValue();
        if (isWrongSum(sum)) {
            throw new WrongMoveException(game, player, sum);
        }
        int nextSum = sum / DELIMITER;
        game.setNextTurn(getNexTurn(game));
        MoveResult moveResult = MoveResult.builder()
                .sum(nextSum)
                .build();
        game.setSum(nextSum);
        if (isWin(nextSum)) {
            game.setFinished(true);
            game.setWinner(player);
            moveResult.setFinished(true);
        }
        repository.save(game);
        moveRepository.save(Move.builder()
                .game(game)
                .action(action)
                .initiator(player)
                .number(number)
                .build());
        return moveResult;
    }

    private boolean isWrongSum(int sum) {
        return sum % DELIMITER != 0;
    }

    private boolean isWin(int nextSum) {
        return nextSum == LAST_SUM;
    }

    private String getNexTurn(Game game) {
        return game.getNextTurn().equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
    }
}
