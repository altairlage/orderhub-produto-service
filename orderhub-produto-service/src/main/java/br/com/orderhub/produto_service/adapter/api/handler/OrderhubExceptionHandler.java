package br.com.orderhub.produto_service.adapter.api.handler;

import br.com.orderhub.core.exceptions.OrderhubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderhubExceptionHandler {

    @ExceptionHandler(OrderhubException.class)
    public ResponseEntity<String> handleOrderhubException(OrderhubException ex) {
        if (ex instanceof br.com.orderhub.core.exceptions.ProdutoNaoEncontradoException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        if (ex instanceof br.com.orderhub.core.exceptions.ProdutoJaExisteException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
    }
}
