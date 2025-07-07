package br.com.orderhub.produto_service.adapter.controller;

import br.com.orderhub.core.domain.entities.Produto; // Ainda precisa para CriarProdutoDTO se for mapeado de Produto
import br.com.orderhub.produto_service.OrderhubProdutoServiceApplication;
import br.com.orderhub.produto_service.adapter.dto.ProdutoApiRequestDto;
import br.com.orderhub.produto_service.adapter.gateway.ProdutoRepositoryJpaGatewayImpl; // Manter se for para testes mais avançados de repo
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
import org.springframework.test.context.jdbc.Sql;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderhubProdutoServiceApplication.class)
@ActiveProfiles("test")
// Usar @Sql para setup e teardown para garantir dados consistentes
@Sql(scripts = {"classpath:/db_clean.sql", "classpath:/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

@DisplayName("ProdutoApiController Integration Tests with RestAssured")
public class ProdutoApiControllerIT {

    @LocalServerPort
    private int port;

    // @Autowired
    // private ProdutoRepositoryJpaGatewayImpl produtoRepositoryJpaGatewayImpl; // Não mais necessário para pré-condição se tudo vem do SQL

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Should create a product successfully and return 200 OK")
    void shouldCreateProductSuccessfully() throws JsonProcessingException {
        ProdutoApiRequestDto requestDto = new ProdutoApiRequestDto(
                "Produto Novo IT Criado", // Nome único para este teste
                "Descrição do Produto Novo IT Criado",
                199.99
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestDto))
                .when()
                .post("/produtos/create")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Produto Novo IT Criado"))
                .body("descricao", equalTo("Descrição do Produto Novo IT Criado"))
                .body("preco", equalTo(199.99f));
    }

    @Test
    @DisplayName("Should return 409 CONFLICT when creating an existing product")
    void shouldReturnConflictWhenCreatingExistingProduct() throws JsonProcessingException {
        // "Produto Existente IT" já está no banco via db_load.sql



        ProdutoApiRequestDto requestDto = new ProdutoApiRequestDto(
                "Produto Existente IT", // Nome que já existe
                "Nova Descrição",
                20.0
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(requestDto))
                .when()
                .post("/produtos/create")
                .then()
                .statusCode(409)
                .body(equalTo("O produto Produto Existente ITJá existe!")); // <-- Verifique o espaço aqui, ajuste para o que sua exceção REALMENTE retorna
    }


    @Test
    @DisplayName("Should retrieve a product by ID successfully and return 200 OK")
    void shouldRetrieveProductByIdSuccessfully() {
        // Produto com ID 101 já está no banco via db_load.sql


        given()
                .when()
                .get("/produtos/{id}", 101L)
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
                .body(equalTo("")); // <-- CORREÇÃO: Espera corpo vazio para 404 de GET
    }

    @Test
    @DisplayName("Should retrieve a product by Name successfully and return 200 OK")
    void shouldRetrieveProductByNameSuccessfully() {

        // Produto "Produto Busca Nome IT" já está no banco via db_load.sql

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
                .body(equalTo("")); // <-- CORREÇÃO: Espera corpo vazio para 404 de GET
    }

    @Test
    @DisplayName("Should update a product successfully and return 200 OK")
    void shouldUpdateProductSuccessfully() throws JsonProcessingException {
        // Produto com ID 103 já está no banco via db_load.sql


        ProdutoApiRequestDto updateRequest = new ProdutoApiRequestDto(
                "Produto Atualizado IT",
                "Nova Descrição IT",
                120.00
        );

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(updateRequest))
                .when()
                .put("/produtos/{id}", 103L)
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
                .body(equalTo("Produto com ID 9999não encontrado")); // <-- CORREÇÃO: Ajuste a string para EXATAMENTE o que sua exceção retorna (com ou sem espaço)
    }

    @Test
    @DisplayName("Should delete a product successfully and return 204 NO CONTENT")
    void shouldDeleteProductSuccessfully() {
        // Produto com ID 104 já está no banco via db_load.sql


        given()
                .when()
                .delete("/produtos/{id}", 104L)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deleting non-existent product")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() {
        given()
                .when()
                .delete("/produtos/{id}", 9999L) // ID que não existe
                .then()
                .statusCode(404)
                .body(equalTo("Produto com ID 9999não encontrado")); // <-- CORREÇÃO: Ajuste a string para EXATAMENTE o que sua exceção retorna (com ou sem espaço)
    }
}