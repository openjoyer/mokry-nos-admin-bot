package com.tylerpants.mokrynos_admin_bot.telegram;

import lombok.Getter;

import java.util.List;

public class BotConstants {

    @Getter
    private static final List<String> commands = List.of(
            "/start",
            "/help",
            "/animal",
            "/symptom",
            "/item",
            "/delete"
    );

    public static final String START_BUTTON = "Старт";
    public static final String UNKNOWN_COMMAND = "Я не понимаю :( Пожалуйста, воспользуйтесь командами из списка /help";
    public static final String EXIT_BUTTON = "Вернуться в меню";
    public static final String CONTINUE_BUTTON = "Продолжить";
    public static final String DELETE_BUTTON = "❌ Удалить из базы";
    public static final String HELP_BUTTON = "\uD83D\uDCA1 Помощь";
    public static final String ADD_ITEM_BUTTON = "Добавить препарат";
    public static final String ADD_ANIMAL_BUTTON = "Добавить вид питомца";
    public static final String ADD_SYMPTOM_BUTTON = "Добавить тип симптомов";
    public static final String HELP_ADVICE = "⚡ Команды бота";
    public static final String ANIMAL_ADVICE = "Добавление животного";
    public static final String ENTER_ANIMAL_NAME = "Введите название животного\n(Во множественном числе)";
    public static final String SYMPTOM_ADVICE = "Добавление симптома";
    public static final String ENTER_SYMPTOM_NAME = "Введите название симптома";
    public static final String ITEM_ADVICE = "Добавление препарата";

    public static final String ITEM_NAME = "Введите название препарата";
    public static final String ITEM_DESCRIPTION = "Введите описание товара";
    public static final String ITEM_ANIMALS = "Выберите, для каких животных этот препарат";
    public static final String ITEM_SYMPTOMS = "Введите, при каких симптомах нужен этот препарат";
    public static final String ITEM_LINK = "Добавьте ссылку на этот товар в каталоге";

    public static final String ADDED = "Сохранено!";
    public static final String HELP_TEXT = """
            /start - Запустить бота
            /help - Информация о боте и его командах
            /animal - Добавление нового вида животного для поиска препаратов
            /symptom - Добавление нового вида симптомов у животного
            /item - Добавление препарата в базу данных""";
    public static final String START_TEXT = """
            Админ Бот, стартовый текст...
            """;

}
