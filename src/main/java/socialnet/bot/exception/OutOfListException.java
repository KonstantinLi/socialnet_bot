package socialnet.bot.exception;

public class OutOfListException extends RuntimeException {
    public OutOfListException() {
        super("Список завершен");
    }
}
