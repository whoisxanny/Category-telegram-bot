package category.tree.bot.commands;

import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import category.tree.bot.updatescontrol.commands.HelpCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpCommandTest {

    private TelegramBotUpdatesControl botMock;
    private HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        botMock = Mockito.mock(TelegramBotUpdatesControl.class);
        helpCommand = new HelpCommand(botMock);
    }

    @Test
    void execute_ShouldSendHelpMessageWithKeyboard() throws TelegramApiException {
        // Arrange
        long chatId = 123456789L;
        Update update = mock(Update.class);
        var message = mock(org.telegram.telegrambots.meta.api.objects.Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        helpCommand.execute(update);

        verify(botMock, times(1)).execute(messageCaptor.capture());
        SendMessage sentMessage = messageCaptor.getValue();

        assertEquals(String.valueOf(chatId), sentMessage.getChatId());
        assertEquals("Вот список доступных команд:\n"
                + "/start - начать работу с ботом\n"
                + "/addElement - добавить категорию\n"
                + "/removeElement - удалить категорию\n"
                + "/viewTree - посмотреть дерево категорий\n"
                + "/download - скачать excel-файл дерева категорий\n"
                + "/upload - показать это сообщение\n", sentMessage.getText());

        assertTrue(sentMessage.getReplyMarkup() instanceof ReplyKeyboardMarkup);
        ReplyKeyboardMarkup keyboardMarkup = (ReplyKeyboardMarkup) sentMessage.getReplyMarkup();

        List<KeyboardRow> keyboard = keyboardMarkup.getKeyboard();
        assertEquals(2, keyboard.size());

        KeyboardRow row1 = keyboard.get(0);
        assertEquals(3, row1.size());
        assertEquals("/addElement", row1.get(0).getText());
        assertEquals("/removeElement", row1.get(1).getText());
        assertEquals("/viewTree", row1.get(2).getText());

        KeyboardRow row2 = keyboard.get(1);
        assertEquals(2, row2.size());
        assertEquals("/download", row2.get(0).getText());
        assertEquals("/upload", row2.get(1).getText());
    }

    @Test
    void execute_ShouldHandleException() throws TelegramApiException {
        // Arrange
        long chatId = 123456789L;
        Update update = mock(Update.class);
        var message = mock(org.telegram.telegrambots.meta.api.objects.Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        doThrow(new RuntimeException("Test exception")).when(botMock).execute(any(SendMessage.class));

        helpCommand.execute(update);

        verify(botMock, times(1)).execute(any(SendMessage.class));
    }
}
