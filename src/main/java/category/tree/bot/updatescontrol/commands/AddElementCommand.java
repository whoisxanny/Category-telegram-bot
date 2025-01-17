package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryAlreadyExists;
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
     * @param bot             Экземпляр бота для отправки сообщений и управления состояниями.
     * @param categoryService Сервис для управления категориями.
     * @param chatStates      Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
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



    /**
     * Обрабатывает состояние добавления категории, проверяет корректность ввода,
     * выполняет добавление и уведомляет пользователя о результате.
     *
     * @param chatId      Идентификатор чата, в котором пришло сообщение.
     * @param messageText Текст сообщения, отправленного пользователем.
     * @param bot         Экземпляр бота для отправки сообщений.
     * @param update      Последний update в боте.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {
        String[] parts = messageText.split(" ", 2);
        try {
            if (parts.length == 1) {
                categoryService.addElement(parts[0], null);
                bot.sendMessage(chatId, "Новая категория добавлена: " + parts[0]);
            } else if (parts.length == 2 && !parts[1].contains(" ")) { // Убедимся, что подкатегория — одно слово
                categoryService.addElement(parts[0], parts[1]);
                bot.sendMessage(chatId, "Подкатегория добавлена: " + parts[1] + " к родителю " + parts[0]);
            } else {
                bot.sendMessage(chatId, "Ошибка! Введите название(-я) корректно!");
            }
        } catch (CategoryAlreadyExists e) {
            bot.sendMessage(chatId, "Ошибка: такая категория уже существует!");
        }
        chatStates.remove(chatId);
    }

}
