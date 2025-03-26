package menu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class BotMenu {
   public static SendMessage getMenu(Long who, String txt){
       //заменитель бд
       List<InlineKeyboardButton> notesEx= new ArrayList<>();

       notesEx.add(InlineKeyboardButton.builder()
               .text("текущие задачи").callbackData("CurrentTasks")
               .build());
       notesEx.add(InlineKeyboardButton.builder()
               .text("добавить задачу").callbackData("AddTask")
               .build());
       notesEx.add(InlineKeyboardButton.builder()
               .text("закрытые задачи").callbackData("ClosedTasks")
               .build());


//       InlineKeyboardButton[] array;
       InlineKeyboardMarkup keyboardM1 = InlineKeyboardMarkup.builder()
               .keyboardRow(notesEx).build();
       SendMessage sm = SendMessage.builder().chatId(who.toString())
               .parseMode("HTML").text(txt)
               .replyMarkup(keyboardM1).build();


       return sm;
   }


    public static List<SendMessage> throwTasksDeleted(long id,String author){
        List<SendMessage> notesEx= new ArrayList<>();

//       List<InlineKeyboardButton> notesCommands= new ArrayList<>();

        System.out.println("author is "+author+"id is "+id);

        try {
            System.out.println(author);
            // 1. URL для запроса (пример с тестовым API)
            String forURL="http://127.0.0.1:8000/notes/completed/"+author;
            System.out.println(forURL);
            URL url = new URL(forURL);

            // 2. Открываем соединение
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // 3. Получаем код ответа
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 4. Читаем ответ
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                )) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("JSON Response: " + response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootArray = mapper.readTree(response.toString());

                    // 2. Создаем список для результата
                    List<Map<String, String>> result = new ArrayList<>();

                    // 3. Обрабатываем каждый элемент массива
                    for (JsonNode node : rootArray) {
                        Map<String, String> item = new HashMap<>();
                        item.put("name", node.get("name").asText());
                        item.put("id", node.get("id").asText());
                        item.put("data", node.get("data").asText());
                        result.add(item);
                    }
                    System.out.println(result);
                    for (Map<String, String> item : result) {
                        List<InlineKeyboardButton> notesCommands= new ArrayList<>();
                        Map<String, String> dataForMessage = new HashMap<>();
                        dataForMessage.put("id", item.get("id"));

                        Gson gson = new Gson();
//                        Map<String, String> data = new HashMap<>();
//                        data.put("action", "taskCompleted");

                        dataForMessage.put("a", "com");
                        String jsonData = gson.toJson(dataForMessage);

                        notesCommands.add(InlineKeyboardButton.builder()
                                .text("Выполнил").callbackData(jsonData)
                                .build());
                        dataForMessage.put("a", "del");
                        jsonData = gson.toJson(dataForMessage);
                        notesCommands.add(InlineKeyboardButton.builder()
                                .text("Удалить").callbackData(jsonData)
                                .build());
                        dataForMessage.put("a", "dod");
                        jsonData = gson.toJson(dataForMessage);
                        notesCommands.add(      InlineKeyboardButton.builder()
                                .text("Пропущена").callbackData(jsonData)
                                .build());

                        InlineKeyboardMarkup notesManage = InlineKeyboardMarkup.builder()
                                .keyboardRow(notesCommands).build();

                        notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
                                .parseMode("HTML").text("Задача : "+item.get("name")+"&#10;&#13;"+item.get("data")).replyMarkup(notesManage)
                                .build());

//                        System.out.println(SendMessage.builder().chatId(String.valueOf(id))
//                                .parseMode("HTML").text("item.get()").replyMarkup(notesManage)
//                                .build());

                    }
                }
            } else {
                System.out.println("Error: " + responseCode);
            }

            // 5. Закрываем соединение
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Преобразуем Map в JSON






        return notesEx;
    }



   public static List<SendMessage> throwTasks(long id,String author){
       List<SendMessage> notesEx= new ArrayList<>();

//       List<InlineKeyboardButton> notesCommands= new ArrayList<>();

       System.out.println("author is "+author+"id is "+id);

       try {
           System.out.println(author);
           // 1. URL для запроса (пример с тестовым API)
           String forURL="http://127.0.0.1:8000/notes/"+String.valueOf(author);
           System.out.println(forURL);
           URL url = new URL(forURL);

           // 2. Открываем соединение
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setRequestMethod("GET");
           connection.setRequestProperty("Accept", "application/json");

           // 3. Получаем код ответа
           int responseCode = connection.getResponseCode();
           System.out.println("Response Code: " + responseCode);

           // 4. Читаем ответ
           if (responseCode == HttpURLConnection.HTTP_OK) {
               try (BufferedReader reader = new BufferedReader(
                       new InputStreamReader(connection.getInputStream())
               )) {
                   String line;
                   StringBuilder response = new StringBuilder();
                   while ((line = reader.readLine()) != null) {
                       response.append(line);
                   }
                   System.out.println("JSON Response: " + response.toString());
                   ObjectMapper mapper = new ObjectMapper();
                   JsonNode rootArray = mapper.readTree(response.toString());

                   // 2. Создаем список для результата
                   List<Map<String, String>> result = new ArrayList<>();

                   // 3. Обрабатываем каждый элемент массива
                   for (JsonNode node : rootArray) {
                       Map<String, String> item = new HashMap<>();
                       item.put("name", node.get("name").asText());
                       item.put("id", node.get("id").asText());
                       item.put("data", node.get("data").asText());
                       result.add(item);
                   }
                   System.out.println(result);
                    for (Map<String, String> item : result) {
                        List<InlineKeyboardButton> notesCommands= new ArrayList<>();
                        Map<String, String> dataForMessage = new HashMap<>();
                        dataForMessage.put("id", item.get("id"));

                        Gson gson = new Gson();
//                        Map<String, String> data = new HashMap<>();
//                        data.put("action", "taskCompleted");

                        dataForMessage.put("a", "com");
                        String jsonData = gson.toJson(dataForMessage);

                        notesCommands.add(InlineKeyboardButton.builder()
                                .text("Выполнил").callbackData(jsonData)
                                .build());
                        dataForMessage.put("a", "del");
                        jsonData = gson.toJson(dataForMessage);
                        notesCommands.add(InlineKeyboardButton.builder()
                                .text("Удалить").callbackData(jsonData)
                                .build());
                        dataForMessage.put("a", "dod");
                        jsonData = gson.toJson(dataForMessage);
                        notesCommands.add(      InlineKeyboardButton.builder()
                                .text("Пропущена").callbackData(jsonData)
                                .build());

                        InlineKeyboardMarkup notesManage = InlineKeyboardMarkup.builder()
                                .keyboardRow(notesCommands).build();

                        notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
                                .parseMode("HTML").text("Задача : "+item.get("name")+"&#10;&#13;"+item.get("data")).replyMarkup(notesManage)
                                .build());

//                        System.out.println(SendMessage.builder().chatId(String.valueOf(id))
//                                .parseMode("HTML").text("item.get()").replyMarkup(notesManage)
//                                .build());

                    }
               }
           } else {
               System.out.println("Error: " + responseCode);
           }

           // 5. Закрываем соединение
           connection.disconnect();

       } catch (IOException e) {
           e.printStackTrace();
       }
       // Преобразуем Map в JSON






       return notesEx;
   }
}
