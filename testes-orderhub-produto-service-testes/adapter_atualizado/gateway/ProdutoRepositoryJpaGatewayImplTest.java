
package br.com.orderhub.produto_service.adapter.gateway;

import br.com.orderhub.core.domain.entities.Produto;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoEntity;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoRepositoryJpaGatewayImplTest {

    private ProdutoRepository produtoRepository;
    private ProdutoRepositoryJpaGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        gateway = new ProdutoRepositoryJpaGatewayImpl(produtoRepository);
    }

    @Test
    void testBuscarPorId_Sucesso() {
        ProdutoEntity entity = new ProdutoEntity(1L, "Produto", "Descricao", 100.0);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(entity));

        Produto produto = gateway.buscarPorId(1L);

        assertEquals("Produto", produto.getNome());
    }

    @Test
    void testBuscarPorId_ProdutoNaoEncontrado() {
        when(produtoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> gateway.buscarPorId(2L));
    }

    @Test
    void testBuscarPorNome_Sucesso() {
        ProdutoEntity entity = new ProdutoEntity(3L, "Cafe", "Bebida", 5.0);
        when(produtoRepository.findByNome("Cafe")).thenReturn(Optional.of(entity));

        Produto produto = gateway.buscarPorNome("Cafe");

        assertEquals("Cafe", produto.getNome());
    }

    @Test
    void testBuscarPorNome_ProdutoNaoEncontrado() {
        when(produtoRepository.findByNome("Inexistente")).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> gateway.buscarPorNome("Inexistente"));
    }

    @Test
    void testSalvarProduto() {
        ProdutoEntity entity = new ProdutoEntity(1L, "Produto", "Descricao", 22.0);
        when(produtoRepository.save(any())).thenReturn(entity);

        Produto domain = new Produto(null, "Produto", "Descricao", 22.0);
        Produto result = gateway.salvar(domain);

        assertEquals("Produto", result.getNome());
    }

    @Test
    void testBuscarTodos() {
        when(produtoRepository.findAll()).thenReturn(java.util.List.of(
                new ProdutoEntity(1L, "P1", "D1", 1.0),
                new ProdutoEntity(2L, "P2", "D2", 2.0)
        ));

        var result = gateway.buscarTodos();
        assertEquals(2, result.size());
    }
}
