package category.tree.bot.service;

import category.tree.bot.entity.Category;
import category.tree.bot.exceptions.CategoryAlreadyExists;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.repository.CategoryRepository;
import category.tree.bot.service.implementations.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testViewTree_EmptyTree() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        String result = categoryService.viewTree();

        assertEquals("Дерево категорий пусто.", result);
    }

    @Test
    void testViewTree_NonEmptyTree() {
        Category parentCategory = new Category();
        parentCategory.setName("Parent");
        parentCategory.setChildren(new ArrayList<>());

        Category childCategory = new Category();
        childCategory.setName("Child");
        childCategory.setParent(parentCategory);
        parentCategory.getChildren().add(childCategory);

        when(categoryRepository.findAll()).thenReturn(List.of(parentCategory));

        String result = categoryService.viewTree();

        String expected = "--Parent\n  --Child\n";
        assertEquals(expected, result);
    }

    @Test
    void testAddElement_NewParentCategory() {
        when(categoryRepository.existsByName("NewParent")).thenReturn(false);

        Category newCategory = new Category();
        newCategory.setName("NewParent");

        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        Category result = categoryService.addElement("NewParent", null);

        assertNotNull(result);
        assertEquals("NewParent", result.getName());
    }

    @Test
    void testAddElement_DuplicateCategory() {
        when(categoryRepository.existsByName("Duplicate")).thenReturn(true);

        assertThrows(CategoryAlreadyExists.class, () -> categoryService.addElement("Duplicate", null));
    }

    @Test
    void testAddElement_NewChildCategory() {
        Category parentCategory = new Category();
        parentCategory.setName("Parent");
        parentCategory.setChildren(new ArrayList<>());

        when(categoryRepository.findByName("Parent")).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.existsByName("Child")).thenReturn(false);

        Category childCategory = new Category();
        childCategory.setName("Child");
        childCategory.setParent(parentCategory);

        when(categoryRepository.save(any(Category.class))).thenReturn(childCategory);

        Category result = categoryService.addElement("Parent", "Child");

        assertNotNull(result);
        assertEquals("Parent", result.getName());
        assertEquals(1, result.getChildren().size());
        assertEquals("Child", result.getChildren().get(0).getName());
    }

    @Test
    void testRemoveElement_CategoryNotFound() {
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(CategoryIsNotFound.class, () -> categoryService.removeElement("NonExistent"));
    }

    @Test
    void testRemoveElement_Success() {
        Category category = new Category();
        category.setName("ToRemove");

        when(categoryRepository.findByName("ToRemove")).thenReturn(Optional.of(category));

        String result = categoryService.removeElement("ToRemove");

        verify(categoryRepository, times(1)).delete(category);
        assertEquals("Категория и её подкатегории удалены: ToRemove", result);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category();
        category1.setName("Category1");

        Category category2 = new Category();
        category2.setName("Category2");

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category1", result.get(0).getName());
        assertEquals("Category2", result.get(1).getName());
    }
}
