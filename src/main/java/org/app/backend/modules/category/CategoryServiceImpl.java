package org.app.backend.modules.category;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.category.dto.*;
import org.app.backend.modules.category.enums.CategoryStatus;
import org.app.backend.modules.category.filter.CategorySpecification;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

  CategoryRepository categoryRepository;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  public Page<CategoryDTO> findAll(CategoryFilterDTO filter, Pageable pageable) {
    return categoryRepository
        .findAll(CategorySpecification.filter(filter), pageable)
        .map(category -> modelMapper.map(category, CategoryDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryDTO findById(UUID id) {
    return mapActiveCategory(categoryRepository.findById(id))
        .map(category -> modelMapper.map(category, CategoryDTO.class))
        .orElseThrow(
            () -> new AppException(HttpStatus.NOT_FOUND, CategoryMessage.NOT_FOUND.getMessage()));
  }

  private java.util.Optional<Category> mapActiveCategory(
      java.util.Optional<Category> categoryOptional) {
    return categoryOptional.filter(category -> category.getStatus() == CategoryStatus.ACTIVE);
  }

  @Override
  @Transactional
  // CHIÊU BẢO MẬT: Chỉ Manager (Thủ thư) mới lọt qua được cửa này
  @PreAuthorize("hasRole('MANAGER')")
  public void create(CategoryCreateDTO dto, CustomUserDetails actor) {
    if (categoryRepository.existsByTitle(dto.getTitle())) {
      throw new AppException(HttpStatus.CONFLICT, CategoryMessage.TITLE_TAKEN.getMessage());
    }

    Category category = modelMapper.map(dto, Category.class);
    categoryRepository.save(category);

    // Lưu vết vào AuditLog
    auditLogService.log(
        actor.getId(),
        actor.getUsername(),
        AuditLogAction.CREATE,
        AuditLogEntity.CATEGORY, // *LƯU
        // Ý:
        // Bạn
        // nhớ
        // thêm
        // enum
        // CATEGORY
        // vào
        // file
        // AuditLogEntity
        // nhé
        category.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Thủ thư đã tạo danh mục mới: " + category.getTitle());
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('MANAGER')")
  public void update(UUID id, CategoryUpdateDTO dto, CustomUserDetails actor) {
    Category category =
        mapActiveCategory(categoryRepository.findById(id))
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, CategoryMessage.NOT_FOUND.getMessage()));

    // Kiểm tra xem tên sửa có bị trùng với thể loại khác không
    if (categoryRepository.existsByTitleAndIdNot(dto.getTitle(), id)) {
      throw new AppException(HttpStatus.CONFLICT, CategoryMessage.TITLE_TAKEN.getMessage());
    }

    category.setTitle(dto.getTitle());
    categoryRepository.save(category);

    auditLogService.log(
        actor.getId(),
        actor.getUsername(),
        AuditLogAction.UPDATE,
        AuditLogEntity.CATEGORY,
        category.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Thủ thư đã cập nhật danh mục: " + category.getTitle());
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('MANAGER')")
  public void delete(UUID id, CustomUserDetails actor) {
    Category category =
        mapActiveCategory(categoryRepository.findById(id))
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, CategoryMessage.NOT_FOUND.getMessage()));

    category.setStatus(CategoryStatus.DELETED);
    categoryRepository.save(category);

    auditLogService.log(
        actor.getId(),
        actor.getUsername(),
        AuditLogAction.DELETE,
        AuditLogEntity.CATEGORY,
        category.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Thủ thư đã xóa danh mục: " + category.getTitle());
  }
}
