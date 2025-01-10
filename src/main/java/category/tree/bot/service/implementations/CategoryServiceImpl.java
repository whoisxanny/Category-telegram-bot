package category.tree.bot.service.implementations;

import category.tree.bot.entity.Category;
import category.tree.bot.exceptions.CategoryAlreadyExcists;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.repository.CategoryRepository;
import category.tree.bot.service.services.CategoryService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

/**
 * Реализация сервиса для управления деревом категорий.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор для внедрения репозитория категорий.
     *
     * @param categoryRepository репозиторий категорий
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает текстовое представление дерева категорий.
     *
     * @return строка, представляющая дерево категорий
     */

    @Transactional
    @Override
    public String viewTree() {
        List<Category> parentCategories = categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .toList();

        if (parentCategories.isEmpty()) {
            return "Дерево категорий пусто.";
        }

        StringBuilder builder = new StringBuilder();
        for (Category parentCategory : parentCategories) {
            Hibernate.initialize(parentCategory.getChildren());
            buildTreeView(builder, parentCategory, 0);
        }
        return builder.toString();
    }

    /**
     * Рекурсивно строит текстовое представление дерева категорий.
     *
     * @param builder строковый билдер для записи дерева
     * @param category текущая категория
     * @param depth текущая глубина вложенности
     */
    private void buildTreeView(StringBuilder builder, Category category, int depth) {
        builder.append("  ".repeat(depth)).append("--").append(category.getName()).append("\n");

        if (!category.getChildren().isEmpty()) {
            for (Category child : category.getChildren()) {
                buildTreeView(builder, child, depth + 1);
            }
        }
    }

    /**
     * Добавляет новую категорию или подкатегорию.
     *
     * @param parent имя родительской категории или новой категории
     * @param child имя подкатегории (null, если создается новая корневая категория)
     * @return созданная категория или родительская категория, к которой добавлена подкатегория
     */

    @Transactional
    @Override
    public Category addElement(String parent, String child) {
        if (child == null) {
            if (categoryRepository.existsByName(parent)) {
                throw new CategoryAlreadyExcists();
            }
            Category newParent = new Category();
            newParent.setName(parent);
            return categoryRepository.save(newParent);
        }

        Category parentCategory = categoryRepository.findByName(parent)
                .orElseGet(() -> {
                    Category newParent = new Category();
                    newParent.setName(parent);
                    return categoryRepository.save(newParent);
                });

        if (categoryRepository.existsByName(child)) {
            throw new CategoryAlreadyExcists();
        }

        Category childCategory = new Category();
        childCategory.setName(child);
        childCategory.setParent(parentCategory);
        parentCategory.getChildren().add(childCategory);

        categoryRepository.save(childCategory);

        return parentCategory;
    }

    /**
     * Удаляет категорию и все её подкатегории.
     *
     * @param element имя категории для удаления
     * @return сообщение об успешном удалении
     */

    @Transactional
    @Override
    public String removeElement(String element) {
        Category category = categoryRepository.findByName(element)
                .orElseThrow(() -> new CategoryIsNotFound());

        categoryRepository.delete(category);

        return "Категория и её подкатегории удалены: " + element;
    }

    /**
     * Возвращает список доступных команд.
     *
     * @return список строк с описанием команд
     */
    @Transactional
    @Override
    public List<String> getHelp() {
        return List.of();
    }

}
