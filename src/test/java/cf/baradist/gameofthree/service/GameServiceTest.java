package cf.baradist.gameofthree.service;

import cf.baradist.gameofthree.dto.GameDto;
import cf.baradist.gameofthree.dto.MoveResultDto;
import cf.baradist.gameofthree.event.AbstractResultEvent;
import cf.baradist.gameofthree.exception.GameNotFoundException;
import cf.baradist.gameofthree.exception.GameStartedException;
import cf.baradist.gameofthree.exception.IncorrectInitialSumException;
import cf.baradist.gameofthree.exception.UserAlreadyParticipatedException;
import cf.baradist.gameofthree.exception.WrongSumException;
import cf.baradist.gameofthree.exception.WrongTurnException;
import cf.baradist.gameofthree.exception.WrongTurnNumberException;
import cf.baradist.gameofthree.exception.WrongUserException;
import cf.baradist.gameofthree.model.Game;
import cf.baradist.gameofthree.model.MoveAction;
import cf.baradist.gameofthree.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    public static final String PLAYER = "player";
    public static final String GAME_ID = "gameId";
    public static final String JOHN = "john";

    @Mock
    private GameRepository repository;
    @Mock
    private ApplicationEventPublisher publisher;
    @Captor
    private ArgumentCaptor<Game> gameCaptor;
    @Captor
    private ArgumentCaptor<AbstractResultEvent> eventCaptor;

    private GameService service;

    @BeforeEach
    void setUp() {
        service = new GameService(repository, publisher);
    }

    @Test
    void startGameShouldStartGameAndReturnGameDto() {
        GameDto gameDto = service.startGame(PLAYER, 42);
        assertThat(gameDto).isNotNull()
                .matches(g -> PLAYER.equals(g.getPlayer1()))
                .matches(g -> g.getSum() == 42)
                .matches(g -> g.getTurns() == 0);
        verify(repository).save(any(Game.class));
        verify(publisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getResultDto()).isEqualTo(gameDto);
    }

    @Test
    void startGameWhenSumIsLessThen1ShouldThrow() {
        assertThrows(IncorrectInitialSumException.class,
                () -> service.startGame(PLAYER, 0));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void joinGameShouldSaveGame() {
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(getGame()));
        GameDto gameDto = service.joinGame(GAME_ID, PLAYER);
        assertThat(gameDto).isNotNull()
                .matches(g -> g.getPlayer2().equals(PLAYER))
                .matches(g -> g.getNextTurn().equals(PLAYER));

        verify(repository).save(gameCaptor.capture());
        Game savedGame = gameCaptor.getValue();
        assertThat(savedGame).isNotNull()
                .matches(g -> g.getPlayer2().equals(PLAYER))
                .matches(g -> g.getNextTurn().equals(PLAYER));

        verify(publisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getResultDto()).isEqualTo(gameDto);
    }

    @Test
    void joinGameWhenGameNotFoundShouldThrow() {
        assertThrows(GameNotFoundException.class,
                () -> service.joinGame(GAME_ID, PLAYER));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void joinGameWhenPlayer2NotNullShouldThrow() {
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(getGame(JOHN, PLAYER)));
        assertThrows(GameStartedException.class,
                () -> service.joinGame(GAME_ID, PLAYER));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void joinGameWhenPlayer1IsEqualToGivenPlayerShouldThrow() {
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(getGame(PLAYER, null)));
        assertThrows(UserAlreadyParticipatedException.class,
                () -> service.joinGame(GAME_ID, PLAYER));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void moveShouldUpdateGame() {
        Game game = getGame(PLAYER, JOHN);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));

        MoveResultDto moveResult = service.move(GAME_ID, 0, JOHN, MoveAction.DONT_CHANGE);
        assertThat(moveResult).isNotNull()
                .matches(m -> m.getNextSum() == 14)
                .matches(m -> m.getNextTurn().equals(PLAYER))
                .matches(m -> m.getWinner() == null)
                .matches(m -> m.getNextTurnNumber() == 1);

        verify(repository).save(gameCaptor.capture());
        Game savedGame = gameCaptor.getValue();
        assertThat(savedGame).isNotNull()
                .matches(g -> g.getNextTurn().equals(PLAYER))
                .matches(g -> g.getSum() == 14)
                .matches(g -> g.getTurns() == 1)
                .matches(g -> g.getWinner() == null);

        verify(publisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getResultDto()).isEqualTo(moveResult);
    }

    @Test
    void moveWhenFinishShouldUpdateGameAndSetWinner() {
        Game game = getGame(PLAYER, JOHN);
        game.setSum(2);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));

        MoveResultDto moveResult = service.move(GAME_ID, 0, JOHN, MoveAction.INCREMENT);
        assertThat(moveResult).isNotNull()
                .matches(m -> m.getNextSum() == 1)
                .matches(m -> m.getNextTurn().equals(PLAYER))
                .matches(m -> m.getNextTurnNumber() == 1)
                .matches(m -> m.getWinner().equals(JOHN));

        verify(repository).save(gameCaptor.capture());
        Game savedGame = gameCaptor.getValue();
        assertThat(savedGame).isNotNull()
                .matches(g -> g.getNextTurn().equals(PLAYER))
                .matches(g -> g.getSum() == 1)
                .matches(g -> g.getTurns() == 1)
                .matches(g -> g.getWinner().equals(JOHN));

        verify(publisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getResultDto()).isEqualTo(moveResult);
    }

    @Test
    void moveWhenGameNotFoundShouldThrow() {
        assertThrows(GameNotFoundException.class,
                () -> service.move(GAME_ID, 0, JOHN, MoveAction.INCREMENT));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void moveWhenThirdPlayerShouldThrow() {
        Game game = getGame(PLAYER, JOHN);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));
        assertThrows(WrongUserException.class,
                () -> service.move(GAME_ID, 0, "Mary", MoveAction.INCREMENT));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void moveWhenWrongPlayerShouldThrow() {
        Game game = getGame(PLAYER, JOHN);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));
        assertThrows(WrongTurnException.class,
                () -> service.move(GAME_ID, 0, PLAYER, MoveAction.INCREMENT));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void moveWhenWrongTurnNumberShouldThrow() {
        Game game = getGame(PLAYER, JOHN);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));
        assertThrows(WrongTurnNumberException.class,
                () -> service.move(GAME_ID, 42, JOHN, MoveAction.INCREMENT));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void moveWhenWrongSumShouldThrow() {
        Game game = getGame(PLAYER, JOHN);
        when(repository.findById(eq(GAME_ID)))
                .thenReturn(Optional.of(game));
        assertThrows(WrongSumException.class,
                () -> service.move(GAME_ID, 0, JOHN, MoveAction.INCREMENT));
        verify(repository, never()).save(any(Game.class));
        verify(publisher, never()).publishEvent(any());
    }

    private Game getGame() {
        return getGame(JOHN, null);
    }

    private Game getGame(String player1, String player2) {
        return Game.builder()
                .id(GAME_ID)
                .player1(player1)
                .player2(player2)
                .nextTurn(player2)
                .sum(42)
                .turns(0)
                .build();
    }
}
