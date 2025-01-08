package com.tylerpants.mokrynos_admin_bot.telegram;

import com.tylerpants.mokrynos_admin_bot.data.model.Animal;
import com.tylerpants.mokrynos_admin_bot.data.model.Symptom;
import com.tylerpants.mokrynos_admin_bot.data.service.AnimalService;
import com.tylerpants.mokrynos_admin_bot.data.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotButtons {

    private final AnimalService animalService;
    private final SymptomService symptomService;

    @Autowired
    public BotButtons(AnimalService animalService, SymptomService symptomService) {
        this.animalService = animalService;
        this.symptomService = symptomService;
    }
    public ReplyKeyboardMarkup initMarkup() {
        KeyboardButton helpButton = new KeyboardButton(BotConstants.HELP_BUTTON);
        KeyboardButton addItemButton = new KeyboardButton(BotConstants.ADD_ITEM_BUTTON);
        KeyboardButton addAnimalButton = new KeyboardButton(BotConstants.ADD_ANIMAL_BUTTON);
        KeyboardButton addSymptomButton = new KeyboardButton(BotConstants.ADD_SYMPTOM_BUTTON);
        KeyboardButton deleteButton = new KeyboardButton(BotConstants.DELETE_BUTTON);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(addItemButton);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(addAnimalButton);
        row2.add(addSymptomButton);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(deleteButton);
        row3.add(helpButton);

        List<KeyboardRow> rows = List.of(row1, row2, row3);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }

    public InlineKeyboardMarkup animalTypeMarkup(int p) {
        List<Animal> list = animalService.findPageable(p);
        int pagesCount = (int) (double) (animalService.count() / 3);

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for(Animal a : list) {
            InlineKeyboardButton button = new InlineKeyboardButton(a.getName());
            button.setCallbackData("/animal "+a.getId());
            rowInline.add(button);

            if(rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        if(!rowInline.isEmpty()) {
            rowsInLine.add(rowInline);
        }

        List<InlineKeyboardButton> pagesRow = new ArrayList<>();

        if(p != 0) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton("←");
            prevButton.setCallbackData("/ago " + (p - 1));
            pagesRow.add(prevButton);
        }

        if(p < pagesCount) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton("→");
            nextButton.setCallbackData("/ago " + (p + 1));
            pagesRow.add(nextButton);
        }
        rowsInLine.add(pagesRow);

        List<InlineKeyboardButton> continueRow = new ArrayList<>();
        InlineKeyboardButton continueButton = new InlineKeyboardButton(BotConstants.CONTINUE_BUTTON);
        continueButton.setCallbackData("/continue");
        continueRow.add(continueButton);

        rowsInLine.add(continueRow);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    public InlineKeyboardMarkup symptomTypeMarkup(int p) {
        List<Symptom> list = symptomService.findPageable(p);
        int pagesCount = (int) (double) (symptomService.count() / 3);

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (Symptom a : list) {
            InlineKeyboardButton button = new InlineKeyboardButton(a.getName());
            button.setCallbackData("/symptom "+a.getId());
            rowInline.add(button);

            if(rowInline.size() == 3) {
                rowsInLine.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        if(!rowInline.isEmpty()) {
            rowsInLine.add(rowInline);
        }

        List<InlineKeyboardButton> pagesRow = new ArrayList<>();

        if(p != 0) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton("←");
            prevButton.setCallbackData("/sgo " + (p - 1));
            pagesRow.add(prevButton);
        }

        if (p < pagesCount) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton("→");
            nextButton.setCallbackData("/sgo " + (p + 1));
            pagesRow.add(nextButton);
        }
        rowsInLine.add(pagesRow);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
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
