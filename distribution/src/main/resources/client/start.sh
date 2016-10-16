#!/usr/bin/env bash
java -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=128m -Xmx512m -server -jar IMAPCloud-client.jar
