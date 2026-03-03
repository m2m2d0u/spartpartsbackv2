package sn.symmetry.spareparts.service;

import org.springframework.data.domain.Pageable;
import sn.symmetry.spareparts.dto.request.CreateCategoryRequest;
import sn.symmetry.spareparts.dto.request.UpdateCategoryRequest;
import sn.symmetry.spareparts.dto.response.CategoryResponse;
import sn.symmetry.spareparts.dto.response.common.PagedResponse;

public interface CategoryService {

    PagedResponse<CategoryResponse> getAllCategories(Pageable pageable);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);
}
