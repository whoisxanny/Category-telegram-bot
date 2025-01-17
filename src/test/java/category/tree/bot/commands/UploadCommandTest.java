package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadCommandTest {

    @Mock
    private TelegramBotUpdatesControl bot;

    @Mock
    private CategoryService categoryService;

    private Map<Long, MainChatStates> chatStates;

    @InjectMocks
    private UploadCommand uploadCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatStates = new HashMap<>();
        uploadCommand = new UploadCommand(bot, chatStates, categoryService);
    }

    @Test
    void testExecute() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        uploadCommand.execute(update);

        verify(bot).sendMessage(12345L, "Пожалуйста, отправьте Excel-документ с деревом категорий.");
        assertEquals(MainChatStates.UPLOAD_FILE, chatStates.get(12345L));
    }

    @Test
    void testHandle_ValidExcelFile_FromResources() throws Exception {
        long chatId = 12345L;
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Document document = mock(Document.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(message.getDocument()).thenReturn(document);
        when(document.getFileId()).thenReturn("file_id");

        org.telegram.telegrambots.meta.api.objects.File telegramFile = mock(org.telegram.telegrambots.meta.api.objects.File.class);
        when(bot.getFile("file_id")).thenReturn(telegramFile);

        File testExcelFile = new File("src/test/resources/categories_tree (3).xlsx");
        assertTrue(testExcelFile.exists(), "Тестовый Excel-файл должен существовать");

        when(bot.downloadFile(telegramFile)).thenReturn(testExcelFile);

        uploadCommand.handle(chatId, null, bot, update);

        verify(bot).sendMessage(chatId, "Обрабатываем Excel...");
        verify(bot).sendMessage(chatId, "Файл обработан, категории успешно добавлены!");
        assertFalse(chatStates.containsKey(chatId), "Состояние чата должно быть удалено после обработки");
    }


    @Test
    void testHandle_InvalidFile() throws Exception {
        long chatId = 12345L;
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Document document = mock(Document.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(message.getDocument()).thenReturn(document);
        when(document.getFileId()).thenReturn("file_id");

        when(bot.getFile("file_id")).thenReturn(null);

        uploadCommand.handle(chatId, null, bot, update);

        verify(bot).sendMessage(chatId, "Ошибка: файл не загружен.");
        assertFalse(chatStates.containsKey(chatId));
    }

    @Test
    void testHandle_ProcessingError() throws Exception {
        long chatId = 12345L;
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Document document = mock(Document.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(message.getDocument()).thenReturn(document);
        when(document.getFileId()).thenReturn("file_id");

        org.telegram.telegrambots.meta.api.objects.File telegramFile = mock(org.telegram.telegrambots.meta.api.objects.File.class);
        when(bot.getFile("file_id")).thenReturn(telegramFile);

        File tempFile = File.createTempFile("test", ".xlsx");
        tempFile.delete();
        when(bot.downloadFile(telegramFile)).thenReturn(tempFile);

        uploadCommand.handle(chatId, null, bot, update);

        verify(bot).sendMessage(chatId, "Ошибка: файл не загружен.");
        assertFalse(chatStates.containsKey(chatId));
    }
}
