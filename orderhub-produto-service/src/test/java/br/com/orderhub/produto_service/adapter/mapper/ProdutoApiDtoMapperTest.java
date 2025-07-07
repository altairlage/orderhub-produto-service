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
        ProdutoDTO dto = new ProdutoDTO(1L, "Nome", "Desc", 10.0);
        ProdutoApiResponseDto response = ProdutoApiDtoMapper.produtoDtoToResponseDto(dto);
        assertEquals("Nome", response.nome());
        assertEquals("Desc", dto.descricao());
        assertEquals(10.0, dto.preco());
    }

    @Test
    void testRequestDtoToCriarProdutoDto() {
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Produto", "Descricao", 22.0);
        CriarProdutoDTO dto = ProdutoApiDtoMapper.requestDtoToCriarProdutoDto(request);
        assertEquals("Produto", dto.nome());
        assertEquals("Descricao", dto.descricao());
        assertEquals(22.0, dto.preco());
    }

    @Test
    void testRequestDtoToProdutoDTO() {
        Long id = 42L;
        ProdutoApiRequestDto request = new ProdutoApiRequestDto("Caneta", "Esferográfica azul", 2.50);

        var dto = ProdutoApiDtoMapper.requestDtoToProdutoDTO(id, request);

        assertNotNull(dto);
        assertEquals(42L, dto.id());
        assertEquals("Caneta", dto.nome());
        assertEquals("Esferográfica azul", dto.descricao());
        assertEquals(2.50, dto.preco());
    }
}
