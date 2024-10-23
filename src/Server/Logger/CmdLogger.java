package Server.Logger;

public class CmdLogger implements Logger {
    @Override
    public void trace(String message) {
        System.out.println(message);
    }
}
