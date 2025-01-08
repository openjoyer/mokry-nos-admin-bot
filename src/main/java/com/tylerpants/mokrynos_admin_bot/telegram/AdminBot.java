package com.tylerpants.mokrynos_admin_bot.telegram;

import com.tylerpants.mokrynos_admin_bot.config.BotConfig;
import com.tylerpants.mokrynos_admin_bot.data.model.Animal;
import com.tylerpants.mokrynos_admin_bot.data.model.Item;
import com.tylerpants.mokrynos_admin_bot.data.model.Symptom;
import com.tylerpants.mokrynos_admin_bot.data.service.AnimalService;
import com.tylerpants.mokrynos_admin_bot.data.service.ItemService;
import com.tylerpants.mokrynos_admin_bot.data.service.SymptomService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AdminBot extends TelegramLongPollingBot {
    private final Map<Long, Integer> chatToMessageDelete = new ConcurrentHashMap<>();
    private final Map<Long, String> userBuffer = new ConcurrentHashMap<>();
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();
    private final BotConfig botConfig;
    private final BotButtons botButtons;

    private final AnimalService animalService;
    private final ItemService itemService;
    private final SymptomService symptomService;

    @Autowired
    public AdminBot(BotConfig botConfig, BotButtons botButtons, AnimalService animalService, ItemService itemService, SymptomService symptomService) {
        this.botConfig = botConfig;
        this.botButtons = botButtons;
        this.animalService = animalService;
        this.itemService = itemService;
        this.symptomService = symptomService;
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

            userStates.putIfAbsent(chatId, UserState.SEND_FEEDBACK);

            if (text.contains("sgo") || text.contains("ago")) {
                int p = Integer.parseInt(text.split("\\s")[1]);
                try {
                    addItemAction(chatId, p, messageId);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
            else if (text.contains("animal")) {
                String id = text.split("\\s")[1];
                addKeywords(chatId, id, true);
            }
            else if (text.contains("symptom")) {
                String id = text.split("\\s")[1];
                addKeywords(chatId, id, false);
                try {
                    addItemAction(chatId, 0, messageId);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
            else if (text.equals("/continue")) {
                addKeywords(chatId, "", false);
                try {
                    addItemAction(chatId, 0, -1);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
            else {
                answer(chatId, Objects.requireNonNull(text), messageId);
            }
        }
        else if(update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();
            text = update.getMessage().getText();

            userStates.putIfAbsent(chatId, UserState.SEND_FEEDBACK);

            if(BotConstants.getCommands().contains(text)) {
                answer(chatId, text, messageId);
            }
            else {
                UserState userState = userStates.get(chatId);

                if (UserState.LISTEN_DATA.equals(userState) && !text.equals(BotConstants.EXIT_BUTTON)) {
                    addKeywords(chatId, text, false);

                    switch (userSessions.get(chatId)) {
                        case ITEM_SESSION -> {
                            try {
                                addItemAction(chatId, 0, -1);
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage());
                            }
                        }
                        case ANIMAL_SESSION -> addAnimalAction(chatId);
                        case SYMPTOM_SESSION -> addSymptomAction(chatId);
                    }
                } else {
                    answer(chatId, text, messageId);
                }
            }
        }

    }

    private void answer(Long chatId, String message, Integer prevMessageId) {
        userBuffer.remove(chatId);
        userStates.replace(chatId, UserState.SEND_FEEDBACK);

        if (message.equals("/start") || message.contains(BotConstants.START_BUTTON)) {
            startAction(chatId);
        }
        else if (message.equals("/animal") || message.contains(BotConstants.ADD_ANIMAL_BUTTON) ||
                    message.equals("/symptom") || message.equals(BotConstants.ADD_SYMPTOM_BUTTON) ||
                    message.equals("/help") || message.contains(BotConstants.HELP_BUTTON) ||
                    message.equals("/item") || message.contains(BotConstants.ADD_ITEM_BUTTON)) {
            chatToMessageDelete.put(chatId, prevMessageId+2);
            transferAction(chatId, message);
        }
        else if(message.contains(BotConstants.EXIT_BUTTON)) {
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

    private void addKeywords(Long chatId, String keywords, boolean isId) {
        if(!userBuffer.containsKey(chatId)) {
            userBuffer.put(chatId, keywords+";");
        }
        else {
            String buffer = userBuffer.get(chatId);
            if(isId) {
                buffer += keywords + " ";
            } else {
                buffer += keywords + ";";
            }
            userBuffer.replace(chatId, buffer);
        }
        System.out.println(userBuffer);
    }

    private void setBotState(Long chatId, UserState userState) {
        if(userStates.get(chatId) == null) {
            userStates.put(chatId, userState);
        }
        else {
            userStates.replace(chatId, userState);
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
            userSessions.put(chatId, UserSession.ANIMAL_SESSION);
            setBotState(chatId, UserState.LISTEN_DATA);
            sendMessage.setText(BotConstants.ANIMAL_ADVICE);
        }
        else if(isSymptom) {
            userSessions.put(chatId, UserSession.SYMPTOM_SESSION);
            setBotState(chatId, UserState.LISTEN_DATA);
            sendMessage.setText(BotConstants.SYMPTOM_ADVICE);
        }
        else if(isItem) {
            userSessions.put(chatId, UserSession.ITEM_SESSION);
            setBotState(chatId, UserState.LISTEN_DATA);
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
            try {
                addItemAction(chatId, 0, -1);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
        else if(isAnimal) {
            addAnimalAction(chatId);
        }
        else if (isSymptom) {
            addSymptomAction(chatId);
        }

    }

    private void addItemAction(Long chatId, int p, int prevMessageId) throws TelegramApiException {
        if(!userBuffer.containsKey(chatId)) {
            executeItemSendMessage(chatId, BotConstants.ITEM_NAME, botButtons.exitMarkup());
        }
        else {
            // ищем количество уже добавленных данных
            int phase = (int) userBuffer.get(chatId).chars().filter(ch -> ch == ';').count();

            switch (phase) {
                case 1 -> executeItemSendMessage(chatId, BotConstants.ITEM_DESCRIPTION, botButtons.exitMarkup());
                case 2 -> {
                    if(prevMessageId == -1) {
                        executeItemSendMessage(chatId, BotConstants.ITEM_ANIMALS, botButtons.animalTypeMarkup(p));
                    }
                    else {
                        executeItemEditMessage(chatId, prevMessageId, botButtons.animalTypeMarkup(p));
                    }
                }
                case 3 -> {
                    if (prevMessageId == -1) {
                        executeItemSendMessage(chatId, BotConstants.ITEM_SYMPTOMS, botButtons.symptomTypeMarkup(p));
                    }
                    else {
                        executeItemEditMessage(chatId, prevMessageId, botButtons.symptomTypeMarkup(p));
                    }
                }
                case 4 -> executeItemSendMessage(chatId, BotConstants.ITEM_LINK, botButtons.exitMarkup());
                case 5 -> {
                    Item item = itemService.decodeItemObject(userBuffer.get(chatId));
                    itemService.save(item);
                    executeItemSendMessage(chatId, BotConstants.ADDED, botButtons.initMarkup());

                    setBotState(chatId, UserState.SEND_FEEDBACK);
                    userBuffer.remove(chatId);
                    userSessions.remove(chatId);
                }
                default -> log.error("Error in switch in addItemAction() wtf");
            }
        }
        if(userStates.get(chatId) != UserState.SEND_FEEDBACK) {
            setBotState(chatId, UserState.LISTEN_DATA);
        }
    }
    private void executeItemSendMessage(Long chatId, String text, ReplyKeyboard markup) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        execute(sendMessage);
    }

    private void executeItemEditMessage(Long chatId, Integer prevMessageId, InlineKeyboardMarkup markup) throws TelegramApiException {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(prevMessageId);
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setReplyMarkup(markup);

        execute(editMessageReplyMarkup);
    }

    private void addAnimalAction(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if(userBuffer.containsKey(chatId)) {
            Animal animal = new Animal();
            animal.setName(userBuffer.get(chatId).replaceAll(";", "").trim());
            animalService.save(animal);

            sendMessage.setReplyMarkup(botButtons.initMarkup());
            sendMessage.setText(BotConstants.ADDED);

            userBuffer.remove(chatId);
            userSessions.remove(chatId);
            setBotState(chatId, UserState.SEND_FEEDBACK);
        }
        else {
//            sendMessage.setReplyMarkup(botButtons.exitMarkup());
            sendMessage.setText(BotConstants.ENTER_ANIMAL_NAME);
            setBotState(chatId, UserState.LISTEN_DATA);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
    private void addSymptomAction(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (userBuffer.containsKey(chatId)) {
            Symptom symptom = new Symptom();
            symptom.setName(userBuffer.get(chatId).replaceAll(";", "").trim());
            symptomService.save(symptom);

            sendMessage.setReplyMarkup(botButtons.initMarkup());
            sendMessage.setText(BotConstants.ADDED);

            userBuffer.remove(chatId);
            userSessions.remove(chatId);
            setBotState(chatId, UserState.SEND_FEEDBACK);
        }
        else {
//            sendMessage.setReplyMarkup(botButtons.exitMarkup());
            sendMessage.setText(BotConstants.ENTER_SYMPTOM_NAME);
            setBotState(chatId, UserState.LISTEN_DATA);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void startAction(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotConstants.START_TEXT);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(botButtons.initMarkup());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void exitAction(Long chatId) {
        Integer messageId = chatToMessageDelete.get(chatId);
        chatToMessageDelete.remove(chatId);
        userBuffer.remove(chatId);

        if(messageId != null) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);

            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
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
