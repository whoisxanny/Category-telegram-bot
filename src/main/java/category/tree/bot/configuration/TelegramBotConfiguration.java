package category.tree.bot.configuration;

import category.tree.bot.service.services.CategoryService;
import category.tree.bot.updatescontrol.commands.CommandRegistry;
import category.tree.bot.updatescontrol.TelegramBotUpdatesControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfiguration {


    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }


    @Bean
    public TelegramBotUpdatesControl telegramBot(TelegramBotsApi botsApi,
                                                 CommandRegistry commandRegistry,
                                                 CategoryService categoryService,
                                                 @Value("${telegram.bot.username}") String username,
                                                 @Value("${telegram.bot.token}") String token) throws TelegramApiException {
        TelegramBotUpdatesControl bot = new TelegramBotUpdatesControl(username, token, categoryService, commandRegistry);
        botsApi.registerBot(bot);
        System.out.println("Bot successfully registered!");
        return bot;
    }
}
