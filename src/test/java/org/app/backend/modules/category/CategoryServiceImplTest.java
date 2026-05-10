package org.app.backend.modules.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.category.dto.CategoryCreateDTO;
import org.app.backend.modules.category.dto.CategoryDTO;
import org.app.backend.modules.category.enums.CategoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private FileService fileService;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private CategoryServiceImpl categoryService;

  private Category mockCategory;
  private CustomUserDetails mockUserDetails;
  private UUID categoryId;

  @BeforeEach
  void setUp() {
    categoryId = UUID.randomUUID();
    mockCategory = new Category();
    mockCategory.setId(categoryId);
    mockCategory.setTitle("Science Fiction");
    mockCategory.setStatus(CategoryStatus.ACTIVE);

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(UUID.randomUUID());
    mockUserDetails.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    CategoryDTO mockDto = new CategoryDTO();
    mockDto.setId(categoryId);
    mockDto.setTitle("Science Fiction");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
    when(modelMapper.map(mockCategory, CategoryDTO.class)).thenReturn(mockDto);

    CategoryDTO result = categoryService.findById(categoryId);

    assertNotNull(result);
    assertEquals("Science Fiction", result.getTitle());
  }

  @Test
  @DisplayName("Create Category - Success")
  void testCreateCategory_Success() {
    CategoryCreateDTO dto = new CategoryCreateDTO();
    dto.setTitle("New Category");

    Category mappedCategory = new Category();
    mappedCategory.setId(UUID.randomUUID());
    mappedCategory.setTitle("New Category");

    when(modelMapper.map(dto, Category.class)).thenReturn(mappedCategory);

    categoryService.create(dto, mockUserDetails);

    verify(categoryRepository, times(1)).save(mappedCategory);
    verify(auditLogService, times(1)).log(
        eq(mockUserDetails.getId()),
        eq(mockUserDetails.getUsername()),
        eq(AuditLogAction.CREATE),
        eq(AuditLogEntity.CATEGORY),
        anyString(),
        eq(AuditLogStatus.SUCCESS),
        anyString()
    );
  }

  @Test
  @DisplayName("Delete Category - Success")
  void testDeleteCategory_Success() {
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

    categoryService.delete(categoryId, mockUserDetails);

    assertEquals(CategoryStatus.DELETED, mockCategory.getStatus());
    verify(categoryRepository, times(1)).save(mockCategory);
  }
}
