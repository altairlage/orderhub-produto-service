package br.com.orderhub.produto_service.adapter.configuration;

import br.com.orderhub.core.controller.ProdutoController;
import br.com.orderhub.core.interfaces.IProdutoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuração de injeção de dependencia para o API controller

@Configuration
public class ProdutoControllerConfig {
    @Bean
    public ProdutoController produtoController(IProdutoGateway produtoGateway) {
        return new ProdutoController(produtoGateway);
    }

    /*
    Opções que eu estudei:
        1- Criar um @Configuration ou @Bean que instancia os use cases de todas as entidades de dominio.
          	Pode ficar grande em caso haka muitos use cases
        2- Criar um @Component UseCaseProvider que prove os use cases como um uma facade para a camada de aplicação (caso queira fazer cache, logging, monitoramento, etc.).
            Introduz uma camada a mais entre controller e use cases...
        3- Agrupar os beans por entidade de dominio em classes @Configuration, organizando as configurações por domínio
            Requer mais arquivos/configurações para muitos domínios

        Gostei mais da opção 3. Porem não foi necessario pois eu criei uma classe controller para o core.
     */
}
