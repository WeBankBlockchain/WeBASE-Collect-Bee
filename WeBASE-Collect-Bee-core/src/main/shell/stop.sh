#!/usr/bin/env bash

ps -ef|grep supervisord|grep -i bee| awk '{print $2}'|xargs kill -9
ps -ef|grep -i `pwd`|grep -i webase-collect-bee|grep -v grep| awk '{print $2}'|xargs kill -9
echo "Stop succeed!"

