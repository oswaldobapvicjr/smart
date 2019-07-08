package net.obvj.smart.console;

public class CommandOutput
{
    private String output;
    private boolean error = false;

    public CommandOutput(String output, boolean error)
    {
        this.output = output;
        this.error = error;
    }

    public String getOutput()
    {
        return output;
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public boolean isError()
    {
        return error;
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

}
