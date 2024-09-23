package insper.pi_zambom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Document(collection = "projetos") // Nome da coleção no MongoDB
@Getter
@Setter
public class Projeto {

    @Id
    private String id; // Usar String como ID no MongoDB

    @Field("nome")
    private String nome;

    @Field("descricao")
    private String descricao;

    @Field("status")
    private ProjetoStatus status;

    @Field("gerenteCpf")
    private String gerenteCpf;

    @DBRef // Referência para outra coleção
    private List<Pessoa> pessoas = new ArrayList<>();



}
