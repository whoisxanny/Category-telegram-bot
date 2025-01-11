package category.tree.bot.updatescontrol.handlers;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;

import java.util.Map;

/**
 * Фабрика обработчиков состояний чата.
 * Предоставляет соответствующий обработчик состояния на основе текущего состояния чата.
 */
public class ChatStateHandlerFactory {

    private final Map<MainChatStates, ChatStateHandler> handlers;

    /**
     * Конструктор фабрики ChatStateHandlerFactory.
     *
     * @param categoryService Сервис для работы с категориями.
     * @param chatStates      Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     *                         Используется для передачи в обработчики.
     */
    public ChatStateHandlerFactory(CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        handlers = Map.of(
                MainChatStates.ADD_ELEMENT, new AddElementHandler(categoryService, chatStates),
                MainChatStates.REMOVE_ELEMENT, new RemoveElementHandler(categoryService, chatStates)
        );
    }

    /**
     * Возвращает обработчик для заданного состояния чата.
     *
     * @param state Текущее состояние чата.
     * @return Обработчик состояния чата или обработчик по умолчанию, если состояние неизвестно.
     */
    public ChatStateHandler getHandler(MainChatStates state) {
        return handlers.getOrDefault(state, (chatId, messageText, bot) ->
                bot.sendMessage(chatId, "Неизвестная команда.")
        );
    }
}
