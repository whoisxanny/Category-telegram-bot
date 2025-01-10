package category.tree.bot.controller;


import category.tree.bot.entity.Category;
import category.tree.bot.service.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Tag(name = "Контроллер для управления категориями",
        description = "Создание, удаление и просмотр категорий.")
@RequestMapping("/category")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Просмотр категорий в структурированном виде",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Категории отображены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            })
    @GetMapping("/viewTree")
    public ResponseEntity<String> viewTree() {
        return ResponseEntity.ok(categoryService.viewTree());
    }

    @Operation(summary = "Создает новую категорию как корневую, если родитель не указан, или добавляет её к существующей категории.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Элемент добавлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            })
    @PostMapping("/addElement")
    public ResponseEntity<Category> addElement(@RequestParam String parent, @RequestParam String child) {
        return ResponseEntity.ok(categoryService.addElement(parent, child));
    }

    @Operation(summary = "Удаление элемента из категорий",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Элемент удален",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    )
            })
    @DeleteMapping("/removeElement")
    public ResponseEntity<String> removeElement(@RequestParam String element) {
        return ResponseEntity.ok(categoryService.removeElement(element));
    }

    @Operation(summary = "Просмотр доступных команд",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список команд",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = List.class)
                            )
                    )
            })
    @GetMapping("/help")
    public ResponseEntity<List<String>> getHelp() {
        return ResponseEntity.ok(categoryService.getHelp());
    }
}
