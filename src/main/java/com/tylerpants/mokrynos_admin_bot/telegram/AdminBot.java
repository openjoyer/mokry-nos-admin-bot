package com.tylerpants.mokrynos_admin_bot.telegram;

import com.tylerpants.mokrynos_admin_bot.config.BotConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AdminBot extends TelegramLongPollingBot {
    private final Map<Long, Integer> chatToMessageDelete = new HashMap<>();
    private final BotConfig botConfig;
    private final BotButtons botButtons;

    @Autowired
    public AdminBot(BotConfig botConfig, BotButtons botButtons) {
        this.botConfig = botConfig;
        this.botButtons = botButtons;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Long chatId;
        String text;
        Integer messageId;

        if(update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            text = update.getCallbackQuery().getData();
            Message message = (Message) update.getCallbackQuery().getMessage();
            messageId = message.getMessageId();

            answer(chatId, text, messageId);
        } else if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();

            if(update.getMessage().hasText()) {
                text = update.getMessage().getText();

                answer(chatId, text, messageId);

            }
        }
    }

    private void answer(Long chatId, String message, Integer prevMessageId) {
        if (message.equals("/start") || message.contains(BotConstants.START_BUTTON)) {
            startAction(chatId);
        } else if (message.equals("/animal") || message.contains(BotConstants.ADD_ANIMAL_BUTTON) ||
                    message.equals("/symptom") || message.equals(BotConstants.ADD_SYMPTOM_BUTTON) ||
                    message.equals("/help") || message.contains(BotConstants.HELP_BUTTON) ||
                    message.equals("/item") || message.contains(BotConstants.ADD_ITEM_BUTTON)) {
            chatToMessageDelete.put(chatId, prevMessageId+2);
            transferAction(chatId, message);
        } else if(message.contains(BotConstants.EXIT_BUTTON)) {
            exitAction(chatId);
        }
        else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(BotConstants.UNKNOWN_COMMAND);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void transferAction(Long chatId, String messageText) {
        boolean isAnimal = messageText.equals("/animal") || messageText.contains(BotConstants.ADD_ANIMAL_BUTTON);
        boolean isSymptom = messageText.equals("/symptom") || messageText.contains(BotConstants.ADD_SYMPTOM_BUTTON);
        boolean isHelp = messageText.equals("/help") || messageText.contains(BotConstants.HELP_BUTTON);
        boolean isItem = messageText.equals("/item") || messageText.contains(BotConstants.ADD_ITEM_BUTTON);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if(isAnimal) {
            sendMessage.setText(BotConstants.ANIMAL_ADVICE);
        }
        else if(isSymptom) {
            sendMessage.setText(BotConstants.SYMPTOM_ADVICE);
        }
        else if(isItem) {
            sendMessage.setText(BotConstants.ITEM_ADVICE);
        }
        else if (isHelp) {
            sendMessage.setText(BotConstants.HELP_ADVICE);
        }

        sendMessage.setReplyMarkup(botButtons.exitMarkup());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        if (isHelp) {
            helpAction(chatId);
        }
        else if (isItem) {
            addItemAction(chatId);
        }
        else if(isAnimal) {
            addAnimalAction(chatId);
        }
        else if (isSymptom) {
            addSymptomAction(chatId);
        }

    }

    private void addItemAction(Long chatId) {

    }
    private void addAnimalAction(Long chatId) {

    }
    private void addSymptomAction(Long chatId) {

    }

    private void startAction(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotConstants.START_TEXT);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(botButtons.initMarkup());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void exitAction(Long chatId) {
        Integer messageId = chatToMessageDelete.get(chatId);
        chatToMessageDelete.remove(chatId);

        if(messageId != null) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);

            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            startAction(chatId);
        }
    }
    private void helpAction(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(BotConstants.HELP_TEXT);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
