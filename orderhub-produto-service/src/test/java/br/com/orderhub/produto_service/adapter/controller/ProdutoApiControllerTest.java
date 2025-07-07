package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.dto.CriarProdutoDTO;
import br.com.orderhub.core.dto.ProdutoDTO;
import br.com.orderhub.core.exceptions.ProdutoJaExisteException;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
        ProdutoDTO dto = new ProdutoDTO(1L, "Produto", "Desc", 10.0);
        when(produtoController.buscarProdutoPorId(1L)).thenReturn(dto);

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Produto", response.getBody().nome());
    }

    @Test
    void testBuscarProdutoPorId_NotFound() {
        when(produtoController.buscarProdutoPorId(any())).thenThrow(new ProdutoNaoEncontradoException(""));

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testBuscarProdutoPorNome() {
        ProdutoDTO dto = new ProdutoDTO(2L, "Nome", "Info", 15.0);
        when(produtoController.buscarProdutoPorNome("Nome")).thenReturn(dto);

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId("Nome");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Nome", response.getBody().nome());
    }

    @Test
    void testBuscarProdutoPorNome_NotFound() {
        when(produtoController.buscarProdutoPorNome("X")).thenThrow(new ProdutoNaoEncontradoException(""));

        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId("X");
        assertEquals(404, response.getStatusCodeValue());
    }

//    @Test
//    void testBuscarTodos() {
//        when(produtoController.buscarTodos()).thenReturn(List.of(
//                new ProdutoDTO(1L, "Prod1", "Desc", 20.0, 3),
//                new ProdutoDTO(2L, "Prod2", "Desc", 30.0, 5)
//        ));
//
//        var response = produtoApiController.buscarTodos();
//        assertEquals(2, response.getBody().size());
//    }

    @Test
    void testCriarProduto() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Novo", "Item", 50.0);
        ProdutoDTO dto = new ProdutoDTO(3L, "Novo", "Item", 50.0);
        when(produtoController.criarProduto(any(CriarProdutoDTO.class))).thenReturn(dto);

        var response = produtoApiController.criarProduto(request);
        assertEquals("Novo", response.getBody().nome());
    }

    @Test
    void testCriarProduto_JaExiste() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Existente", "Item", 60.0);
        when(produtoController.criarProduto(any(CriarProdutoDTO.class)))
                .thenThrow(new ProdutoJaExisteException("Já existe"));

        var response = produtoApiController.criarProduto(request);
        assertEquals(409, response.getStatusCodeValue());
        
    }

    @Test
    void testAtualizarProduto() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Atual", "Item", 77.0);
        ProdutoDTO dto = new ProdutoDTO(5L, "Atual", "Item", 77.0);
        when(produtoController.editarProduto(any())).thenReturn(dto);

        var response = produtoApiController.editarProduto(5L, request);
        assertEquals("Atual", response.getBody().nome());
    }

    @Test
    void testAtualizarProduto_NotFound() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Nada", "Item", 11.0);
        when(produtoController.editarProduto(any())).thenThrow(new ProdutoNaoEncontradoException(""));

        var response = produtoApiController.editarProduto(6L, request);
        assertEquals(404, response.getStatusCodeValue());
    }
}

