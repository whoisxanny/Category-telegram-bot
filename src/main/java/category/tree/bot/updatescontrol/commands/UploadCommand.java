package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class UploadCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final Map<Long, MainChatStates> chatStates;

    public UploadCommand(TelegramBotUpdatesControl bot, Map<Long, MainChatStates> chatStates) {
        this.bot = bot;
        this.chatStates = chatStates;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        bot.sendMessage(chatId, "Пожалуйста, отправьте Excel-документ с деревом категорий.");
        chatStates.put(chatId, MainChatStates.UPLOAD_FILE);
    }
}


