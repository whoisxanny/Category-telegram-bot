package category.tree.bot.commands;

import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import category.tree.bot.updatescontrol.commands.ViewTreeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewTreeCommandTest {

    private TelegramBotUpdatesControl botMock;
    private CategoryService categoryServiceMock;
    private ViewTreeCommand viewTreeCommand;

    @BeforeEach
    void setUp() {
        botMock = Mockito.mock(TelegramBotUpdatesControl.class);
        categoryServiceMock = Mockito.mock(CategoryService.class);
        viewTreeCommand = new ViewTreeCommand(botMock, categoryServiceMock);
    }

    @Test
    void execute_ShouldSendCategoryTree() {
        long chatId = 123456789L;
        String treeRepresentation = "Root\n  ├── Category1\n  └── Category2";
        when(categoryServiceMock.viewTree()).thenReturn(treeRepresentation);

        Update update = mock(Update.class);
        var message = mock(org.telegram.telegrambots.meta.api.objects.Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        viewTreeCommand.execute(update);

        verify(botMock, times(1)).sendMessage(chatId, "Дерево категорий:\n" + treeRepresentation);
    }

    @Test
    void execute_ShouldHandleEmptyTree() {
        long chatId = 123456789L;
        String emptyTree = "Дерево категорий пусто.";
        when(categoryServiceMock.viewTree()).thenReturn(emptyTree);

        Update update = mock(Update.class);
        var message = mock(org.telegram.telegrambots.meta.api.objects.Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        viewTreeCommand.execute(update);

        verify(botMock, times(1)).sendMessage(chatId, "Дерево категорий:\n" + emptyTree);
    }

    @Test
    void execute_ShouldHandleException() {
        long chatId = 123456789L;
        Update update = mock(Update.class);
        var message = mock(org.telegram.telegrambots.meta.api.objects.Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        when(categoryServiceMock.viewTree()).thenThrow(new RuntimeException("Test exception"));

        assertDoesNotThrow(() -> viewTreeCommand.execute(update));

        verify(botMock, times(1)).sendMessage(chatId, "Произошла ошибка при отображении дерева категорий: Test exception");
    }

}
