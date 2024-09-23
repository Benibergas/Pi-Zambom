package insper.pi_zambom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Projeto cadastrarProjeto(Projeto projeto) {
        String gerenteCpf = projeto.getGerenteCpf();
        // Verificar se o gerente existe na API externa
        if (!verificarPessoa(gerenteCpf)) {
            throw new RuntimeException("Gerente não existe!");
        }
        // Definir o status inicial como PLANEJAMENTO
        projeto.setStatus(ProjetoStatus.PLANEJAMENTO);
        return projetoRepository.save(projeto);
    }

    public List<Projeto> listarProjetos(ProjetoStatus status) {
        if (status != null) {
            return projetoRepository.findByStatus(status);
        }
        return projetoRepository.findAll();
    }

    public Projeto adicionarPessoa(String projetoId, String pessoaCpf) {
        // Verificar se o projeto existe
        Optional<Projeto> optionalProjeto = projetoRepository.findById(projetoId);
        if (optionalProjeto.isEmpty()) {
            throw new RuntimeException("Projeto não encontrado!");
        }

        Projeto projeto = optionalProjeto.get();

        // Verificar se o projeto está finalizado
        if (projeto.getStatus() == ProjetoStatus.FINALIZADO) {
            throw new RuntimeException("Não é possível adicionar pessoas em projetos finalizados.");
        }

        // Verificar se a pessoa existe
        if (!verificarPessoa(pessoaCpf)) {
            throw new RuntimeException("Pessoa não existe!");
        }

        // Adicionar a pessoa ao projeto
        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(pessoaCpf);
        projeto.getPessoas().add(pessoa);

        return projetoRepository.save(projeto);
    }

    private boolean verificarPessoa(String cpf) {
        String url = "http://184.72.80.215:8080/usuario/" + cpf;
        try {
            ResponseEntity<Pessoa> response = restTemplate.getForEntity(url, Pessoa.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}