package br.com.orderhub.produto_service.adapter.persistence;

import br.com.orderhub.produto_service.adapter.mapper.ProdutoEntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
    Optional<ProdutoEntity> findByNome(String nome);
}
