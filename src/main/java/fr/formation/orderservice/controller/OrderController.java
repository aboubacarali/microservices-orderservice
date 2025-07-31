package fr.formation.orderservice.controller;

import feign.FeignException;
import fr.formation.orderservice.client.ProductServiceClient;
import fr.formation.orderservice.dto.ProductDTO;
import fr.formation.orderservice.entity.Order;
import fr.formation.orderservice.entity.OrderItem;
import fr.formation.orderservice.exception.ProductNotFoundException;
import fr.formation.orderservice.repository.OrderRepository;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient; // Injection du client Feign

    public OrderController(OrderRepository orderRepository,
                           ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    // Endpoint pour créer une commande simple pour un produit et une quantité
    // POST http://localhost:8082/api/orders
    // Body: { "productId": 1, "quantity": 2 }
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        // 1. Appel au product-service via Feign pour récupérer les infos du produit
        ProductDTO product;
        try {
            product = productServiceClient.findProductById(request.productId);
        } catch (FeignException.NotFound e) {
            // Gérer le cas où le produit n'est pas trouvé (Feign lèvera une exception)
            Map<String, String> error = new HashMap<>();
            error.put("status", "404");
            error.put("message", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // 2. Vérifier si le stock est suffisant
        if (product.getStock() < request.quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error-Message", "Insufficient stock")
                    .build();
        }

        // 3. Créer et sauvegarder la commande
        Order order = new Order();
        order.setOrderDate(LocalDate.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getPrice()); // On enregistre le prix actuel

        order.setOrderItems(List.of(orderItem));

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // DTO interne pour la requête de création de commande
    @Data
    static class OrderRequest {
        private Long productId;
        private int quantity;
    }
}
