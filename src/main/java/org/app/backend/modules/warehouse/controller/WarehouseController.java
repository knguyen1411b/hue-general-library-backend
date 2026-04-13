package org.app.backend.modules.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.app.backend.modules.warehouse.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;


    @GetMapping("/tree")
    public ResponseEntity<List<Floor>> getTree() {
        return ResponseEntity.ok(warehouseService.getWarehouseTree());
    }

    
    @PostMapping("/shelves")
    public ResponseEntity<Shelf> addShelf(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        int maxRow = (int) payload.get("maxRow");
        int maxCol = (int) payload.get("maxCol");
        Long aisleId = Long.valueOf(payload.get("aisleId").toString());
        
        return ResponseEntity.ok(warehouseService.createShelf(name, maxRow, maxCol, aisleId));
    }


    @DeleteMapping("/shelves/{id}")
    public ResponseEntity<?> deleteShelf(@PathVariable Long id) {
        try {
            warehouseService.deleteShelf(id);
            return ResponseEntity.ok(Map.of("message", "Xóa kệ thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
