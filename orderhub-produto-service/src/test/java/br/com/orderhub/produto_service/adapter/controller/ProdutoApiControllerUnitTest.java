package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.dto.produtos.CriarProdutoDTO;
import br.com.orderhub.core.dto.produtos.ProdutoDTO;
import br.com.orderhub.core.exceptions.ProdutoJaExisteException;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy; // Importe este para testar exceções
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoApiControllerUnitTest {

    @Mock
    private ProdutoController produtoController;

    @InjectMocks
    private ProdutoApiController produtoApiController;

    private ProdutoDTO produtoDTO;
    private ProdutoApiRequestDto produtoApiRequestDto;
    private ProdutoApiResponseDto produtoApiResponseDto;

    @BeforeEach
    void setUp() {
        produtoDTO = new ProdutoDTO(1L, "Produto Teste", "Descricao Teste", 100.00);
        produtoApiRequestDto = new ProdutoApiRequestDto("Produto Teste", "Descricao Teste", 100.00);
        produtoApiResponseDto = new ProdutoApiResponseDto(1L, "Produto Teste", "Descricao Teste", 100.00);
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        when(produtoController.buscarProdutoPorId(anyLong())).thenReturn(produtoDTO);
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(produtoApiResponseDto);
        verify(produtoController).buscarProdutoPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar NOT_FOUND ao buscar produto por ID inexistente")
    void deveRetornarNotFoundAoBuscarProdutoPorIdInexistente() {
        // Aqui o catch no controlador já lida com ProdutoNaoEncontradoException, então testamos o retorno do ResponseEntity
        doThrow(new ProdutoNaoEncontradoException("Produto não encontrado")).when(produtoController).buscarProdutoPorId(anyLong());
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId(99L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(produtoController).buscarProdutoPorId(99L);
    }

    @Test
    @DisplayName("Deve buscar produto por nome com sucesso")
    void deveBuscarProdutoPorNomeComSucesso() {
        when(produtoController.buscarProdutoPorNome(anyString())).thenReturn(produtoDTO);
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId("Produto Teste");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(produtoApiResponseDto);
        verify(produtoController).buscarProdutoPorNome("Produto Teste");
    }

    @Test
    @DisplayName("Deve retornar NOT_FOUND ao buscar produto por nome inexistente")
    void deveRetornarNotFoundAoBuscarProdutoPorNomeInexistente() {
        // Aqui o catch no controlador já lida com ProdutoNaoEncontradoException, então testamos o retorno do ResponseEntity
        doThrow(new ProdutoNaoEncontradoException("Produto não encontrado")).when(produtoController).buscarProdutoPorNome(anyString());
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.buscarProdutoPorId("Nome Inexistente");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(produtoController).buscarProdutoPorNome("Nome Inexistente");
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(produtoController.criarProduto(any(CriarProdutoDTO.class))).thenReturn(produtoDTO);
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.criarProduto(produtoApiRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(produtoApiResponseDto);
        verify(produtoController).criarProduto(any(CriarProdutoDTO.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoJaExisteException ao tentar criar produto já existente")
    void deveLancarProdutoJaExisteExceptionAoCriarProdutoExistente() {
        // Dado
        doThrow(new ProdutoJaExisteException("O produto já existe!")).when(produtoController).criarProduto(any(CriarProdutoDTO.class));

        // Quando / Então
        // O controller não tem try-catch para ProdutoJaExisteException, então a exceção se propaga.
        // Testamos se a exceção correta é lançada.
        assertThatThrownBy(() -> produtoApiController.criarProduto(produtoApiRequestDto))
                .isInstanceOf(ProdutoJaExisteException.class)
                .hasMessage("O produto já existe!");
        verify(produtoController).criarProduto(any(CriarProdutoDTO.class));
    }


    @Test
    @DisplayName("Deve editar produto com sucesso")
    void deveEditarProdutoComSucesso() {
        when(produtoController.editarProduto(any(ProdutoDTO.class))).thenReturn(produtoDTO);
        ResponseEntity<ProdutoApiResponseDto> response = produtoApiController.editarProduto(1L, produtoApiRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(produtoApiResponseDto);
        verify(produtoController).editarProduto(any(ProdutoDTO.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException ao tentar editar produto inexistente")
    void deveLancarProdutoNaoEncontradoExceptionAoEditarProdutoInexistente() {
        // Dado
        doThrow(new ProdutoNaoEncontradoException("Produto não encontrado")).when(produtoController).editarProduto(any(ProdutoDTO.class));

        // Quando / Então
        // O controller não tem try-catch para ProdutoNaoEncontradoException no método editarProduto,
        // então a exceção se propaga. Testamos se a exceção correta é lançada.
        assertThatThrownBy(() -> produtoApiController.editarProduto(99L, produtoApiRequestDto))
                .isInstanceOf(ProdutoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
        verify(produtoController).editarProduto(any(ProdutoDTO.class));
    }


    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        doNothing().when(produtoController).deletarProduto(anyLong());
        ResponseEntity<Void> response = produtoApiController.deletarProduto(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(produtoController).deletarProduto(1L);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException ao tentar deletar produto inexistente")
    void deveLancarProdutoNaoEncontradoExceptionAoDeletarProdutoInexistente() {
        // Dado
        doThrow(new ProdutoNaoEncontradoException("Produto não encontrado")).when(produtoController).deletarProduto(anyLong());

        // Quando / Então
        // O controller não tem try-catch para ProdutoNaoEncontradoException no método deletarProduto,
        // então a exceção se propaga. Testamos se a exceção correta é lançada.
        assertThatThrownBy(() -> produtoApiController.deletarProduto(99L))
                .isInstanceOf(ProdutoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
        verify(produtoController).deletarProduto(99L);
    }
}