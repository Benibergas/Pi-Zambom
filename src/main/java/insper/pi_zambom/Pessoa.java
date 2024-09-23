package insper.pi_zambom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "pessoas") // Nome da coleção no MongoDB
@Getter
@Setter
public class Pessoa {

    @Id
    private String cpf; // CPF como ID

    @Field("nome")
    private String nome;

    // Não é necessário incluir os métodos getters e setters aqui
    // Eles serão gerados automaticamente pelo Lombok
}
