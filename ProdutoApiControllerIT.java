package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.domain.entities.Produto;
import br.com.orderhub.produto_service.OrderhubProdutoServiceApplication; // Sua classe principal da aplicação Spring Boot
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.gateway.ProdutoRepositoryJpaGatewayImpl; // Seu gateway de persistência
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional; // Para rollback de transações após cada teste

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderhubProdutoServiceApplication.class)
@ActiveProfiles("test") // Ative um perfil de teste, se tiver um para H2 por exemplo
@Transactional // Cada teste rodará em uma transação e fará rollback
@DisplayName("ProdutoApiController Integration Tests with RestAssured")
public class ProdutoApiControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ProdutoRepositoryJpaGatewayImpl produtoRepositoryJpaGatewayImpl; // Para pré-condicionar dados

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RestAssured.port = port; // Configura a porta do servidor para os testes RestAssured
    }

    @Test
    @DisplayName("Should create a product successfully and return 200 OK")
    void shouldCreateProductSuccessfully() throws JsonProcessingException {
        ProdutoApiRequestDto requestDto = new ProdutoApiRequestDto(
                "Produto Novo IT",
                "Descrição do Produto Novo IT",
                99.99
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestDto))
                .when()
                .post("/produtos/create")
                .then()
                .statusCode(200) // Seu controller retorna OK
                .body("nome", equalTo("Produto Novo IT"))
                .body("descricao", equalTo("Descrição do Produto Novo IT"))
                .body("preco", equalTo(99.99f)); // Use float para double em jsonPath
    }

    @Test
    @DisplayName("Should return 409 CONFLICT when creating an existing product")
    void shouldReturnConflictWhenCreatingExistingProduct() throws JsonProcessingException {
        // Pré-condição: Crie um produto primeiro para que ele já exista
        Produto existingProduto = new Produto("Produto Existente IT", "Desc IT", 10.0);
        produtoRepositoryJpaGatewayImpl.criar(existingProduto);

        ProdutoApiRequestDto requestDto = new ProdutoApiRequestDto(
                "Produto Existente IT", // Mesmo nome
                "Nova Descrição",
                20.0
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestDto))
                .when()
                .post("/produtos/create")
                .then()
                .statusCode(409) // O OrderhubExceptionHandler deve retornar CONFLICT
                .body(equalTo("O produto Produto Existente ITJá existe!")); // Mensagem exata do seu ExceptionHandler
    }


    @Test
    @DisplayName("Should retrieve a product by ID successfully and return 200 OK")
    void shouldRetrieveProductByIdSuccessfully() {
        // Pré-condição: Crie um produto no banco para buscar
        Produto createdProduto = produtoRepositoryJpaGatewayImpl.criar(new Produto("Produto Busca ID IT", "Desc Busca IT", 50.0));

        given()
                .when()
                .get("/produtos/{id}", createdProduto.getId())
                .then()
                .statusCode(200)
                .body("nome", equalTo("Produto Busca ID IT"))
                .body("preco", equalTo(50.0f));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when retrieving non-existent product by ID")
    void shouldReturnNotFoundWhenRetrievingNonExistentProductById() {
        given()
                .when()
                .get("/produtos/{id}", 9999L) // ID que não existe
                .then()
                .statusCode(404)
                .body(equalTo("Produto com ID 9999 não encontrado"));
    }

    @Test
    @DisplayName("Should retrieve a product by Name successfully and return 200 OK")
    void shouldRetrieveProductByNameSuccessfully() {
        // Pré-condição: Crie um produto no banco para buscar
        Produto createdProduto = produtoRepositoryJpaGatewayImpl.criar(new Produto("Produto Busca Nome IT", "Desc Busca Nome IT", 75.0));

        given()
                .when()
                .get("/produtos/nome/{nome}", "Produto Busca Nome IT")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Produto Busca Nome IT"))
                .body("preco", equalTo(75.0f));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when retrieving non-existent product by Name")
    void shouldReturnNotFoundWhenRetrievingNonExistentProductByName() {
        given()
                .when()
                .get("/produtos/nome/{nome}", "Nome Inexistente IT")
                .then()
                .statusCode(404)
                .body(equalTo("Produto com nome Nome Inexistente ITnão encontrado")); // Cuidado com o espaço na mensagem de erro do seu caso de uso
    }

    @Test
    @DisplayName("Should update a product successfully and return 200 OK")
    void shouldUpdateProductSuccessfully() throws JsonProcessingException {
        // Pré-condição: Crie um produto para ser atualizado
        Produto createdProduto = produtoRepositoryJpaGatewayImpl.criar(new Produto("Produto Original IT", "Desc Original IT", 100.0));

        ProdutoApiRequestDto updateRequest = new ProdutoApiRequestDto(
                "Produto Atualizado IT",
                "Nova Descrição IT",
                120.00
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(updateRequest))
                .when()
                .put("/produtos/{id}", createdProduto.getId())
                .then()
                .statusCode(200)
                .body("nome", equalTo("Produto Atualizado IT"))
                .body("preco", equalTo(120.00f));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when updating non-existent product")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws JsonProcessingException {
        ProdutoApiRequestDto updateRequest = new ProdutoApiRequestDto(
                "Produto Inexistente Update",
                "Desc Inexistente Update",
                120.00
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(updateRequest))
                .when()
                .put("/produtos/{id}", 9999L) // ID que não existe
                .then()
                .statusCode(404)
                .body(equalTo("Produto com ID 9999 não encontrado"));
    }

    @Test
    @DisplayName("Should delete a product successfully and return 204 NO CONTENT")
    void shouldDeleteProductSuccessfully() {
        // Pré-condição: Crie um produto para ser deletado
        Produto createdProduto = produtoRepositoryJpaGatewayImpl.criar(new Produto("Produto para Deletar IT", "Desc para Deletar IT", 10.0));

        given()
                .when()
                .delete("/produtos/{id}", createdProduto.getId())
                .then()
                .statusCode(204); // Status esperado para deleção bem-sucedida
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deleting non-existent product")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() {
        given()
                .when()
                .delete("/produtos/{id}", 9999L) // ID que não existe
                .then()
                .statusCode(404)
                .body(equalTo("Produto com ID 9999 não encontrado"));
    }
}