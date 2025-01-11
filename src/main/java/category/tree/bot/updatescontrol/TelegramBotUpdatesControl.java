package category.tree.bot.updatescontrol;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryAlreadyExcists;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.commands.*;
import category.tree.bot.updatescontrol.handlers.ChatStateHandler;
import category.tree.bot.updatescontrol.handlers.ChatStateHandlerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

    private final String botUsername;
    private final String botToken;
    private final CategoryService categoryService;
    private final CommandRegistry commandRegistry;
    private final ChatStateHandlerFactory handlerFactory;

    private final Map<Long, MainChatStates> chatStates = new HashMap<>();

    /**
     * Конструктор класса TelegramBotUpdatesControl.
     *
     * @param botUsername    Имя пользователя бота, заданное в настройках.
     * @param botToken       Токен доступа к Telegram API.
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
        this.handlerFactory = new ChatStateHandlerFactory(categoryService, chatStates);

        registerCommands();
    }

    /**
     * Регистрирует все доступные команды бота.
     */
    private void registerCommands() {
        commandRegistry.registerCommand("/start", new StartCommand(this));
        commandRegistry.registerCommand("/addElement", new AddElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/removeElement", new RemoveElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/viewTree", new ViewTreeCommand(this, categoryService));
        commandRegistry.registerCommand("/help", new HelpCommand(this));
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     *
     * @param update Объект обновления, содержащий данные о новом событии.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            MainChatStates chatState = chatStates.getOrDefault(chatId, MainChatStates.DEFAULT);

            CommandHandler command = commandRegistry.getCommand(messageText);

            if (command != null) {
                command.execute(update);
            } else {
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
        ChatStateHandler handler = handlerFactory.getHandler(chatState);
        handler.handle(chatId, messageText, this);
    }

    /**
     * Устанавливает новое состояние чата для указанного пользователя.
     *
     * @param chatId Идентификатор чата.
     * @param state  Новое состояние чата.
     */
    public void setChatState(long chatId, MainChatStates state) {
        chatStates.put(chatId, state);
    }

    /**
     * Отправляет текстовое сообщение пользователю.
     *
     * @param chatId Идентификатор чата.
     * @param text   Текст сообщения.
     */
    public void sendMessage(long chatId, String text) {
        try {
            execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает имя пользователя бота.
     *
     * @return Имя пользователя бота.
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Возвращает токен доступа бота.
     *
     * @return Токен доступа бота.
     */
    @Override
    public String getBotToken() {
        return botToken;
    }
}
