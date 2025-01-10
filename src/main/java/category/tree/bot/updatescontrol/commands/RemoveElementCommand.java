package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для удаления категории.
 */
public class RemoveElementCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    public RemoveElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (chatStates.containsKey(chatId) && chatStates.get(chatId) == MainChatStates.REMOVE_ELEMENT) {
            // Если пользователь уже в состоянии REMOVE_ELEMENT, обрабатываем ввод.
            try {
                String result = categoryService.removeElement(messageText);
                bot.sendMessage(chatId, "Категория удалена: " + result);
            } catch (CategoryIsNotFound e) {
                bot.sendMessage(chatId, "Категория не найдена.");
            }
            chatStates.remove(chatId);
        } else {
            // Инициируем удаление.
            bot.sendMessage(chatId, "Введите название категории, которую хотите удалить. Все подкатегории также будут удалены.");
            chatStates.put(chatId, MainChatStates.REMOVE_ELEMENT);
        }
    }
}
