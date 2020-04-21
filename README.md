![SMART logo](resources/smart_logo.svg)

[![Build Status](https://travis-ci.org/oswaldobapvicjr/smart.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/smart) [![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/smart/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/smart?branch=master)

A lightweight runtime engine for Java agents.

---

## Summary of features

- Multi-threaded runtime environment for background services written in Java
- Supports periodic tasks on top of Java Scheduled Executor Service
- CLI-based management console with comprehensive usability features, including autocompletion
- Start, stop and reset agents independently, reducing downtime
- Customizable shell
- Standalone: runs on top of JVM, requiring no application server or additional frameworks

### Timer agents

A timer agent is an object that, once started, runs a particular task periodically, given a configurable interval in seconds, minutes, or hours.

Timer agents can be started together with the container, or via management console.
The agent task can also be executed manually via management console, if required. The timer agent can also be stopped, if required, provided that it is not running at the moment.

![Timer agents state machine](resources/state_chart_timer_agents.svg)

This is a sample configuration of a timer agent that will execute every 30 seconds:

```xml
<agent>
  <name>DummyAgent</name>
  <type>timer</type>
  <class>net.obvj.smart.agents.dummy.DummyAgent</class>
  <frequency>30 seconds</frequency>
</agent>
```
