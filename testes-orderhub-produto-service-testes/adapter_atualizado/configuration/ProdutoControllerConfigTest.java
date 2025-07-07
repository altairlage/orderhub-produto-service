
package br.com.orderhub.produto_service.adapter.configuration;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.interfaces.IProdutoGateway;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoControllerConfigTest {

    @Test
    void testProdutoControllerBeanCreation() {
        IProdutoGateway mockGateway = mock(IProdutoGateway.class);
        ProdutoControllerConfig config = new ProdutoControllerConfig();
        ProdutoController controller = config.produtoController(mockGateway);
        assertNotNull(controller);
    }
}
