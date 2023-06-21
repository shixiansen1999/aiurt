#!/bin/bash
mysqlip=$MYSQL_IP
mysqlname=$DB_NAME
mysqlport=$DB_PROT
redisip=$REDIS_IP
redisport=$REDIS_PORT
esurl=$ES_URL
mysqlpassword=$MYSQL_PASSWORD
uploadtype=$UPLOAD_TYPE
miniourl=$MINIO_URL
sqlserverip=$SQL_SERVER_IP
sqlservername=$SQL_SERVER_DB_NAME
sqlserverport=$SQL_SERVER_DB_PROT
sqlserverusername=$SQL_SERVER_USERNAME
sqlserverpassword=$SQL_SERVER_PASSWORD

if [ ! $mysqlip ]; then
    mysqlip="127.0.0.1"
fi
if [ ! $mysqlname ]; then
    mysqlname="aiurt-platform"
fi
if [ ! $mysqlport ]; then
    mysqlport="3306"
fi
if [ ! $redisip ]; then
    redisip="127.0.0.1"
fi
if [ ! $redisport ]; then
    redisport="6379"
fi
if [ ! $esurl ]; then
    esurl="http://127.0.0.1:9200"
fi
if [ ! $mysqlpassword ]; then
    mysqlpassword="1qaz@2wsx"
fi
if [ ! $uploadtype ]; then
    uploadtype="local"
fi
if [ ! $miniourl ]; then
    miniourl="http://127.0.0.1:9000"
fi

if [ ! $sqlserverip ]; then
    sqlserverip="192.168.1.27"
fi
if [ ! $sqlservername ]; then
    sqlservername="alarm"
fi
if [ ! $sqlserverport ]; then
    sqlserverport="1433"
fi
if [ ! $sqlserverusername ]; then
    sqlserverusername="sa"
fi
if [ ! $sqlserverpassword ]; then
    sqlserverpassword="1qaz@2wsx"
fi
applicationYml="/aiurt-platfrom/aiurt-module-system/config/application-docker.yml"
if [ ! -z "$miniourl" ] && [ ! -z "$uploadtype" ] && [ ! -z "$mysqlip" ] && [ ! -z "$mysqlname" ] && [ ! -z "$mysqlport" ]  && [ ! -z "$redisip" ]  && [ ! -z "$redisport" ] && [ ! -z "$esurl" ] && [ ! -z "$mysqlpassword" ]; then
  needReMySqlIpChar="\$MYSQL_IP"
  needReMySqlNameChar="\$DB_NAME"
  needReMySqlPortChar="\$DB_PROT"
  needReRedisIpChar="\$REDIS_IP"
  needReRedisPortChar="\$REDIS_PORT"
  needEsUrlChar="\$ES_URL"
  needMysqlPasswordChar="\$MYSQL_PASSWORD"
  needUploadTypeChar="\$UPLOAD_TYPE"
  needMinioUrlChar="\$MINIO_URL"
  needsqlserverip="\$SQL_SERVER_IP"
  needsqlservername="\$SQL_SERVER_DB_NAME"
  needsqlserverport="\$SQL_SERVER_DB_PROT"
  needsqlserverusername="\$SQL_SERVER_USERNAME"
  needsqlserverpassword="\$SQL_SERVER_PASSWORD"

  if [ -f "$applicationYml"  ]; then
    sed -i "s|$needReMySqlIpChar|$mysqlip|g" $applicationYml
    sed -i "s|$needReMySqlNameChar|$mysqlname|g" $applicationYml
    sed -i "s|$needReMySqlPortChar|$mysqlport|g" $applicationYml
    sed -i "s|$needReRedisIpChar|$redisip|g" $applicationYml
    sed -i "s|$needReRedisPortChar|$redisport|g" $applicationYml
    sed -i "s|$needEsUrlChar|$esurl|g" $applicationYml
    sed -i "s|$needMysqlPasswordChar|$mysqlpassword|g" $applicationYml
    sed -i "s|$needUploadTypeChar|$uploadtype|g" $applicationYml
    sed -i "s|$needMinioUrlChar|$miniourl|g" $applicationYml
    sed -i "s|$needsqlserverip|$sqlserverip|g" $applicationYml
    sed -i "s|$needsqlservername|$sqlservername|g" $applicationYml
    sed -i "s|$needsqlserverport|$sqlserverport|g" $applicationYml
    sed -i "s|$needsqlserverusername|$sqlserverusername|g" $applicationYml
    sed -i "s|$needsqlserverpassword|$sqlserverpassword|g" $applicationYml
  fi
fi
cd /aiurt-platfrom/aiurt-module-system
java  -jar -Duser.timezone=GMT+8 -Xms2048M  -Xmx2048M aiurt-module-system-3.2.0.jar
