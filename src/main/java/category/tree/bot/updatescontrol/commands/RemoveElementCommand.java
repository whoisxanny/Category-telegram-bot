package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для удаления категории из дерева категорий.
 * Обрабатывает ввод пользователя, удаляя указанную категорию и все её подкатегории.
 */
public class RemoveElementCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    /**
     * Конструктор команды RemoveElementCommand.
     *
     * @param bot             Экземпляр бота для отправки сообщений и управления состояниями.
     * @param categoryService Сервис для работы с категориями.
     * @param chatStates      Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */
    public RemoveElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    /**
     * Выполняет логику команды, удаляя указанную категорию.
     * Если команда вызвана впервые, запрашивает у пользователя название категории.
     * При повторном вызове выполняет удаление категории, используя введённые данные.
     *
     * @param update Объект обновления, содержащий данные о новом сообщении.
     */
    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        bot.sendMessage(chatId, "Введите название категории, которую хотите удалить. Все её подкатегории также будут удалены.");
        chatStates.put(chatId, MainChatStates.REMOVE_ELEMENT);
    }
}
