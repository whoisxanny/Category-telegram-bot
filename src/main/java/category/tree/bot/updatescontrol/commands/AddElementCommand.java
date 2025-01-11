package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryAlreadyExcists;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Команда для добавления новой категории в дерево категорий.
 */
public class AddElementCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    /**
     * Конструктор для создания команды AddElementCommand.
     *
     * @param bot            Экземпляр бота для отправки сообщений и управления состояниями.
     * @param categoryService Сервис для управления категориями.
     * @param chatStates     Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */
    public AddElementCommand(TelegramBotUpdatesControl bot, CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    /**
     * Выполняет логику команды для добавления новой категории.
     * Отправляет пользователю запрос на ввод данных для создания категории.
     *
     * @param update Объект обновления, содержащий данные о новом сообщении.
     */
    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, "Введите название новой категории или укажите родителя и подкатегорию через пробел. Пример: 'категория подкатегория'.");
        chatStates.put(chatId, MainChatStates.ADD_ELEMENT);
    }
}
