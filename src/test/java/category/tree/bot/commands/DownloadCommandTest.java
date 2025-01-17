package category.tree.bot.commands;

import category.tree.bot.entity.Category;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import category.tree.bot.updatescontrol.commands.DownloadCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DownloadCommandTest {

    @Mock
    private TelegramBotUpdatesControl bot;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private DownloadCommand downloadCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_Success() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        File tempFile = File.createTempFile("categories_tree", ".xlsx");
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        doAnswer(invocation -> {
            SendDocument sendDocument = invocation.getArgument(0);
            InputFile document = sendDocument.getDocument();

            // Проверяем, что имя файла корректно установлено
            assertEquals("categories_tree.xlsx", document.getMediaName());
            return null;
        }).when(bot).execute(any(SendDocument.class));

        downloadCommand.execute(update);

        verify(bot, times(1)).execute(any(SendDocument.class));
        tempFile.deleteOnExit();
    }


    @Test
    void testExecute_GenerationFailure() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(12345L);

        doThrow(RuntimeException.class).when(categoryService).getAllCategories();

        downloadCommand.execute(update);

        verify(bot).sendMessage(12345L, "Ошибка при генерации файла: null");
    }

    @Test
    void testGenerateExcelFile_CorrectFileStructure() throws IOException {
        List<Category> mockCategories = new ArrayList<>();
        Category parent = new Category();
        parent.setName("Родитель");

        Category child = new Category();
        child.setName("Дочерняя");
        child.setParent(parent);

        mockCategories.add(parent);
        mockCategories.add(child);

        when(categoryService.getAllCategories()).thenReturn(mockCategories);

        File file = downloadCommand.generateExcelFile();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        file.deleteOnExit();
    }

    @Test
    void testGenerateExcelFile_EmptyCategories() throws IOException {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        File file = downloadCommand.generateExcelFile();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        file.deleteOnExit();
    }
}
