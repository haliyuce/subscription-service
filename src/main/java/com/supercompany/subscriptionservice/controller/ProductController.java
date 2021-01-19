package com.supercompany.subscriptionservice.controller;

import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Gets all products as list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all products.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))})
    })
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
}
