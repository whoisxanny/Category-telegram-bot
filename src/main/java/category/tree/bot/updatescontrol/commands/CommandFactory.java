package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;

import java.util.Map;

/**
 * Фабрика для создания обработчиков команд в зависимости от текущего состояния чата.
 */
public class CommandFactory {

    private final CommandRegistry commandRegistry;
    private final TelegramBotUpdatesControl bot;
    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    /**
     * Конструктор для создания фабрики команд.
     *
     * @param commandRegistry Реестр команд.
     * @param bot Экземпляр бота для взаимодействия с Telegram API.
     * @param categoryService Сервис для управления категориями.
     * @param chatStates Карта состояний чатов, где ключ — ID чата, а значение — текущее состояние.
     */
    public CommandFactory(
            CommandRegistry commandRegistry,
            TelegramBotUpdatesControl bot,
            CategoryService categoryService,
            Map<Long, MainChatStates> chatStates) {
        this.commandRegistry = commandRegistry;
        this.bot = bot;
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    /**
     * Возвращает обработчик команды в зависимости от имени команды и текущего состояния чата.
     *
     * @param commandName Имя команды, полученной от пользователя.
     * @param chatState Текущее состояние чата.
     * @return Обработчик команды или null, если обработчик не найден.
     */
    public CommandHandler getCommandHandler(String commandName, MainChatStates chatState) {
        switch (chatState) {
            case ADD_ELEMENT -> {
                return new AddElementCommand(bot,categoryService,chatStates);
            }
            case REMOVE_ELEMENT -> {
                return new RemoveElementCommand(bot, categoryService, chatStates);
            }
            case UPLOAD_FILE -> {
                return new UploadCommand(bot, chatStates, categoryService);
            }
            default -> {
                return null;
            }
        }
    }
}
