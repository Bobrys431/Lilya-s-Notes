package com.example.lilyasnotes.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.Activities.ConsoleActivity;
import com.example.lilyasnotes.Activities.SplashActivity;
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeIntoManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeNoteManager;
import com.example.lilyasnotes.DatabaseManagement.ThemesManager;

import java.util.Locale;

public class Console {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static String commandsHistory;
    private static String commandLine;

    public static void build(Context context) {
        Console.context = context;
    }

    static {
        commandsHistory = "";
        commandLine = "";
    }

    public static void setCommandLine(String text) {
        commandLine = text;
    }

    public static void executeCommandLine(int type, int id) {
        commandsHistory += "\n\n-------------------------------------------------------------------------------";
        StringBuilder sb = new StringBuilder(commandLine);
        int index = sb.indexOf("current");
        if (index != -1) {
            sb.delete(index, index + 7);
            sb.insert(index, id);
            sb.insert(index, " ");
            if (type == ConsoleActivity.THEMES_TYPE) {
                sb.insert(index, "themes");
            } else if (type == ConsoleActivity.THEME_TYPE) {
                sb.insert(index, "theme");
            } else if (type == ConsoleActivity.NOTE_TYPE) {
                sb.insert(index, "note");
            }
        }

        String[] arguments = sb.toString().split(" ");
        executeArgumentsSequence(arguments);
        commandLine = "";
    }

    private static void executeArgumentsSequence(String[] arguments) {
        try {
            switch (arguments[0]) {
                case "help":
                    onHelp();
                    break;
                case "clear":
                    onClear();
                    break;
                case "reset":
                    onReset();
                    break;
                case "print":
                    if (arguments[1].equals("all")) {
                        onPrintAll();
                    } else {
                        onPrintTable(arguments[1]);
                    }
                    break;
                case "add":
                    StringBuilder title = new StringBuilder();
                    for (int i = 3; i < arguments.length; i++) {
                        title.append(arguments[i]).append(" ");
                    }
                    title.deleteCharAt(title.lastIndexOf(" "));
                    onAdd(arguments[1], Integer.parseInt(arguments[2]), title.toString());
                    break;
                case "set":
                    StringBuilder value = new StringBuilder();
                    for (int i = 4; i < arguments.length; i++) {
                        value.append(arguments[i]).append(" ");
                    }
                    value.deleteCharAt(value.lastIndexOf(" "));
                    onSet(arguments[1], Integer.parseInt(arguments[2]), arguments[3], value.toString());
                    break;
                case "delete":
                    onDelete(arguments[1], Integer.parseInt(arguments[2]));
                    break;
                case "execute":
                    StringBuilder body = new StringBuilder();
                    for (int i = 2; i < arguments.length; i++) {
                        body.append(arguments[i]).append(" ");
                    }
                    body.deleteCharAt(body.lastIndexOf(" "));
                    onExecute(arguments[1], body.toString());
                    break;
                default:
                    StringBuilder line = new StringBuilder();
                    for (String argument : arguments) {
                        line.append(argument).append(" ");
                    }
                    line.deleteCharAt(line.lastIndexOf(" "));
                    addToCommandsHistory("Такої команди не існує: ”" + line + "”.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            StringBuilder line = new StringBuilder();
            for (String argument : arguments) {
                line.append(argument).append(" ");
            }
            line.deleteCharAt(line.lastIndexOf(" "));
            addToCommandsHistory("Недостатньо аргументів: ”" + line + "”.");
        } catch (Exception e) {
            StringBuilder line = new StringBuilder();
            for (String argument : arguments) {
                line.append(argument).append(" ");
            }
            line.deleteCharAt(line.lastIndexOf(" "));
            addToCommandsHistory("Невдалий запит: ”" + line + "”.");
            addToCommandsHistory(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void onHelp() {
        String instructions =
                        "help" + "\n" +
                        "Отримати список можливих команд" + "\n" +
                        "\n" +
                        "clear" + "\n" +
                        "Очистити всі данні" + "\n" +
                        "\n" +
                        "reset" + "\n" +
                        "Перезапуск програми" + "\n" +
                        "\n" +
                        "print all" + "\n" +
                        "Вивести на екран всю Базу Данних" + "\n" +
                        "\n" +
                        "print <table>" + "\n" +
                        "Вивести на екран таблицю в Базі Данних, де:" + "\n" +
                        "<table>\t-\tназва таблиці" + "\n" +
                        "\n" +
                        "add <type> <id> <title>" + "\n" +
                        "Створити новий об’єкт у вказаному, де:" + "\n" +
                        "<type>\t-\tтип нового об’єкту" + "\n" +
                        "<id>\t-\tідентифікатор теми ( id ), всередині якої буде розташований новий об’єкт" + "\n" +
                        "<title>\t-\tназва нового об’єкту" + "\n" +
                        "\n" +
                        "set <table> <id> <property> <value>" + "\n" +
                        "Встановити властивість об’єкту на вказану, де:" + "\n" +
                        "<table>\t-\tназва таблиці" + "\n" +
                        "<id>\t-\tідентифікатор запису ( id )" + "\n" +
                        "<property>\t-\tназва властивості" + "\n" +
                        "<value>\t-\tзначення, на яке необхідно встановити властивість запису" + "\n" +
                        "\n" +
                        "delete <type> <id>" + "\n" +
                        "Безповоротно видалити вказаний об’єкт, де:" + "\n" +
                        "<type>\t-\tтип об’єкту" + "\n" +
                        "<id>\t-\tідентифікатор об’єкту ( id )" + "\n" +
                        "\n" +
                        "execute <request>" + "\n" +
                        "Виконати SQL-запит до Бази Данних, де:" + "\n" +
                        "<request>\t-\tтіло запиту" + "\n" +
                        "\n" +
                        "Можливі типи об’єктів ( <type> ):" + "\n" +
                        "themes\t-\tголовне меню ( там неможна створювати нотатки, лише теми )" + "\n" +
                        "theme\t-\tтема" + "\n" +
                        "note\t-\tнотатка" + "\n" +
                        "\n" +
                        "Примітки:" + "\n" +
                        "Щоб підтвердити команду - введи ”*”" + "\n" +
                        "Ключове слово “current” здатне замінити комбінацію з <type> і <id> на об’єкт, в якому користувач розташовний прямо зараз" + "\n" +
                        "Користуючись консоллю рекомендується виконати перезапуск програми ( команда “reset” )" + "\n" +
                        "Коли користувача запитують id таблички ”themes” - він вводить ”-1”" + "\n";

        addToCommandsHistory(instructions);
    }

    private static void onClear() {
        ActionsSimulator.clearDB();
        addToCommandsHistory("База Данних була очищена");
    }

    private static void onReset() {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void onPrintAll() {
        StringBuilder sb = new StringBuilder();
        SQLiteDatabaseAdapter.printTable(null, context, sb);
        addToCommandsHistory(sb.toString());
        sb.setLength(0);
    }

    private static void onPrintTable(String tableName) {
        StringBuilder sb = new StringBuilder();
        SQLiteDatabaseAdapter.printTable(tableName, context, sb);
        if (sb.length() == 0) addToCommandsHistory("Такого імені не існує: ”" + tableName + "”.");
        else addToCommandsHistory(sb.toString());
        sb.setLength(0);
    }

    private static void onAdd(String type, int id, String title) {
        if (type.equals("theme")) {
            ThemeManager.addNewTheme(title);
            if (id == -1) {
                ThemesManager.addConnection(ThemeManager.getLastThemeId());
            } else {
                ThemeIntoManager.addConnection(id, ThemeManager.getLastThemeId());
            }
        } else if (type.equals("note")) {
            NoteManager.addNewNote(title, "");
            ThemeNoteManager.addConnection(id, NoteManager.getLastNoteId());
        } else {
            addToCommandsHistory("Неправильний тип данних: ”" + type + "”.");
        }

        String inTitle = id == -1 ? " themes " : " theme ( " + id + " )";
        addToCommandsHistory(String.format(
                Locale.getDefault(),
                "Створено нову %s з назвою: ”%s” - всередині %s",
                type, title, inTitle));
    }

    private static void onSet(String type, int id, String propertyName, String value) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);
        database.execSQL("UPDATE " + type +
                " SET " + propertyName + " = " + value +
                " WHERE " + type + "_id = " + id);

        addToCommandsHistory(String.format(
                Locale.getDefault(),
                "Значення %s в %s ( %d ) було встановлено на %s",
                propertyName, type, id, value));
    }

    private static void onDelete(String type, int id) {
        if (type.equals("theme")) {
            ThemeManager.deleteTheme(id);
        } else if (type.equals("note")) {
            NoteManager.deleteNote(id);
        }

        addToCommandsHistory(String.format(
                Locale.getDefault(),
                "Видалено %s ( %d )",
                type, id));
    }

    private static void onExecute(String sqlStart, String requestBody) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);
        if (sqlStart.equals("SELECT")) {
            Cursor cursor = database.rawQuery(sqlStart + " ?", new String[]{requestBody});
            if (cursor != null) {
                StringBuilder result = new StringBuilder();
                while (cursor.moveToNext()) {
                    for (int i = 0; i < requestBody.substring(0, requestBody.indexOf("FROM")).split(",").length; i++) {
                        result.append(cursor.getColumnName(i));
                        result.append("\t");
                        result.append(cursor.getString(i));
                    }
                    result.append("\n");
                }
                addToCommandsHistory("Виконано SQL-" + sqlStart + "-запит з відповіддю:\n" + result);
                cursor.close();
            } else {
                addToCommandsHistory("Виконано SQL-" + sqlStart + "-запит без відповіді.");
            }

        } else {
            database.execSQL(sqlStart + " ?", new Object[]{requestBody});
            addToCommandsHistory("Виконано SQL-" + sqlStart + "-запит.");
        }
    }

    private static void addToCommandsHistory(String executionResult) {
        commandsHistory += "\n\n" + commandLine + "\n\n" + executionResult;
    }

    public static String getCommandsHistory() {
        return commandsHistory;
    }
}
