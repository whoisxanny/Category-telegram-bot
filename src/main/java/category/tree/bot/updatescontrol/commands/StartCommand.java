package category.tree.bot.updatescontrol.commands;

import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Команда для отображения главного меню.
 */
public class StartCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;

    public StartCommand(TelegramBotUpdatesControl bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        bot.sendMessage(chatId, """
                Привет! Я бот, позволяющий работать с деревом категорий.
                Используйте /help, чтобы узнать, что я умею.
                """);
    }
}
