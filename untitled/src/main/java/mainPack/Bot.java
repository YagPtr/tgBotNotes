package mainPack;
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
import java.util.List;
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


        //The L just turns the Integer into a Long
    }


    public void send() throws TelegramApiException {
        execute(BotMenu.getMenu(919660791L,"whatever"));
    }



    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){


        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }



    private void buttonTap(Long id, String queryId, String data, int msgId) throws TelegramApiException {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();



        if(data.equals("one")) {

//            newTxt.setText("MENU 2");
            List<SendMessage> messages=BotMenu.throwTasks(id);
            for (SendMessage sendMessage : messages) {
                execute(sendMessage);
            }
//            newKb.setReplyMarkup(keyboardM1);
        } else  {
            newTxt.setText("не кнопка меню");
//            newKb.setReplyMarkup(keyboardM1);
        }



        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
        execute(newTxt);
        execute(newKb);
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
        if (update.hasCallbackQuery()){
            System.out.println(update.getCallbackQuery().getData());
            try {
                buttonTap(919660791L,update.getCallbackQuery().getId(),update.getCallbackQuery().getData(),update.getCallbackQuery().getMessage().getMessageId());
            } catch (TelegramApiException e) {
                System.out.println(e);
            }
        }

        org.telegram.telegrambots.meta.api.objects.Message msg = update.getMessage();
        var query=update.getCallbackQuery();

        System.out.println(query.getFrom().getId()+" send "+query.getData()+"command to data "+query.getMessage().getText());
        System.out.println(update);


        var txt = msg.getText();



        if(msg.isCommand()) {
            System.out.println("msg is "+msg);
                if(msg.getText().equals("/start")) {
                    try {
                        execute(BotMenu.getMenu(919660791L,"whatever"));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            ;


            return;
        }


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