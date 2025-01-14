package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Класс для обработки команды загрузки Excel-документа с деревом категорий.
 *
 * Данный класс реализует интерфейс {@link CommandHandler} и позволяет пользователю
 * отправлять Excel-файл для добавления категорий в систему.
 */
public class UploadCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final Map<Long, MainChatStates> chatStates;
    private final CategoryService categoryService;

    /**
     * Конструктор для создания экземпляра команды загрузки.
     *
     * @param bot            Экземпляр управления обновлениями Telegram-бота.
     * @param chatStates     Состояния чатов для управления процессами бота.
     * @param categoryService Сервис для работы с категориями.
     */
    public UploadCommand(TelegramBotUpdatesControl bot, Map<Long, MainChatStates> chatStates, CategoryService categoryService) {
        this.bot = bot;
        this.chatStates = chatStates;
        this.categoryService = categoryService;
    }

    /**
     * Выполняет команду, отправляя пользователю запрос на загрузку Excel-документа.
     *
     * @param update Объект обновления из Telegram.
     */
    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();

        bot.sendMessage(chatId, "Пожалуйста, отправьте Excel-документ с деревом категорий.");
        chatStates.put(chatId, MainChatStates.UPLOAD_FILE);
    }

    /**
     * Обрабатывает отправленный пользователем файл, проверяя и добавляя категории в систему.
     *
     * @param chatId      Идентификатор чата.
     * @param messageText Текст сообщения.
     * @param bot         Экземпляр управления Telegram-ботом.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot) {
        bot.sendMessage(chatId, "Проверяем наличие документа...");
        if (bot.getUpdate().hasMessage() && bot.getUpdate().getMessage().hasDocument()) {
            bot.sendMessage(chatId, "Документ найден. Получаем файл...");
            try {
                String fileId = bot.getUpdate().getMessage().getDocument().getFileId();
                org.telegram.telegrambots.meta.api.objects.File telegramFile = bot.getFile(fileId);
                bot.sendMessage(chatId, "Скачиваем файл...");
                File downloadedFile = bot.downloadFile(telegramFile);
                bot.sendMessage(chatId, "Обрабатываем Excel...");
                processExcelFile(downloadedFile);
                bot.sendMessage(chatId, "Файл обработан, категории успешно добавлены!");

            } catch (Exception e) {
                bot.sendMessage(chatId, "Произошла ошибка при обработке файла: " + e.toString());
            } finally {
                chatStates.remove(chatId);
            }
        } else {
            bot.sendMessage(chatId, "Пожалуйста, отправьте Excel-документ.");
        }
    }

    /**
     * Обрабатывает содержимое Excel-документа и добавляет категории в систему.
     *
     * @param file Файл Excel, отправленный пользователем.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    private void processExcelFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getCell(0) == null) continue; // Пропуск пустых строк
                String categoryName = row.getCell(0).getStringCellValue();
                String parentName = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;

                categoryService.addElement(parentName, categoryName);
            }
        }
    }
}
