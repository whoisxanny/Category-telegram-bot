package category.tree.bot.updatescontrol.handlers;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class UploadFileHandler implements ChatStateHandler {

    private final CategoryService categoryService;
    private final Map<Long, MainChatStates> chatStates;

    public UploadFileHandler(CategoryService categoryService, Map<Long, MainChatStates> chatStates) {
        this.categoryService = categoryService;
        this.chatStates = chatStates;
    }

    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot) {
        if (bot.getUpdate().hasMessage() && bot.getUpdate().getMessage().hasDocument()) {
            try {
                String fileId = bot.getUpdate().getMessage().getDocument().getFileId();
                File downloadedFile = bot.downloadFile(bot.getFile(fileId).getFilePath());

                processExcelFile(downloadedFile);
                bot.sendMessage(chatId, "Файл обработан, категории успешно добавлены!");

            } catch (Exception e) {
                bot.sendMessage(chatId, "Произошла ошибка при обработке файла: " + e.getMessage());
            } finally {
                chatStates.remove(chatId);
            }
        } else {
            bot.sendMessage(chatId, "Пожалуйста, отправьте Excel-документ.");
        }
    }

    private void processExcelFile(File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                String categoryName = row.getCell(0).getStringCellValue();
                String parentName = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                categoryService.addElement(parentName, categoryName);
            }
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            throw new IOException("Файл имеет некорректный формат. Убедитесь, что это файл Excel (.xlsx).", e);
        }
    }

}

