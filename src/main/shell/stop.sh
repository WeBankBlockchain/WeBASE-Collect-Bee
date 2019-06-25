#!/usr/bin/env bash

ps -ef|grep supervisord|grep webasebee| awk '{print $2}'|xargs kill -9
ps -ef|grep WeBASE-Collect-Bee|grep -v grep| awk '{print $2}'|xargs kill -9
