package net.chat.dto;

public enum ChatMessageType {
    CREATE("CREATE"),
    INFO("INFO"),
    ENTER("ENTER"),
    TALK("TALK"),
    EXIT("EXIT");

    private final String type;
    ChatMessageType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
}
