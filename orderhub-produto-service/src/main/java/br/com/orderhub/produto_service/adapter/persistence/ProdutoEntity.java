package br.com.orderhub.produto_service.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produtos")
public class ProdutoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private Double preco;


//    public ProdutoEntity(Long id, String nome, String descricao, Double preco) {
//        this.id = id;
//        this.nome = nome;
//        this.descricao = descricao;
//        this.preco = preco;
//    }
//
//    public ProdutoEntity() {}
}
