package cf.baradist.gameofthree.repository;

import cf.baradist.gameofthree.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {

    List<Game> findAllByPlayer2IsNull();

    Optional<Game> findByPlayer1OrPlayer2(String player1, String player2);
}
