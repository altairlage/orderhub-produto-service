package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.dto.CriarProdutoDTO;
import br.com.orderhub.core.dto.ProdutoDTO;
import br.com.orderhub.core.exceptions.ProdutoJaExisteException;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import br.com.orderhub.produto_service.adapter.mapper.ProdutoApiDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
public class ProdutoApiController {
    private final ProdutoController produtoController;

    public ProdutoApiController(ProdutoController produtoController) {
        this.produtoController = produtoController;
    }

    // *** posso usar os mesmos DTOs do core, ou preciso criar outros para request e response na camada de API?
    // Li um pouco e entendi que seria melhor ter DTOs separados para a camada da aplicação, pois caso os DTOs do core
    // mudem, a camada de aplicação nao quebra.

    //falta o get all

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoApiResponseDto> buscarProdutoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    ProdutoApiDtoMapper.produtoDtoToResponseDto(produtoController.buscarProdutoPorId(id))
            );
        } catch (ProdutoNaoEncontradoException ex){
            System.out.println(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<ProdutoApiResponseDto> buscarProdutoPorId(@PathVariable String nome) {
        try {
            return ResponseEntity.ok(
                    ProdutoApiDtoMapper.produtoDtoToResponseDto(produtoController.buscarProdutoPorNome(nome))
            );
        } catch (ProdutoNaoEncontradoException ex){
            System.out.println(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ProdutoApiResponseDto> criarProduto(@RequestBody ProdutoApiRequestDto requestDto) {
        CriarProdutoDTO criarProdutoDTO = ProdutoApiDtoMapper.requestDtoToCriarProdutoDto(requestDto);
        ProdutoDTO produtoDTO = produtoController.criarProduto(criarProdutoDTO);
        return ResponseEntity.ok(ProdutoApiDtoMapper.produtoDtoToResponseDto(produtoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoApiResponseDto> editarProduto(
            @PathVariable("id") Long id,
            @RequestBody ProdutoApiRequestDto request
    ) {
        ProdutoDTO produtoDTO = ProdutoApiDtoMapper.requestDtoToProdutoDTO(id, request);
        ProdutoDTO produtoEditadoDTO = produtoController.editarProduto(produtoDTO);
        return ResponseEntity.ok(ProdutoApiDtoMapper.produtoDtoToResponseDto(produtoEditadoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable("id") Long id) {
        produtoController.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}
