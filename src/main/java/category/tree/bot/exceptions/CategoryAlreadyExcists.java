package category.tree.bot.exceptions;

public class CategoryAlreadyExcists extends RuntimeException {

    public CategoryAlreadyExcists() {
        super("Категория уже существует!");
    }
}
