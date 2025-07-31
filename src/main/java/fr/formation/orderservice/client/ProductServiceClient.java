package fr.formation.orderservice.client;


import fr.formation.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient est la clé !
// name = "product-service" : C'est le nom de l'application cible,
// tel qu'il est enregistré dans Eureka. C'est ici que la magie opère.
// Feign va demander à Eureka : "Donne-moi l'adresse de 'product-service'".
@FeignClient(name = "product-service")
public interface ProductServiceClient {

    // La signature de cette méthode doit correspondre à l'endpoint
    // du ProductController dans product-service.
    // GET /api/products/{id}
    @GetMapping("/api/products/{id}")
    ProductDTO findProductById(@PathVariable("id") Long id);
}