package fr.formation.orderservice.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders") // "order" est un mot-clé SQL, mieux vaut utiliser "orders"
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate orderDate;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
