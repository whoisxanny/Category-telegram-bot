package category.tree.bot.updatescontrol;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.exceptions.CategoryAlreadyExcists;
import category.tree.bot.exceptions.CategoryIsNotFound;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.commands.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramBotUpdatesControl extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final CategoryService categoryService;
    private final CommandRegistry commandRegistry;

    private final Map<Long, MainChatStates> chatStates = new HashMap<>();

    public TelegramBotUpdatesControl(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            CategoryService categoryService,
            CommandRegistry commandRegistry) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.categoryService = categoryService;
        this.commandRegistry = commandRegistry;

        registerCommands();
    }

    /**
     * Регистрация всех доступных команд.
     */
    private void registerCommands() {
        commandRegistry.registerCommand("/start", new StartCommand(this));
        commandRegistry.registerCommand("/addElement", new AddElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/removeElement", new RemoveElementCommand(this, categoryService, chatStates));
        commandRegistry.registerCommand("/viewTree", new ViewTreeCommand(this, categoryService));
        commandRegistry.registerCommand("/help", new HelpCommand(this));
    }


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
     * Обрабатывает команды, требующие взаимодействия с деревом категорий.
     *
     * @param chatId      Идентификатор чата.
     * @param chatState   Состояние чата.
     * @param messageText Текст сообщения.
     */
    private void handleCommand(long chatId, MainChatStates chatState, String messageText) {
        switch (chatState) {
            case ADD_ELEMENT -> {
                String[] parts = messageText.split(" ", 2);
                try {
                    if (parts.length == 1) {
                        categoryService.addElement(parts[0], null);
                        sendMessage(chatId, "Новая категория добавлена: " + parts[0]);
                    } else if (parts.length == 2) {
                        categoryService.addElement(parts[0], parts[1]);
                        sendMessage(chatId, "Подкатегория добавлена: " + parts[1] + " к родителю " + parts[0]);
                    } else {
                        sendMessage(chatId, "Ошибка! Введите название(-я) корректно!");
                    }
                } catch (CategoryAlreadyExcists e) {
                    sendMessage(chatId, "Такая категория уже существует!");
                }
                chatStates.remove(chatId);
            }
            case REMOVE_ELEMENT -> {
                try {
                    String result = categoryService.removeElement(messageText);
                    sendMessage(chatId, "Категория и её подкатегории (при наличии) удалены: " + result);
                } catch (CategoryIsNotFound e) {
                    sendMessage(chatId, "Категория не найдена или не может быть удалена.");
                }
                chatStates.remove(chatId);
            }
            default -> sendMessage(chatId, "Неизвестная команда.");
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Устанавливает состояние чата для пользователя.
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
}
