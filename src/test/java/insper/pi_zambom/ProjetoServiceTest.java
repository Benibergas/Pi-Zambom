package insper.pi_zambom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetoServiceTest {

    @InjectMocks
    private ProjetoService projetoService;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCadastrarProjeto_GerenteExistente() {
        Projeto projeto = new Projeto();
        projeto.setGerenteCpf("12345678900");
        when(restTemplate.getForEntity(any(String.class), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(new Pessoa(), HttpStatus.OK));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto result = projetoService.cadastrarProjeto(projeto);

        assertNotNull(result);
        assertEquals(projeto.getGerenteCpf(), result.getGerenteCpf());
    }

    @Test
    public void testCadastrarProjeto_GerenteInexistente() {
        Projeto projeto = new Projeto();
        projeto.setGerenteCpf("12345678900");
        when(restTemplate.getForEntity(any(String.class), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetoService.cadastrarProjeto(projeto);
        });

        assertEquals("Gerente não existe!", exception.getMessage());
    }

    @Test
    public void testListarProjetos_ComStatus() {
        List<Projeto> projetos = new ArrayList<>();
        Projeto projeto = new Projeto();
        projeto.setStatus(ProjetoStatus.EXECUCAO);
        projetos.add(projeto);

        when(projetoRepository.findByStatus(ProjetoStatus.EXECUCAO)).thenReturn(projetos);

        List<Projeto> result = projetoService.listarProjetos(ProjetoStatus.EXECUCAO);

        assertEquals(1, result.size());
        assertEquals(ProjetoStatus.EXECUCAO, result.get(0).getStatus());
    }

    @Test
    public void testAdicionarPessoa_ProjetoExistente() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.PLANEJAMENTO);
        projeto.setPessoas(new ArrayList<>());

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));
        when(restTemplate.getForEntity(any(String.class), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(new Pessoa(), HttpStatus.OK));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto result = projetoService.adicionarPessoa("1", "12345678900");

        assertNotNull(result);
        assertEquals(1, result.getPessoas().size());
    }

    @Test
    public void testAdicionarPessoa_ProjetoFinalizado() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.FINALIZADO);

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetoService.adicionarPessoa("1", "12345678900");
        });

        assertEquals("Não é possível adicionar pessoas em projetos finalizados.", exception.getMessage());
    }
}

