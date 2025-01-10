package category.tree.bot.updatescontrol.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработки команд бота.
 */
public interface CommandHandler {
    void execute(Update update);
}