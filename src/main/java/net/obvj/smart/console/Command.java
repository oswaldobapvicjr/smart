package net.obvj.smart.console;

public enum Command
{

    SHOW_AGENTS("show-agents"),
    SHOW_THREADS("show-threads"),
    START("start"),
    RUN("run"),
    STOP("stop"),
    STATUS("status"),
    RESET("reset"),
    DATE("date"),
    HELP("help"),
    EXIT("exit");

    private String string;

    private Command(String string)
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }
}
