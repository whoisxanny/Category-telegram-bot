package category.tree.bot.updatescontrol.commands;

import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработки команд бота.
 */
public interface CommandHandler {
    void execute(Update update);

    /**
     * Обрабатывает пользовательский ввод для текущего состояния чата.
     *
     * @param chatId      Идентификатор чата, в котором пришло сообщение.
     * @param messageText Текст сообщения, отправленного пользователем.
     * @param bot         Экземпляр бота для отправки сообщений или выполнения действий.
     */
    void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update);
}