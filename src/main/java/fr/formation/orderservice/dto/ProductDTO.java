package fr.formation.orderservice.dto;


import lombok.Data;

// Cet objet simple (POJO) représente les données d'un produit
// que order-service a besoin de connaître.
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private int stock;
}
