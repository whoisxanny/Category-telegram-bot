package category.tree.bot.updatescontrol.handlers;

import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;

/**
 * Интерфейс ChatStateHandler определяет метод для обработки состояний чата.
 * Реализации этого интерфейса обрабатывают пользовательский ввод в зависимости от текущего состояния чата.
 */
public interface ChatStateHandler {

    /**
     * Обрабатывает пользовательский ввод для текущего состояния чата.
     *
     * @param chatId      Идентификатор чата, в котором пришло сообщение.
     * @param messageText Текст сообщения, отправленного пользователем.
     * @param bot         Экземпляр бота для отправки сообщений или выполнения действий.
     */
    void handle(long chatId, String messageText, TelegramBotUpdatesControl bot);
}
