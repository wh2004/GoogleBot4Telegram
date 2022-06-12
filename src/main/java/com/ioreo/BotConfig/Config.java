package com.ioreo.BotConfig;

public class Config {
    public static String botToken = "";//botfather获取的bottoken
    public static String botName = "UniGoogleBot";//bot的username
    public static String ownerID = "";//拥有者ID
    public static String GoogleApiToken = "";//GoogleApiKey
    public static String[] GoogleApiCX = {"xxx","xxx","xxx"};//google搜索引擎ID
    
    public static String userManual = "GoogleBot使用指南：\n本机器人处于测试阶段，为防止滥用，采用白名单机制，如需申请使用，请联系 @UniDMBot \n本机器人属于内联机器人，无需使用命令进行操作，要使用本机器人，请在任意会话的输入栏输入本机器人的用户名：@"+botName+"并在后方输入一个空格，输入关键字后进行搜索，搜索结果将呈现在输入框上方。\n本机器人原作者： @UniOreoX \n由于本机器人使用免费Api，故每日限定搜索次数100次\n欢迎关注频道 @UniChannelX 获取更多内容";//使用指南
    public static String betaList = ownerID+",xxxx";//白名单，任意符号分割即可
    
}
