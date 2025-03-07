package mainPack;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import menu.BotMenu;
import constants.BotConstants;

public class Bot extends TelegramLongPollingBot {
    InlineKeyboardButton next = InlineKeyboardButton.builder()
            .text("Next").callbackData("next")
            .build();
    public InlineKeyboardMarkup keyboardM1;



    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();                  //We moved this line out of the register method, to access it later
        botsApi.registerBot(bot);
        bot.keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(bot.next,bot.next)).build();
        bot.sendText(919660791L, "Запуск бота");
        bot.send();
        bot.scheduleDailyMessage(5,0);
        bot.scheduleDailyMessage(23,0);
        bot.scheduleDailyMessage(14,0);


        //The L just turns the Integer into a Long
    }


    public void send() throws TelegramApiException {
        execute(BotMenu.getMenu(919660791L,"whatever"));
    }


    private void scheduleDailyMessage(int hour, int minute) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Вычисляем задержку до 10:00 утра
        LocalTime now = LocalTime.now();

        LocalTime targetTime = LocalTime.of(hour, minute);
        long initialDelay = Duration.between(now, targetTime).toMinutes();
        if (initialDelay < 0) {
            initialDelay += 24 * 60; // Если время уже прошло, ждем до следующего дня
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendDailyMessage();
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }, initialDelay, 24*60, TimeUnit.MINUTES);
    }

    private void sendDailyMessage() throws TelegramApiException {

        sendText(919660791L, "Запланированное");
        List<SendMessage> messages = BotMenu.throwTasks(919660791L,"919660791");
        if (messages.size()>0){
            for (SendMessage sendMessage : messages) {
                execute(sendMessage);
            }
        }else {
            sendText(919660791L, "Открытых задач нет");
        }
    }

//    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
//
//        SendMessage sm = SendMessage.builder().chatId(who.toString())
//                .parseMode("HTML").text(txt)
//                .replyMarkup(kb).build();
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }



    private void buttonTap(Long id, String queryId, String data, int msgId,String command, String author) throws TelegramApiException {

//        EditMessageText newTxt = EditMessageText.builder()
//                .chatId(id.toString())
//                .messageId(msgId).text("").build();
//
//        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
//                .chatId(id.toString()).messageId(msgId).build();

        try {
            System.out.println(data);
            Gson gson = new Gson();
            Map<String, String> jsonData = gson.fromJson(data, Map.class);

            // Достаём данные
            String action = jsonData.get("a"); // "buy"
            String productId = jsonData.get("id"); // "12345"
//            System.out.println(action);
//            System.out.println(productId);
            if (Objects.equals(action, "com") || Objects.equals(action, "del") || Objects.equals(action, "dod")){
                try {
                    // URL для удаления (пример с тестовым API)
                    URL url = new URL("http://127.0.0.1:8000/notes/"+productId);
                    System.out.println(url);
                    // Открываем соединение
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");

                    // Получаем код ответа
                    int responseCode = connection.getResponseCode();
                    if (responseCode==200){
                        sendText(Long.valueOf(author), "задача была удалена");
                    }
                    else{
                        sendText(Long.valueOf(author), "что-то пошло не так");

                    }
                    // Читаем ответ (если нужно)
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        try (BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(connection.getInputStream())
//                        )) {
//                            String line;
//                            StringBuilder response = new StringBuilder();
//                            while ((line = reader.readLine()) != null) {
//                                response.append(line);
//                            }
//                            System.out.println("Response: " + response.toString());
//                        }
//                    }

                    // Закрываем соединение
                    connection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }


        if(data.equals("CurrentTasks")) {

            List<SendMessage> messages = BotMenu.throwTasks(id,author);
            if (messages.size()>0){
            for (SendMessage sendMessage : messages) {
                execute(sendMessage);
            }
            }else {
                sendText(Long.valueOf(author), "Открытых задач нет");
            }
//            newKb.setReplyMarkup(keyboardM1);
//        } else  {
////            newTxt.setText("не кнопка меню");
////            newKb.setReplyMarkup(keyboardM1);
//        }


            AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                    .callbackQueryId(queryId).build();

        execute(close);
//        execute(newTxt);
//        execute(newKb);
        }else {
             // "buy"
//            System.out.println(command);
//            sendText(919660791L, command);
        }

    }




    @Override
    public String getBotUsername() {
        return BotConstants.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return BotConstants.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
//        System.out.println(update);
        org.telegram.telegrambots.meta.api.objects.Message msg = update.getMessage();
        if (update.hasCallbackQuery()){
//            System.out.println(update.getCallbackQuery().getData());
            try {
                String userId=new String();
                String action=new String();
                try {
                    userId=Long.toString(update.getCallbackQuery().getFrom().getId());
                    Gson gson = new Gson();

                    Map<String, String> data = gson.fromJson(update.getCallbackQuery().getData(), Map.class);

                    // Достаём данные
                    action = data.get("action");
                }
                catch (Exception e) {

                }
                buttonTap(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery().getId(),update.getCallbackQuery().getData(),update.getCallbackQuery().getMessage().getMessageId(),action,userId);
            } catch (TelegramApiException e) {
                System.out.println(e);
            }
            return;
        } else  if(msg.isCommand()) {
            System.out.println("msg is "+msg);
            if(msg.getText().equals("/start")) {
                try {
                    execute(BotMenu.getMenu(msg.getFrom().getId(),"Меню"));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }



            return;
        } else {
            System.out.println(update);
            URL url = null;
            try {
                url = new URL("http://127.0.0.1:8000/notes/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true); // Разрешаем запись тела

            // 3. JSON-тело запрос

            String note=update.getMessage().getText();
            String author=Long.toString(update.getMessage().getFrom().getId());
            String jsonInputString = "{\"Note\": \""+ note +"\", \"author\": \"" + author + "\"}";
            System.out.println(jsonInputString);
            // 4. Отправляем тело запроса
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int responseCode = 0;
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (responseCode == 200) {
                sendText(Long.valueOf(author), "задача была добавлена");
            }
            else{
                sendText(Long.valueOf(author), "что-то пошло не так");
            }

            connection.disconnect();
        }

//        var query=update.getCallbackQuery();
//
//
//
//        String encodedData = query.getData(); // Получаем callbackData
//
//        // Декодируем Base64 обратно в строку JSON
////        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
//        String jsonData = new String(encodedData);
//
//        // Парсим JSON обратно в Map
//        Gson gson = new Gson();
//        Map<String, String> data = gson.fromJson(jsonData, Map.class);
//
//        // Достаём данные
//        String action = data.get("action"); // "buy"
//        String productId = data.get("productId"); // "12345"
//        System.out.println(action);
//        System.out.println(productId);
//
//
//        System.out.println(query.getFrom().getId()+" send "+query.getData()+"command to data "+query.getMessage().getText());
//        System.out.println(update);
//
//
//        var txt = msg.getText();






    }


    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
}