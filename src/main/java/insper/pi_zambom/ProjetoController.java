package insper.pi_zambom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @PostMapping
    public ResponseEntity<Projeto> cadastrarProjeto(@RequestBody Projeto projeto) {
        Projeto novoProjeto = projetoService.cadastrarProjeto(projeto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProjeto);
    }

    @GetMapping
    public ResponseEntity<List<Projeto>> listarProjetos(@RequestParam(required = false) ProjetoStatus status) {
        List<Projeto> projetos = projetoService.listarProjetos(status);
        return ResponseEntity.ok(projetos);
    }

    @PostMapping("/{id}/pessoas")
    public ResponseEntity<Projeto> adicionarPessoa(@PathVariable String id, @RequestParam String cpf) {
        Projeto projeto = projetoService.adicionarPessoa(id, cpf);
        return ResponseEntity.ok(projeto);
    }
}

