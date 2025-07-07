package br.com.orderhub.produto_service.adapter.mapper;

// este seria tambem um presenter??

import br.com.orderhub.core.domain.entities.Produto;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoEntity;

public class ProdutoEntityMapper {

    public ProdutoEntityMapper() {}


    public static Produto entityToDomain(ProdutoEntity produtoEntity) {
        return new Produto(
                produtoEntity.getId(),
                produtoEntity.getNome(),
                produtoEntity.getDescricao(),
                produtoEntity.getPreco()
        );
    }

    public static ProdutoEntity domainToEntity(Produto produto) {
        return new ProdutoEntity(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco()
        );
    }
}
