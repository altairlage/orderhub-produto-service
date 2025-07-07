package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.dto.CriarProdutoDTO;
import br.com.orderhub.core.dto.ProdutoDTO;
import br.com.orderhub.core.exceptions.ProdutoJaExisteException;
import br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException;
import br.com.orderhub.produto_service.adapter.api.handler.OrderhubExceptionHandler;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // Importe este
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ProdutoApiController Unit and Component Tests")
public class ProdutoApiControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProdutoController produtoController;

    @InjectMocks
    private ProdutoApiController produtoApiController;

    private MockMvc mockMvc;
    private AutoCloseable mocks;

    private ProdutoDTO produtoDTO;
    private ProdutoApiRequestDto produtoApiRequestDto;
    private ProdutoApiResponseDto produtoApiResponseDto;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        produtoApiController = new ProdutoApiController(produtoController);
        mockMvc = MockMvcBuilders.standaloneSetup(produtoApiController)
                .setControllerAdvice(new OrderhubExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();

        produtoDTO = new ProdutoDTO(1L, "Produto Teste", "Descricao Teste", 100.00);
        produtoApiRequestDto = new ProdutoApiRequestDto("Produto Teste", "Descricao Teste", 100.00);
        produtoApiResponseDto = new ProdutoApiResponseDto(1L, "Produto Teste", "Descricao Teste", 100.00);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Nested
    @DisplayName("GET Produto Cases")
    class GetProdutoCases {
        @Test
        @DisplayName("Should return 200 OK and product by ID")
        void testBuscarProdutoPorId_success() throws Exception {
            when(produtoController.buscarProdutoPorId(1L)).thenReturn(produtoDTO);

            mockMvc.perform(get("/produtos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nome").value("Produto Teste"));

            verify(produtoController, times(1)).buscarProdutoPorId(1L);
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when product by ID not found")
        void testBuscarProdutoPorId_notFound() throws Exception {
            // Seu controlador retorna NOT_FOUND().build(), que não tem corpo.
            doThrow(new ProdutoNaoEncontradoException("Produto com ID 999 não encontrado")).when(produtoController).buscarProdutoPorId(999L);

            mockMvc.perform(get("/produtos/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("")); // <-- CORREÇÃO: Espera um corpo vazio
        }

        @Test
        @DisplayName("Should return 200 OK and product by name")
        void testBuscarProdutoPorNome_success() throws Exception {
            when(produtoController.buscarProdutoPorNome("Produto Teste")).thenReturn(produtoDTO);

            mockMvc.perform(get("/produtos/nome/{nome}", "Produto Teste")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nome").value("Produto Teste"));

            verify(produtoController, times(1)).buscarProdutoPorNome("Produto Teste");
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when product by name not found")
        void testBuscarProdutoPorNome_notFound() throws Exception {
            // Seu controlador retorna NOT_FOUND().build(), que não tem corpo.
            doThrow(new ProdutoNaoEncontradoException("Produto com nome Inexistente não encontrado")).when(produtoController).buscarProdutoPorNome("Inexistente");

            mockMvc.perform(get("/produtos/nome/{nome}", "Inexistente")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("")); // <-- CORREÇÃO: Espera um corpo vazio
        }
    }

    @Nested
    @DisplayName("POST CriarProduto Cases")
    class CriarProdutoCases {
        @Test
        @DisplayName("Should return 200 OK and created product")
        void testCriarProduto_success() throws Exception {
            when(produtoController.criarProduto(any(CriarProdutoDTO.class))).thenReturn(produtoDTO);

            mockMvc.perform(post("/produtos/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoApiRequestDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Produto Teste"));

            verify(produtoController, times(1)).criarProduto(any(CriarProdutoDTO.class));
        }

        @Test
        @DisplayName("Should return 409 CONFLICT when product already exists")
        void testCriarProduto_alreadyExists() throws Exception {
            doThrow(new ProdutoJaExisteException("O produto 'Produto Existente' já existe!"))
                    .when(produtoController).criarProduto(any(CriarProdutoDTO.class));

            ProdutoApiRequestDto existingProductRequest = new ProdutoApiRequestDto("Produto Existente", "Desc", 10.0);

            mockMvc.perform(post("/produtos/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(existingProductRequest)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(content().string("O produto 'Produto Existente' já existe!")); // <-- CORREÇÃO: Espera a string pura
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST for invalid input (e.g., empty name)")
        void testCriarProduto_invalidInput() throws Exception {
            ProdutoApiRequestDto invalidRequest = new ProdutoApiRequestDto("", "Desc", 10.0);

            // Mockando que o produtoController lança uma IllegalArgumentException
            // Seu OrderhubExceptionHandler provavelmente tem um @ExceptionHandler(IllegalArgumentException.class)
            // ou OrderhubException que trata isso como BAD_REQUEST sem o prefixo "Erro interno: "
            doThrow(new IllegalArgumentException("O nome não pode ser nulo ou vazio."))
                    .when(produtoController).criarProduto(any(CriarProdutoDTO.class));

            mockMvc.perform(post("/produtos/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest()) // <-- CORREÇÃO: Espera 400 BAD REQUEST
                    .andExpect(content().string("O nome não pode ser nulo ou vazio.")); // <-- CORREÇÃO: Espera a string pura, sem "Erro interno:"
        }
    }

    @Nested
    @DisplayName("PUT EditarProduto Cases")
    class EditarProdutoCases {
        @Test
        @DisplayName("Should return 200 OK and updated product")
        void testEditarProduto_success() throws Exception {
            when(produtoController.editarProduto(any(ProdutoDTO.class))).thenReturn(produtoDTO);

            mockMvc.perform(put("/produtos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoApiRequestDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Produto Teste"));

            verify(produtoController, times(1)).editarProduto(any(ProdutoDTO.class));
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when product to edit not found")
        void testEditarProduto_notFound() throws Exception {
            // O controller não tem um try-catch para ProdutoNaoEncontradoException no editarProduto,
            // então a exceção será capturada pelo OrderhubExceptionHandler
            doThrow(new ProdutoNaoEncontradoException("Produto com ID 999 não encontrado"))
                    .when(produtoController).editarProduto(any(ProdutoDTO.class));

            mockMvc.perform(put("/produtos/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(produtoApiRequestDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Produto com ID 999 não encontrado")); // <-- CORREÇÃO: Espera a string pura
        }
    }

    @Nested
    @DisplayName("DELETE DeletarProduto Cases")
    class DeletarProdutoCases {
        @Test
        @DisplayName("Should return 204 NO CONTENT when product deleted successfully")
        void testDeletarProduto_success() throws Exception {
            doNothing().when(produtoController).deletarProduto(1L);

            mockMvc.perform(delete("/produtos/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(produtoController, times(1)).deletarProduto(1L);
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when product to delete not found")
        void testDeletarProduto_notFound() throws Exception {
            // O controller não tem um try-catch para ProdutoNaoEncontradoException no deletarProduto,
            // então a exceção será capturada pelo OrderhubExceptionHandler
            doThrow(new ProdutoNaoEncontradoException("Produto com ID 999 não encontrado"))
                    .when(produtoController).deletarProduto(999L);

            mockMvc.perform(delete("/produtos/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Produto com ID 999 não encontrado")); // <-- CORREÇÃO: Espera a string pura
        }
    }

    private String asJsonString(final Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}