package br.com.orderhub.produto_service.adapter.mapper;

import br.com.orderhub.core.dto.produtos.CriarProdutoDTO;
import br.com.orderhub.core.dto.produtos.ProdutoDTO;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;

public class ProdutoApiDtoMapper {
    public static ProdutoApiResponseDto produtoDtoToResponseDto(ProdutoDTO produtoDTO ) {
        return new ProdutoApiResponseDto(
                produtoDTO.id(),
                produtoDTO.nome(),
                produtoDTO.descricao(),
                produtoDTO.preco()
        );
    }

    public static CriarProdutoDTO requestDtoToCriarProdutoDto(ProdutoApiRequestDto requestDto) {
        return new CriarProdutoDTO(
                requestDto.nome(),
                requestDto.descricao(),
                requestDto.preco()
        );
    }

    public static ProdutoDTO requestDtoToProdutoDTO(Long id, ProdutoApiRequestDto requestDto) {
        return new ProdutoDTO(
                id,
                requestDto.nome(),
                requestDto.descricao(),
                requestDto.preco()
        );
    }
}
