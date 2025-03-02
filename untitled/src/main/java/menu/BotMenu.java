package menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;

import java.util.List;


public class BotMenu {
   public static SendMessage getMenu(Long who, String txt){
       //заменитель бд
       List<InlineKeyboardButton> notesEx= new ArrayList<>();

       notesEx.add(InlineKeyboardButton.builder()
               .text("текущие задачи").callbackData("one")
               .build());
       notesEx.add(InlineKeyboardButton.builder()
               .text("добавить задачу").callbackData("two")
               .build());
       notesEx.add(InlineKeyboardButton.builder()
               .text("закрытые задачи").callbackData("three")
               .build());


//       InlineKeyboardButton[] array;
       InlineKeyboardMarkup keyboardM1 = InlineKeyboardMarkup.builder()
               .keyboardRow(notesEx).build();
       SendMessage sm = SendMessage.builder().chatId(who.toString())
               .parseMode("HTML").text(txt)
               .replyMarkup(keyboardM1).build();


       return sm;
   }

   public static List<SendMessage> throwTasks(long id){
       List<SendMessage> notesEx= new ArrayList<>();

       List<InlineKeyboardButton> notesCommands= new ArrayList<>();
       notesCommands.add(InlineKeyboardButton.builder()
               .text("отметить задачу как выполненную").callbackData("taskComplete")
               .build());
       notesCommands.add(InlineKeyboardButton.builder()
               .text("удалить задачу").callbackData("taskDeleted")
               .build());
       notesCommands.add(      InlineKeyboardButton.builder()
               .text("отметить задачу как невыполненную").callbackData("taskDodged")
               .build());

       InlineKeyboardMarkup notesManage = InlineKeyboardMarkup.builder()
               .keyboardRow(notesCommands).build();

       notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
               .parseMode("HTML").text("тут будет текущая задача1").replyMarkup(notesManage)
               .build());
       notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
               .parseMode("HTML").text("тут будет текущая задача2").replyMarkup(notesManage)
               .build());
       notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
               .parseMode("HTML").text("тут будет текущая задача3").replyMarkup(notesManage)
               .build());
       notesEx.add(SendMessage.builder().chatId(String.valueOf(id))
               .parseMode("HTML").text("тут будет текущая задача4").replyMarkup(notesManage)
               .build());
        return notesEx;
   }
}
