package insper.pi_zambom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import insper.pi_zambom.Projeto;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    public Projeto cadastrarProjeto(Projeto projeto) {
        String gerenteCpf = projeto.getGerenteCpf();
        // Verificar se o gerente existe na API externa
        if (!verificarPessoa(gerenteCpf)) {
            throw new RuntimeException("Gerente não existe!");
        }
        return projetoRepository.save(projeto);
    }

    public List<Projeto> listarProjetos(ProjetoStatus status) {
        if (status != null) {
            return projetoRepository.findByStatus(status);
        }
        return projetoRepository.findAll();
    }

    public Projeto adicionarPessoa(Long projetoId, String pessoaCpf) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado!"));

        if (projeto.getStatus() == ProjetoStatus.FINALIZADO) {
            throw new RuntimeException("Não é possível adicionar pessoas em projetos finalizados.");
        }

        if (!verificarPessoa(pessoaCpf)) {
            throw new RuntimeException("Pessoa não existe!");
        }

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(pessoaCpf);
        projeto.getPessoas().add(pessoa);

        return projetoRepository.save(projeto);
    }

    private boolean verificarPessoa(String cpf) {
        // Chamada à API externa para verificar a existência de uma pessoa
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://184.72.80.215:8080/usuario/" + cpf;
        try {
            ResponseEntity<Pessoa> response = restTemplate.getForEntity(url, Pessoa.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}

