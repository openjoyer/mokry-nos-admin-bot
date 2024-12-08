package com.tylerpants.mokrynos_admin_bot.config;

import com.tylerpants.mokrynos_admin_bot.telegram.AdminBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class BotInitializer {
    private final AdminBot adminBot;

    @Autowired
    public BotInitializer(AdminBot adminBot) {
        this.adminBot = adminBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(adminBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}