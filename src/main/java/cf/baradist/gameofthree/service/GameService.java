package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.exception.GameNotFoundException;
import cf.baradist.gameofthree.exception.WrongMoveException;
import cf.baradist.gameofthree.exception.WrongTurnException;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository repository;

    public List<Game> getAvailableGameSessions() {
        return repository.findAll(); // TODO: get available
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
        if (game.getPlayer2() == null) { // TODO
            game.setPlayer2(player);
            game.setNextTurn(player);
            repository.save(game);
        }
        // TODO: notify players
    }

    @Transactional
    public void move(String gameId, int number, String player, MoveAction action) {
        Game game = repository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
        if (!player.equals(game.getNextTurn())) {
            throw new WrongTurnException(game, player);
        }
        // TODO: check a number of the move?
        int nextSum = game.getSum() + action.getValue();
        if ((nextSum % 3 != 0)) {
            throw new WrongMoveException(game, player, nextSum);
        }
        game.setSum(nextSum);
        game.setNextTurn(getNexTurn(game));
        if (nextSum == 0) {
            game.setFinished(true);
            game.setWinner(player);
        }
        repository.save(game);
    }

    private String getNexTurn(Game game) {
        return game.getNextTurn().equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
    }
}
