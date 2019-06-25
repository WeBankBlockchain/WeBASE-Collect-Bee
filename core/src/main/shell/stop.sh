#!/usr/bin/env bash

ps -ef|grep supervisord|grep webasebee| awk '{print $2}'|xargs kill -9
ps -ef|grep webase-bee|grep -v grep| awk '{print $2}'|xargs kill -9