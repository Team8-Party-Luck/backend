# run_new_was.sh

#!/bin/bash

CURRENT_PORT=$(cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0
JASYPT_PASSWORD=$(echo $JASYPT_PASSWORD)

# EC2 서버에 미리 만들어둔 jasypt_password를 환경변수에 등록
source /etc/profile.d/codedeploy.sh

echo "JASYPT_PASSWORD is ${JASYPT_PASSWORD}"
echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "> No WAS is connected to nginx"
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')
echo "> TARGET_PID = ${TARGET_PID}"

if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill WAS running at ${TARGET_PORT}."
  sudo kill ${TARGET_PID}
fi

nohup java -jar -DJASYPT_PASSWORD=${JASYPT_PASSWORD} -Dserver.port=${TARGET_PORT} /home/ubuntu/eatsring-logging/build/libs/* > /home/ubuntu/eatsring-logging/nohup.out 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0