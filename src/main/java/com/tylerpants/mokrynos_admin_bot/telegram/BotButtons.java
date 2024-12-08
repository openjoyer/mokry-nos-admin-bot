package com.tylerpants.mokrynos_admin_bot.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class BotButtons {
    public ReplyKeyboardMarkup initMarkup() {
        KeyboardButton helpButton = new KeyboardButton(BotConstants.HELP_BUTTON);
        KeyboardButton addItemButton = new KeyboardButton(BotConstants.ADD_ITEM_BUTTON);
        KeyboardButton addAnimalButton = new KeyboardButton(BotConstants.ADD_ANIMAL_BUTTON);
        KeyboardButton addSymptomButton = new KeyboardButton(BotConstants.ADD_SYMPTOM_BUTTON);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(addItemButton);
        row1.add(helpButton);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(addAnimalButton);
        row2.add(addSymptomButton);

        List<KeyboardRow> rows = List.of(row1, row2);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }

    public ReplyKeyboardMarkup exitMarkup() {
        KeyboardButton exitButton = new KeyboardButton(BotConstants.EXIT_BUTTON);
        KeyboardRow row = new KeyboardRow();
        row.add(exitButton);

        List<KeyboardRow> keyboard = List.of(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
