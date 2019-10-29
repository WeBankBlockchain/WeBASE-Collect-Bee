#!/usr/bin/env bash

chmod +x *.jar
nohup java -jar `pwd`/*.jar >/dev/null 2>&1 &
echo "Press ctrl + C to break ..."
echo ""
echo "Try to start server, please wait."
sleep 3

num=0
while [[ ! -f "webasebee-core.log" ]]
do
  sleep 1
  ((num++))
  if [[ $num == 10 ]]; then
    echo "print log is timeout, please check the application."
    exit 0
  fi
  echo -e ".\c"
done

tail -f  webasebee-core.log