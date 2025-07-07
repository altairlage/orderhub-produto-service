
package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.dto.ProdutoDTO;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import br.com.orderhub.produto_service.adapter.mapper.ProdutoApiDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoApiControllerTest {

    private ProdutoController produtoController;
    private ProdutoApiController produtoApiController;

    @BeforeEach
    void setUp() {
        produtoController = mock(ProdutoController.class);
        produtoApiController = new ProdutoApiController(produtoController);
    }

    @Test
    void testBuscarProdutoPorId_Sucesso() {
        ProdutoDTO produtoDTO = new ProdutoDTO(1L, "Produto Teste", "Descricao", 10.0, 5);
        when(produtoController.buscarProdutoPorId(1L)).thenReturn(produtoDTO);

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Produto Teste", response.getBody().getNome());
    }

    @Test
    void testBuscarProdutoPorId_NaoEncontrado() {
        when(produtoController.buscarProdutoPorId(99L)).thenThrow(new ProdutoNaoEncontradoException("Produto não encontrado"));

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(99L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
