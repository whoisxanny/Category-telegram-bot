package category.tree.bot.updatescontrol.commands;

import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Команда для отображения дерева категорий.
 */
public class ViewTreeCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;

    public ViewTreeCommand(TelegramBotUpdatesControl bot, CategoryService categoryService) {
        this.bot = bot;
        this.categoryService = categoryService;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String tree = categoryService.viewTree();
        bot.sendMessage(chatId, "Дерево категорий:\n" + tree);
    }
}
