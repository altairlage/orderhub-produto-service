
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
}
