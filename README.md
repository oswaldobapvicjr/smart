# smart [![Build Status](https://travis-ci.org/oswaldobapvicjr/smart.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/smart)

A lightweight runtime engine for Java agents.

---

## Summary of features

- Multi-threaded runtime environment for daemon services written in Java
- Supports periodic taks on top of Java Scheduled Executor Service
- CLI-based management console with comprehensive usability features, including autocompletion
- Start, stop and reset agents independently, reducing downtime
- Customizable shell
- Standalone: runs on top of JVM, requiring no application server or additional frameworks

### Timer agents

![Timer agents state machine](resources/state_chart_timer_agents.svg)


### Daemon agents

![Daemon agents state machine](resources/state_chart_daemon_agents.svg)
