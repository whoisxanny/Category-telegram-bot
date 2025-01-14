package category.tree.bot.commands;

import category.tree.bot.entity.Category;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import category.tree.bot.updatescontrol.commands.DownloadCommand;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DownloadCommandTest {

    @Mock
    private TelegramBotUpdatesControl bot;

    @Mock
    private CategoryService categoryService;

    private DownloadCommand downloadCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        downloadCommand = new DownloadCommand(bot, categoryService);
    }

//    @Test
//    void testExecute_Success() throws Exception {
//        long chatId = 12345L;
//        Update update = mock(Update.class);
//        when(update.getMessage().getChatId()).thenReturn(chatId);
//
//        List<Category> categories = Arrays.asList(
//                new Category("Категория1", null),
//                new Category("Категория2", new Category("Категория1", null))
//        );
//        when(categoryService.getAllCategories()).thenReturn(categories);
//
//        downloadCommand.execute(update);
//
//        ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
//        verify(bot).execute(captor.capture());
//
//        SendDocument sendDocument = captor.getValue();
//        assertEquals(String.valueOf(chatId), sendDocument.getChatId());
//        assertEquals("categories_tree.xlsx", sendDocument.getDocument().getMediaName());
//        assertEquals("Дерево категорий", sendDocument.getCaption());
//
//        // Проверяем содержимое файла
//        File tempFile = new File(sendDocument.getDocument().getNewMediaFile());
//        assertTrue(tempFile.exists());
//
//        try (FileInputStream fis = new FileInputStream(tempFile);
//             var workbook = WorkbookFactory.create(fis)) {
//            var sheet = workbook.getSheet("Categories");
//            assertNotNull(sheet);
//            assertEquals("Категория", sheet.getRow(0).getCell(0).getStringCellValue());
//            assertEquals("Родительская категория", sheet.getRow(0).getCell(1).getStringCellValue());
//            assertEquals("Категория1", sheet.getRow(1).getCell(0).getStringCellValue());
//            assertEquals("", sheet.getRow(1).getCell(1).getStringCellValue());
//            assertEquals("Категория2", sheet.getRow(2).getCell(0).getStringCellValue());
//            assertEquals("Категория1", sheet.getRow(2).getCell(1).getStringCellValue());
//        }
//
//        // Убедимся, что временный файл был удалён
//        assertFalse(tempFile.exists());
//    }


//    @Test
//    void testExecute_EmptyCategories() throws Exception {
//        long chatId = 12345L;
//        Update update = mock(Update.class);
//        when(update.getMessage().getChatId()).thenReturn(chatId);
//
//        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
//
//        downloadCommand.execute(update);
//
//        ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
//        verify(bot).execute(captor.capture());
//
//        SendDocument sendDocument = captor.getValue();
//        assertEquals(String.valueOf(chatId), sendDocument.getChatId());
//        assertEquals("categories_tree.xlsx", sendDocument.getDocument().getFileName());
//        assertEquals("Дерево категорий", sendDocument.getCaption());
//
//        // Проверяем, что файл пустой (только заголовок)
//        File file = new File(sendDocument.getDocument().getNewMediaFile());
//        assertTrue(file.exists());
//
//        try (FileInputStream fis = new FileInputStream(file);
//             var workbook = WorkbookFactory.create(fis)) {
//            var sheet = workbook.getSheet("Categories");
//            assertNotNull(sheet);
//            assertEquals("Категория", sheet.getRow(0).getCell(0).getStringCellValue());
//            assertEquals("Родительская категория", sheet.getRow(0).getCell(1).getStringCellValue());
//            assertNull(sheet.getRow(1)); // Нет данных
//        }
//
//        // Убедимся, что временный файл был удалён
//        assertFalse(file.exists());
//    }

    @Test
    void testExecute_ExceptionHandling() throws Exception {
        long chatId = 12345L;
        Update update = mock(Update.class);
        when(update.getMessage().getChatId()).thenReturn(chatId);

        when(categoryService.getAllCategories()).thenThrow(new RuntimeException("Test exception"));

        downloadCommand.execute(update);

        verify(bot).sendMessage(chatId, "Ошибка при генерации файла: Test exception");
    }
}
