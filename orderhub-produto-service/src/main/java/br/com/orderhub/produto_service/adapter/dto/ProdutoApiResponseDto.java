package br.com.orderhub.produto_service.adapter.dto;

public record ProdutoApiResponseDto(Long id, String nome, String descricao, Double preco) {
}
