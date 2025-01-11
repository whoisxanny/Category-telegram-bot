package category.tree.bot.updatescontrol.handlers;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;

import java.util.Map;

/**
 * Обработчик состояния удаления категории.
 * Выполняет удаление категории и её подкатегорий из дерева категорий.
 */
public class RemoveElementHandler implements ChatStateHandler {

    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    /**
     * Конструктор RemoveElementHandler.
     *
     * @param categoryService Сервис для работы с категориями.
     * @param chatStates      Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */
    public RemoveElementHandler(CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    /**
     * Обрабатывает удаление категории на основе пользовательского ввода.
     *
     * @param chatId      Идентификатор чата, в котором выполняется удаление.
     * @param messageText Название категории для удаления, отправленное пользователем.
     * @param bot         Экземпляр бота для отправки сообщений пользователю.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot) {
        try {
            String result = categoryService.removeElement(messageText);
            bot.sendMessage(chatId, "Категория и её подкатегории (при наличии) удалены: " + result);
        } catch (CategoryIsNotFound e) {
            bot.sendMessage(chatId, "Категория не найдена или не может быть удалена.");
        } finally {
            // Сброс состояния чата после завершения операции.
            chatStates.remove(chatId);
        }
    }
}
