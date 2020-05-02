![SMART logo](resources/smart_logo.svg)

[![Build Status](https://travis-ci.org/oswaldobapvicjr/smart.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/smart) [![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/smart/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/smart?branch=master)

A lightweight runtime engine for Java agents.

---

## Summary of features

- Multi-threaded runtime environment for scheduled services written in Java
- CLI-based console with autocompletion and customizable shell
- Agent management made independently, reducing overall downtime
- Standalone: runs on top of JVM, requiring no application server or additional frameworks
- Flexible agent configuration via annotations or XML

## Agents overview

Any class annotated with **@Agent**, and containing a main method annotated with **@AgentTask**, can be managed by the service, which looks up for such classes by scannning specific packages that are configurable via the `smart.properties` file.

An agent can be o type **Timer** or **Cron**, both designed to run particular tasks periodically in the JVM. Agents can be started automatically, at server startup, or manuaually, via the management console, which also allows manual execution, status query and shutdown.

![Timer agents state machine](resources/state_chart_timer_agents.svg)

### Timer agents

A Timer agent can be executed periodically, in a fixed run frequency, which must be in seconds, minutes, or hours.
For example, a Timer agent to be execured every 30 seconds could be configured in either of the following ways:

- **Using annotation**:

    ```java
    @Agent(type=AgentType.TIMER, frequency="30 seconds")
    public class DummyAgent
    {..}
    ```

- **Using XML**:

    ```xml
    <agent>
      <name>DummyAgent</name>
      <class>com.yourcompany.agents.DummyAgent</class>
      <type>timer</type>
      <frequency>30 seconds</frequency>
    </agent>
    ```

### Cron agents

A Cron agent can be executed at specific dates and times, comparable to the Cron Service available in Unix/Linux systems.
For example, the following agent is configured to execute every weekday at 2:00 AM:

- **Using annotation**:

    ```java
    @Agent(type=AgentType.CRON, frequency="0 2 * * MON-FRI")
    public class DummyAgent
    {..}
    ```

- **Using XML**:

    ```xml
    <agent>
      <name>DummyAgent</name>
      <class>com.yourcompany.agents.DummyAgent</class>
      <type>cron</type>
      <frequency>0 2 * * MON-FRI</frequency>
    </agent>
    ```
