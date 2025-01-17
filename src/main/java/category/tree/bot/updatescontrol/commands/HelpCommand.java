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
 * Предоставляет список доступных команд и отображает пользовательскую клавиатуру с кнопками для удобного взаимодействия.
 */
public class HelpCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;


    public HelpCommand(TelegramBotUpdatesControl bot) {
        this.bot = bot;
    }

    /**
     * Выполняет логику команды, отображая пользователю список доступных команд
     * и предоставляя удобную клавиатуру для выбора действий.
     *
     * @param update Объект обновления, содержащий данные о новом сообщении.
     */
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
            row1.add(new KeyboardButton("/viewTree"));

            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("/download"));
            row2.add(new KeyboardButton("/upload"));

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
                            + "/download - скачать excel-файл дерева категорий\n"
                            + "/upload - показать это сообщение\n")
                    .replyMarkup(keyboardMarkup)
                    .build();

            bot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {}
}
