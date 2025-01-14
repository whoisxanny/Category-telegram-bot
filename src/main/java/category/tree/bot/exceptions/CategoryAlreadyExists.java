package category.tree.bot.exceptions;

public class CategoryAlreadyExists extends RuntimeException {

    public CategoryAlreadyExists() {
        super("Категория уже существует!");
    }
}
