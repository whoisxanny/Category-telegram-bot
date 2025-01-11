package category.tree.bot.updatescontrol.handlers;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryAlreadyExcists;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;

import java.util.Map;

/**
 * Обработчик состояния чата для добавления новой категории или подкатегории.
 * Отвечает за обработку пользовательского ввода и добавление категории в дерево.
 */
public class AddElementHandler implements ChatStateHandler {

    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    /**
     * Конструктор обработчика AddElementHandler.
     *
     * @param categoryService Сервис для работы с категориями.
     * @param chatStates      Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */
    public AddElementHandler(CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    /**
     * Обрабатывает состояние добавления категории, проверяет корректность ввода,
     * выполняет добавление и уведомляет пользователя о результате.
     *
     * @param chatId      Идентификатор чата, в котором пришло сообщение.
     * @param messageText Текст сообщения, отправленного пользователем.
     * @param bot         Экземпляр бота для отправки сообщений.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot) {
        String[] parts = messageText.split(" ", 2); // Разделяем текст на части (категория и подкатегория).
        try {
            if (parts.length == 1) {
                // Если пользователь ввел только одну категорию.
                categoryService.addElement(parts[0], null);
                bot.sendMessage(chatId, "Новая категория добавлена: " + parts[0]);
            } else if (parts.length == 2) {
                // Если пользователь ввел родительскую категорию и подкатегорию.
                categoryService.addElement(parts[0], parts[1]);
                bot.sendMessage(chatId, "Подкатегория добавлена: " + parts[1] + " к родителю " + parts[0]);
            } else {
                // Если ввод некорректен (например, больше двух частей).
                bot.sendMessage(chatId, "Ошибка! Введите название(-я) корректно!");
            }
        } catch (CategoryAlreadyExcists e) {
            // Если категория уже существует.
            bot.sendMessage(chatId, "Ошибка: такая категория уже существует!");
        }
        // Удаляем состояние чата после завершения обработки.
        chatStates.remove(chatId);
    }
}
