package br.com.orderhub.produto_service.adapter.mapper;

import br.com.orderhub.core.domain.entities.Produto;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoEntityMapperTest {

    @Test
    void testEntityToDomain() {
        ProdutoEntity entity = new ProdutoEntity(1L, "Nome", "Descricao", 99.9);
        Produto produto = ProdutoEntityMapper.entityToDomain(entity);
        assertEquals("Nome", produto.getNome());
    }

    @Test
    void testDomainToEntity() {
        Produto produto = new Produto(2L, "Produto", "Info", 11.0);
        ProdutoEntity entity = ProdutoEntityMapper.domainToEntity(produto);
        assertEquals("Produto", entity.getNome());
    }
}
