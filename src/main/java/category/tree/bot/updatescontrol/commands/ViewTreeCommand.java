package category.tree.bot.updatescontrol.commands;

import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Команда для отображения дерева категорий.
 *
 * Данный класс реализует интерфейс {@link CommandHandler} и используется для
 * отображения дерева категорий в текстовом формате.
 */
public class ViewTreeCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;

    /**
     * Конструктор для создания команды отображения дерева категорий.
     *
     * @param bot            Экземпляр управления обновлениями Telegram-бота.
     * @param categoryService Сервис для работы с категориями.
     */
    public ViewTreeCommand(TelegramBotUpdatesControl bot, CategoryService categoryService) {
        this.bot = bot;
        this.categoryService = categoryService;
    }

    /**
     * Выполняет команду, отправляя пользователю текстовое представление дерева категорий.
     *
     * @param update Объект обновления из Telegram.
     */
    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String tree = categoryService.viewTree();
        bot.sendMessage(chatId, "Дерево категорий:\n" + tree);
    }

    /**
     * Обработчик команды.
     *
     * @param chatId      Идентификатор чата.
     * @param messageText Текст сообщения.
     * @param bot         Экземпляр управления Telegram-ботом.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot) {}
}
