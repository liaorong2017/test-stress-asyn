#!/usr/bin/env bash

export LC_ALL=en_US.UTF-8
export JAVA_HOME=/usr/local/jdk1.8
umask 022

echo "Begin to startup:"
DIR=`dirname "$0"`
APP_HOME=`cd ${DIR}/..;pwd`
APP_LOG_HOME=${APP_HOME}/logs

result=$(ps axw |grep "${APP_HOME}/" | grep java | wc -l)
if [ ${result} -ge 1 ];then
    echo -ne "$APP_HOME process is exists"
    exit 1
fi

#内存相关
JAVA_OPTS="-server -Xms256m -Xmx1g -Xss228k"
#性能相关
JAVA_OPTS="${JAVA_OPTS} -XX:-UseBiasedLocking -XX:-UseCounterDecay -XX:AutoBoxCacheMax=20000"

#G1 GC
JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
#GC LOG
JAVA_OPTS="${JAVA_OPTS} -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${APP_LOG_HOME}/gc.log"
#异常日志
JAVA_OPTS="${JAVA_OPTS} -XX:ErrorFile=${APP_LOG_HOME}/hs_err_%p.log"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_LOG_HOME}"
JAVA_OPTS="${JAVA_OPTS} -XX:OnError=\"${JAVA_HOME}/bin/jstack %p > ${APP_LOG_HOME}/java_error.log\""
#其它参数
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8 -Dlog.home=${APP_LOG_HOME}"

#classpath将config放在前边，避免找classpath资源时先去找lib目录中的jar,如果lib中的jar有打包配置文件的话，就会先使用jar中的配置文件
CLASS_PATH="-classpath ${APP_HOME}/config:${APP_HOME}/lib/*"
JAVA_MAIN_CLASS="org.raje.test.main.HttpApp"

echo ${JAVA_OPTS}
echo "CLASSPATH:${CLASS_PATH}"

eval "${JAVA_HOME}/bin/java ${JAVA_OPTS} ${CLASS_PATH} ${JAVA_MAIN_CLASS}"
echo "server process started"