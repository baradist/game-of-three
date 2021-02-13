package cf.baradist.gameofthree.repository;

import cf.baradist.gameofthree.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {

    List<Game> findAllByPlayer2IsNull();
}
