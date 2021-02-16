package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.event.GameDto;
import cf.baradist.gameofthree.event.MoveResult;
import cf.baradist.gameofthree.exception.GameNotFoundException;
import cf.baradist.gameofthree.exception.GameStartedException;
import cf.baradist.gameofthree.exception.IncorrectInitialSumException;
import cf.baradist.gameofthree.exception.UserAlreadyParticipatedException;
import cf.baradist.gameofthree.exception.WrongMoveException;
import cf.baradist.gameofthree.exception.WrongTurnException;
import cf.baradist.gameofthree.exception.WrongTurnNumberException;
import cf.baradist.gameofthree.exception.WrongUserException;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {
    public static final int DELIMITER = 3;
    public static final int LAST_SUM = 1;

    private final GameRepository repository;

    public List<Game> getAvailableGameSessions() {
        return repository.findAll(); // TODO: temporary show all games
    }

    public Optional<Game> getById(String id) {
        return repository.findById(id);
    }

    public Optional<Game> getCurrentGameByPlayer(String player) {
        return repository.findByPlayer1OrPlayer2(player, player);
    }

    public GameDto startGame(String initiatorPlayer, int sum) {
        if (sum < 1) {
            throw new IncorrectInitialSumException(sum);
        }
        Game game = Game.builder()
                .id(UUID.randomUUID().toString())
                .player1(initiatorPlayer)
                .sum(sum)
                .turns(0)
                .build();
        repository.save(game);
        return mapGameToDto(game);
    }

    public GameDto joinGame(String gameId, String player) {
        Game game = repository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
        if (game.getPlayer2() != null) {
            throw new GameStartedException(game, player);
        }
        if (Objects.equals(player, game.getPlayer1())) {
            throw new UserAlreadyParticipatedException(game, player);
        }
        game.setPlayer2(player);
        game.setNextTurn(player);
        repository.save(game);
        return mapGameToDto(game);
    }

    private GameDto mapGameToDto(Game game) {
        return GameDto.builder()
                .id(game.getId())
                .player1(game.getPlayer1())
                .player2(game.getPlayer2())
                .nextTurn(game.getNextTurn())
                .sum(game.getSum())
                .winner(game.getWinner())
                .turns(game.getTurns())
                .build();
    }

    public MoveResult move(String gameId, int turnNumber, String player, MoveAction action) {
        Game game = repository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
        if (!player.equals(game.getPlayer1()) && !player.equals(game.getPlayer2())) {
            throw new WrongUserException(game, player);
        }
        if (!player.equals(game.getNextTurn())) {
            throw new WrongTurnException(game, player);
        }
        if (game.getTurns() != turnNumber) {
            throw new WrongTurnNumberException(game, player, turnNumber);
        }
        int sum = game.getSum() + action.getValue();
        if (isWrongSum(sum)) {
            throw new WrongMoveException(game, player, sum);
        }
        int nextSum = sum / DELIMITER;
        String nextTurnPlayer = getNextTurn(game, player);
        game.setNextTurn(nextTurnPlayer);
        int nextTurnNumber = turnNumber + 1;
        MoveResult moveResult = MoveResult.builder()
                .nextSum(nextSum)
                .nextTurn(nextTurnPlayer)
                .nextTurnNumber(nextTurnNumber)
                .build();
        game.setSum(nextSum);
        game.setTurns(nextTurnNumber);
        if (isWin(nextSum)) {
            game.setWinner(player);
            moveResult.setWinner(player);
        }
        repository.save(game);
        return moveResult;
    }

    private boolean isWrongSum(int sum) {
        return sum % DELIMITER != 0;
    }

    private boolean isWin(int nextSum) {
        return nextSum == LAST_SUM;
    }

    private String getNextTurn(Game game, String currentPlayer) {
        return game.getNextTurn().equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
    }
}
