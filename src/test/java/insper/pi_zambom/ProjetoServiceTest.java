package insper.pi_zambom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProjetoService projetoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarProjetoSucesso() {
        Projeto projeto = new Projeto();
        projeto.setGerenteCpf("12345678900");
        projeto.setStatus(ProjetoStatus.PLANEJAMENTO);

        when(restTemplate.getForEntity(anyString(), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(new Pessoa(), HttpStatus.OK));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto resultado = projetoService.cadastrarProjeto(projeto);

        assertNotNull(resultado);
        assertEquals(ProjetoStatus.PLANEJAMENTO, resultado.getStatus());
        verify(projetoRepository).save(projeto);
    }

    @Test
    void testCadastrarProjetoGerenteNaoExiste() {
        Projeto projeto = new Projeto();
        projeto.setGerenteCpf("12345678900");
        projeto.setStatus(ProjetoStatus.PLANEJAMENTO);

        when(restTemplate.getForEntity(anyString(), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> projetoService.cadastrarProjeto(projeto));
    }

    @Test
    void testListarProjetosComStatus() {
        ProjetoStatus status = ProjetoStatus.EXECUCAO;
        List<Projeto> projetos = Arrays.asList(new Projeto(), new Projeto());

        when(projetoRepository.findByStatus(status)).thenReturn(projetos);

        List<Projeto> resultado = projetoService.listarProjetos(status);

        assertEquals(2, resultado.size());
        verify(projetoRepository).findByStatus(status);
    }

    @Test
    void testListarProjetosSemStatus() {
        List<Projeto> projetos = Arrays.asList(new Projeto(), new Projeto(), new Projeto());

        when(projetoRepository.findAll()).thenReturn(projetos);

        List<Projeto> resultado = projetoService.listarProjetos(null);

        assertEquals(3, resultado.size());
        verify(projetoRepository).findAll();
    }

    @Test
    void testAdicionarPessoaSucesso() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.EXECUCAO);

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));
        when(restTemplate.getForEntity(anyString(), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(new Pessoa(), HttpStatus.OK));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto resultado = projetoService.adicionarPessoa("1", "12345678900");

        assertNotNull(resultado);
        assertEquals(1, resultado.getPessoas().size());
        verify(projetoRepository).save(projeto);
    }

    @Test
    void testAdicionarPessoaProjetoNaoEncontrado() {
        when(projetoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projetoService.adicionarPessoa("1", "12345678900"));
    }

    @Test
    void testAdicionarPessoaProjetoFinalizado() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.FINALIZADO);

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));

        assertThrows(RuntimeException.class, () -> projetoService.adicionarPessoa("1", "12345678900"));
    }

    @Test
    void testAdicionarPessoaNaoExistente() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.EXECUCAO);

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));
        when(restTemplate.getForEntity(anyString(), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> projetoService.adicionarPessoa("1", "12345678900"));
    }

    @Test
    void testAdicionarPessoaEmProjetoPlanejamento() {
        Projeto projeto = new Projeto();
        projeto.setId("1");
        projeto.setStatus(ProjetoStatus.PLANEJAMENTO);

        when(projetoRepository.findById("1")).thenReturn(Optional.of(projeto));
        when(restTemplate.getForEntity(anyString(), eq(Pessoa.class)))
                .thenReturn(new ResponseEntity<>(new Pessoa(), HttpStatus.OK));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto resultado = projetoService.adicionarPessoa("1", "12345678900");

        assertNotNull(resultado);
        assertEquals(1, resultado.getPessoas().size());
        verify(projetoRepository).save(projeto);
    }
}