package cf.baradist.gameofthree.repository;

import cf.baradist.gameofthree.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends JpaRepository<Move, String> {
}
