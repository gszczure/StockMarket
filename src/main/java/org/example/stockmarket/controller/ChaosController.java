package org.example.stockmarket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chaos")
public class ChaosController {

    @PostMapping
    public ResponseEntity<Void> killInstance() {

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
            System.exit(1);
        }).start();

        return ResponseEntity.ok().build();
    }
}
