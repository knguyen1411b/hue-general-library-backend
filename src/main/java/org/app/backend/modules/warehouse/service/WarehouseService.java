package org.app.backend.modules.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.app.backend.modules.warehouse.entity.*;
import org.app.backend.modules.warehouse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final FloorRepository floorRepo;
    private final AisleRepository aisleRepo;
    private final ShelfRepository shelfRepo;
    private final PositionRepository positionRepo;

    
    public List<Floor> getWarehouseTree() {
        return floorRepo.findAll();
    }

   
    @Transactional
    public Shelf createShelf(String name, int maxRow, int maxCol, Long aisleId) {
        // Tìm Dãy (Aisle) xem có tồn tại không
        Aisle aisle = aisleRepo.findById(aisleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Dãy!"));

        
        Shelf shelf = new Shelf();
        shelf.setName(name);
        shelf.setMaxRow(maxRow);
        shelf.setMaxCol(maxCol);
        shelf.setAisle(aisle);
        Shelf savedShelf = shelfRepo.save(shelf);

        
        for (int r = 1; r <= maxRow; r++) {
            for (int c = 1; c <= maxCol; c++) {
                Position pos = new Position();
                pos.setRowIndex(r);
                pos.setColIndex(c);
                pos.setShelf(savedShelf);
                pos.setBookCount(0); 
                positionRepo.save(pos);
            }
        }
        return savedShelf;
    }

   
    @Transactional
    public void deleteShelf(Long id) {
        Shelf shelf = shelfRepo.findById(id).orElseThrow();
        
       
        boolean hasBooks = shelf.getPositions().stream()
                .anyMatch(p -> p.getBookCount() > 0);
        
        if (hasBooks) {
            throw new RuntimeException("Kệ đang có sách, không thể xóa!");
        }
        
        shelfRepo.delete(shelf);
    }
}
