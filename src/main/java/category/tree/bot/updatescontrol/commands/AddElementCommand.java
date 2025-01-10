package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для добавления новой категории.
 */
public class AddElementCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    public AddElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, "Введите название новой категории или родителя и подкатегории через пробел. Пример: 'категория подкатегория'");
        chatStates.put(chatId, MainChatStates.ADD_ELEMENT);
    }
}
