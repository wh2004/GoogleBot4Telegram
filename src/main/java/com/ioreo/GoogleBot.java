package com.ioreo;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery.AnswerInlineQueryBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.api.services.customsearch.v1.model.Search;
import com.ioreo.BotConfig.Config;

public class GoogleBot extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {
        handle(update);
    }

    @Override
    public String getBotUsername() {
        return Config.botName;
    }

    @Override
    public String getBotToken() {
        return Config.botToken;
    }
    
    private void handle(Update update)
    {
        //仅接受内联消息
        if(update.hasMessage()&&update.getMessage().isUserMessage())
        {
            Message comMessage = update.getMessage();
            SendMessage answerMessage = SendMessage.builder().chatId(comMessage.getFrom().getId().toString()).text(Config.userManual).build();
            try {
                execute(answerMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if(update.hasInlineQuery()&&Config.betaList.contains(update.getInlineQuery().getFrom().getId().toString())) //测试版筛选
        {
            InlineQuery inlineQuery = update.getInlineQuery();
            
            String query = inlineQuery.getQuery();

            if(!query.isEmpty())
                {
                    System.out.print("接收到关键字："+query);
    
                    CustomSearchAPI customSearchAPI = new CustomSearchAPI.Builder(new NetHttpTransport(), new GsonFactory(), null).setApplicationName("UniGoogleBot for Telegram").build();
                    Search resultSearch = null;
                    try {
                        CustomSearchAPI.Cse.List seacher = customSearchAPI.cse().list();
                        seacher.setQ(query);
                        seacher.setKey(Config.GoogleApiToken);
                        seacher.setCx(Config.GoogleApiCX);
                        seacher.setNum(8);
                        resultSearch = seacher.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<Result> resultList = resultSearch.getItems();
                    List<InlineQueryResult> resultAll = new ArrayList<>();
                    AnswerInlineQueryBuilder answerInlineQueryBuilder = AnswerInlineQuery.builder();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("有关“"+query+"”的全部搜索结果 - 内容来源于Google\n\n");
                    for(int i = 0; i<resultList.size(); i++)
                    {
                        Result resultItem = resultList.get(i);
                        String messgaeString = "<a href=\""+resultItem.getLink()+"\">"+resultItem.getTitle()+"</a>";
                        stringBuilder.append(messgaeString+"\n");
                        InlineKeyboardButton openNow = InlineKeyboardButton.builder().url(resultItem.getLink()).text("立即打开").build();
                        List<InlineKeyboardButton> buttons = new ArrayList<>();
                        buttons.add(openNow);
                        InlineQueryResult result = InlineQueryResultArticle.builder().id(String.valueOf(i+1)).inputMessageContent(
                            InputTextMessageContent.builder()
                            .parseMode(ParseMode.HTML)
                            .disableWebPagePreview(true)
                            .messageText("有关“"+query+"”的单个搜索结果\n\n"+messgaeString)
                            .build()
                        )
                        .title(resultItem.getTitle())
                        .description(resultItem.getSnippet())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(buttons).build())
                        .build();
                        resultAll.add(result);
                    }
                    
                    InlineKeyboardButton openNow = InlineKeyboardButton.builder().url("https://www.google.com/search?q="+query).text("全部完整搜索结果").build();
                    List<InlineKeyboardButton> buttons = new ArrayList<>();
                    buttons.add(openNow);
    
                    AnswerInlineQuery answerInlineQuery = answerInlineQueryBuilder.result(InlineQueryResultArticle.builder()
                    .id("0")
                    .title("发送搜索结果集合")
                    .description("共 "+resultAll.size()+" 条结果")
                    .thumbUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/480px-Google_%22G%22_Logo.svg.png")
                    .inputMessageContent(InputTextMessageContent.builder().parseMode(ParseMode.HTML).messageText(stringBuilder.toString()).disableWebPagePreview(true).build())
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboardRow(buttons).build())
                    .build()).results(resultAll).isPersonal(false).inlineQueryId(inlineQuery.getId()).build();
                    try {
                        answerInlineQuery.validate();
                        execute(answerInlineQuery);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        }

    }

}
