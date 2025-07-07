
package br.com.orderhub.produto_service.adapter.mapper;

import br.com.orderhub.core.dto.CriarProdutoDTO;
import br.com.orderhub.core.dto.ProdutoDTO;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoApiDtoMapperTest {

    @Test
    void testProdutoDtoToResponseDto() {
        ProdutoDTO dto = new ProdutoDTO(1L, "Nome", "Desc", 10.0, 5);
        ProdutoApiResponseDto response = ProdutoApiDtoMapper.produtoDtoToResponseDto(dto);
        assertEquals("Nome", response.getNome());
    }

    @Test
    void testRequestDtoToCriarProdutoDto() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Produto", "Descricao", 22.0);
        CriarProdutoDTO dto = ProdutoApiDtoMapper.requestDtoToCriarProdutoDto(request);
        assertEquals("Produto", dto.nome());
    }
}
