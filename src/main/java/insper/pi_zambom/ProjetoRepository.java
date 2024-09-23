package insper.pi_zambom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjetoRepository extends MongoRepository<Projeto, String> {
    List<Projeto> findByStatus(ProjetoStatus status);
}
