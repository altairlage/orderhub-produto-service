package br.com.orderhub.produto_service.adapter.gateway;

import br.com.orderhub.core.domain.entities.Produto;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.core.interfaces.IProdutoGateway;
import br.com.orderhub.produto_service.adapter.mapper.ProdutoEntityMapper;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoEntity;
import br.com.orderhub.produto_service.adapter.persistence.ProdutoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProdutoRepositoryJpaGatewayImpl implements IProdutoGateway {

    private final ProdutoRepository produtoRepository;

    public ProdutoRepositoryJpaGatewayImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public Produto buscarPorId(Long id) {
        Optional<ProdutoEntity> produtoOp = this.produtoRepository.findById(id);
        if(produtoOp.isEmpty()) {
            return null;
        }

        ProdutoEntity produtoEntity = produtoOp.get();
        return ProdutoEntityMapper.entityToDomain(produtoEntity);
    }

    @Override
    public Produto buscarPorNome(String nome) {
        Optional<ProdutoEntity> produtoOp = this.produtoRepository.findByNome(nome);
        if(produtoOp.isEmpty()) {
            return null;
        }
        ProdutoEntity produtoEntity = produtoOp.get();
        return ProdutoEntityMapper.entityToDomain(produtoEntity);
    }

    @Override
    public Produto criar(Produto produto) {
        ProdutoEntity produtoEntity = ProdutoEntityMapper.domainToEntity(produto);
        return ProdutoEntityMapper.entityToDomain(this.produtoRepository.save(produtoEntity));
    }

    @Override
    public Produto atualizar(Produto produto) throws ProdutoNaoEncontradoException {
        ProdutoEntity produtoEntity = ProdutoEntityMapper.domainToEntity(produto);
        produtoRepository.save(produtoEntity);
        return ProdutoEntityMapper.entityToDomain(produtoEntity);
    }

    @Override
    public void deletar(Long id) throws ProdutoNaoEncontradoException {
        produtoRepository.deleteById(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll()
                .stream().map(ProdutoEntityMapper::entityToDomain)
                .toList();
    }
}
