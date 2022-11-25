#!/bin/bash
##JVM OPTIONS -Xms -Xmx config
JAVA_OPTIONS_MIN=-Xms2048M
JAVA_OPTIONS_MAX=-Xmx2048M

##spring boot 打包完成后的jar包名称
##spring boot project package jar name
APP_JAR_NAME=aiurt-module-system-3.2.0-test.jar

##应用名称
##application name
APP_NAME=platform

##系统home目录  linux系统默认为 /home/ywpt/bdyw
##application home dir
HyERP_HOME=/home/test/platform/aiurt-module-system


##查询应用进程pid命令
PID=$(ps aux | grep ${APP_JAR_NAME} | grep -v grep | awk '{print $2}' )

##spring boot 外部配置文件
##ALARM_CONFIG_FILE=`pwd`/alarmConfig.yaml


##检查进程是否被启动方法
function check_if_process_is_running {
 if [ "$PID" = "" ]; then
 return 1
 fi
 ps -p $PID | grep "java"
 return $?
}
case "$1" in
 status)
 if check_if_process_is_running
 then
 echo -e "\033[32m $APP_NAME is running \033[0m"
 else
 echo -e "\033[32m $APP_NAME not running \033[0m"
 fi
 ;;
 stop)
 if ! check_if_process_is_running
 then
 echo -e "\033[32m $APP_NAME already stopped \033[0m"
 exit 0
 fi
 kill -9 $PID
 echo -e "\033[32m Waiting for process to stop \033[0m"
 NOT_KILLED=1
 for i in {1..20}; do
 if check_if_process_is_running
 then
 echo -ne "\033[32m . \033[0m"
 sleep 1
 else
 NOT_KILLED=0
 fi
 done
 echo
 if [ $NOT_KILLED = 1 ]
 then
 echo -e "\033[32m Cannot kill process \033[0m"
 exit 1
 fi
 echo -e "\033[32m $APP_NAME already stopped \033[0m"
 ;;
 start)
 if [ "$PID" != "" ] && check_if_process_is_running
 then
 echo -e "\033[32m $APP_NAME already running \033[0m"
 exit 1
 fi
 echo "Jump dir $HyERP_HOME"
 cd $HyERP_HOME
 echo "Execute shell cmd"
 nohup java  -jar  $JAVA_OPTIONS_MIN $JAVA_OPTIONS_MAX "./"$APP_JAR_NAME  > "./logs/"$APP_NAME"_local.log" 2>"./logs/"$APP_NAME"_out".log &
 echo -ne "\033[32m Starting \033[0m"
 for i in {1..20}; do
 echo -ne "\033[32m.\033[0m"
 sleep 1
 done
 if check_if_process_is_running
 then
 echo -e "\033[32m $APP_NAME fail \033[0m"
 else
 echo -e "\033[32m $APP_NAME started \033[0m"
 fi
 ;;
 restart)
 $0 stop
 if [ $? = 1 ]
 then
 exit 1
 fi
 $0 start
 ;;
 *)
 echo "Usage: $0 {start|stop|restart|status}"
 exit 1
esac
exit 0
