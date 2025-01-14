package category.tree.bot.updatescontrol;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс TelegramBotUpdatesControl управляет обработкой обновлений, поступающих от Telegram,
 * и взаимодействием с пользователями через бот.
 */
@Service
public class TelegramBotUpdatesControl extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesControl.class);

    private final String botUsername;
    private final String botToken;
    private final CategoryService categoryService;
    private final CommandRegistry commandRegistry;

    private final Map<Long, MainChatStates> chatStates = new HashMap<>();


    private Update lastUpdate;

    /**
     * Конструктор класса TelegramBotUpdatesControl.
     *
     * @param botUsername     Имя пользователя бота, заданное в настройках.
     * @param botToken        Токен доступа к Telegram API.
     * @param categoryService Сервис для работы с категориями.
     * @param commandRegistry Реестр команд бота.
     */
    public TelegramBotUpdatesControl(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            CategoryService categoryService,
            CommandRegistry commandRegistry) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.categoryService = categoryService;
        this.commandRegistry = commandRegistry;

        logger.info("Initializing TelegramBotUpdatesControl");
        registerCommands();
    }

    /**
     * Регистрирует все доступные команды бота.
     */
    private void registerCommands() {
        logger.info("Registering commands");
        commandRegistry.registerCommand("/start", new StartCommand(this));
        commandRegistry.registerCommand("/addElement", new AddElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/removeElement", new RemoveElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/viewTree", new ViewTreeCommand(this, categoryService));
        commandRegistry.registerCommand("/help", new HelpCommand(this));
        commandRegistry.registerCommand("/download", new DownloadCommand(this, categoryService));
        commandRegistry.registerCommand("/upload", new UploadCommand(this, chatStates, categoryService));
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     *
     * @param update Объект обновления, содержащий данные о новом событии.
     */
    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("Received update: {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            MainChatStates chatState = chatStates.getOrDefault(chatId, MainChatStates.DEFAULT);

            CommandHandler command = commandRegistry.getCommand(messageText);

            if (command != null) {
                logger.info("Executing command: {} for chatId: {}", messageText, chatId);
                command.execute(update);
            } else {
                logger.warn("Unknown command: {} for chatId: {}", messageText, chatId);
                handleCommand(chatId, chatState, messageText);
            }
        }
    }

    /**
     * Обрабатывает команды, не зарегистрированные в реестре команд,
     * в зависимости от текущего состояния чата.
     *
     * @param chatId      Идентификатор чата.
     * @param chatState   Текущее состояние чата.
     * @param messageText Текст сообщения, отправленного пользователем.
     */
    private void handleCommand(long chatId, MainChatStates chatState, String messageText) {
        logger.debug("Handling command for chatId: {}, state: {}, message: {}", chatId, chatState, messageText);
        CommandHandler commandHandler = new CommandFactory(commandRegistry, this, categoryService, chatStates)
                .getCommandHandler(messageText, chatState);

        if (commandHandler != null) {
            logger.info("Executing handler for chatId: {}", chatId);
            commandHandler.handle(chatId, messageText, this);
        } else {
            logger.warn("No handler found for chatId: {} and message: {}", chatId, messageText);
            sendMessage(chatId, "Команда не распознана или не поддерживается в текущем состоянии.");
        }
    }


    /**
     * Устанавливает новое состояние чата для указанного пользователя.
     *
     * @param chatId Идентификатор чата.
     * @param state  Новое состояние чата.
     */
    public void setChatState(long chatId, MainChatStates state) {
        logger.debug("Setting chat state for chatId: {} to state: {}", chatId, state);
        chatStates.put(chatId, state);
    }

    /**
     * Отправляет текстовое сообщение пользователю.
     *
     * @param chatId Идентификатор чата.
     * @param text   Текст сообщения.
     */
    public void sendMessage(long chatId, String text) {
        logger.debug("Sending message to chatId: {}: {}", chatId, text);
        try {
            execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            logger.error("Error sending message to chatId: {}: {}", chatId, e.getMessage(), e);
        }
    }

    /**
     * Возвращает имя пользователя бота.
     *
     * @return Имя пользователя бота.
     */
    @Override
    public String getBotUsername() {
        logger.debug("Getting bot username");
        return botUsername;
    }

    /**
     * Возвращает токен доступа бота.
     *
     * @return Токен доступа бота.
     */
    @Override
    public String getBotToken() {
        logger.debug("Getting bot token");
        return botToken;
    }


    /**
     * Возвращает последнее обновление.
     *
     * @return Объект Update, представляющий последнее обновление.
     */
    public Update getUpdate() {
        logger.debug("Getting last update");
        return lastUpdate;
    }

    /**
     * Получает файл из Telegram по его идентификатору.
     *
     * @param fileId Идентификатор файла.
     * @return Объект File, представляющий файл из Telegram.
     * @throws TelegramApiException Если произошла ошибка при запросе файла.
     */
    public File getFile(String fileId) throws TelegramApiException {
        logger.debug("Getting file with fileId: {}", fileId);
        GetFile getFile = new GetFile(fileId);
        return execute(getFile);
    }
}
