package category.tree.bot.updatescontrol.commands;

import category.tree.bot.chatStates.MainChatStates;
import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Класс для обработки команды загрузки Excel-документа с деревом категорий.
 * Данный класс реализует интерфейс {@link CommandHandler} и позволяет пользователю
 * отправлять Excel-файл для добавления категорий в систему.
 */
public class UploadCommand implements CommandHandler {

    private final TelegramBotUpdatesControl bot;
    private final Map<Long, MainChatStates> chatStates;
    private final CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(UploadCommand.class);

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
        logger.info("Состояние чата для chatId: {} обновлено на UPLOAD_FILE", chatId);
    }

    /**
     * Обрабатывает файл, отправленный пользователем.
     *
     * @param chatId     Идентификатор чата, из которого пришёл файл.
     * @param messageText Текст сообщения (не используется в данном методе).
     * @param bot         Экземпляр управления Telegram-ботом.
     * @param update      Объект обновления из Telegram.
     */
    @Override
    public void handle(long chatId, String messageText, TelegramBotUpdatesControl bot, Update update) {
        Message message = update.getMessage();
        Document document = message.getDocument();
        String fileId = document.getFileId();
        logger.info("Получен fileId: {}", fileId);

        try {

            org.telegram.telegrambots.meta.api.objects.File telegramFile = bot.getFile(fileId);
            logger.info("Получен объект File с fileId: {}", fileId);

            File downloadedFile = bot.downloadFile(telegramFile);
            if (downloadedFile == null || !downloadedFile.exists()) {
                bot.sendMessage(chatId, "Ошибка: файл не загружен.");
                logger.error("Файл не загружен для fileId: {}", fileId);
                return;
            }

            logger.info("Файл скачан: {}", downloadedFile.getAbsolutePath());
            bot.sendMessage(chatId, "Обрабатываем Excel...");

            processExcelFile(downloadedFile);
            bot.sendMessage(chatId, "Файл обработан, категории успешно добавлены!");

        } catch (Exception e) {
            bot.sendMessage(chatId, "Ошибка при обработке файла: " + e.getMessage());
            logger.error("Ошибка при обработке файла: ", e);
        } finally {
            chatStates.remove(chatId);
            logger.info("Состояние чата для chatId: {} удалено.", chatId);
        }
    }

    /**
     * Обрабатывает содержимое Excel-документа и добавляет категории в систему.
     *
     * @param file Файл Excel, отправленный пользователем.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    public void processExcelFile(File file) throws IOException {
        logger.info("Начинаем обработку Excel файла: {}", file.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Обрабатываем лист: {}", sheet.getSheetName());

            for (Row row : sheet) {
                if (row.getCell(0) == null) continue;

                String categoryName = row.getCell(0).getStringCellValue();
                String parentName = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;

                logger.info("Добавление категории: {}, Родитель: {}", categoryName, parentName);

                categoryService.addElement(parentName, categoryName);
            }
        } catch (IOException e) {
            logger.error("Ошибка при чтении Excel файла: ", e);
            throw e;
        }
    }
}
