package category.tree.bot.updatescontrol.commands;

import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда для отображения помощи и функционала бота.
 */
public class HelpCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;

    public HelpCommand(TelegramBotUpdatesControl bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        try {
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(false);

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton("/addElement"));
            row1.add(new KeyboardButton("/removeElement"));

            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("/viewTree"));

            keyboard.add(row1);
            keyboard.add(row2);
            keyboardMarkup.setKeyboard(keyboard);

            SendMessage message = SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Вот список доступных команд:\n"
                            + "/start - начать работу с ботом\n"
                            + "/addElement - добавить категорию\n"
                            + "/removeElement - удалить категорию\n"
                            + "/viewTree - посмотреть дерево категорий\n"
                            + "/help - показать это сообщение")
                    .replyMarkup(keyboardMarkup)
                    .build();

            bot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}