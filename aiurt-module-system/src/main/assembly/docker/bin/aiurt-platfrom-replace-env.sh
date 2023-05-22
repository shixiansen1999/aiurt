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
  fi
fi
cd /aiurt-platfrom/aiurt-module-system
java  -jar -Duser.timezone=GMT+8 -Xms2048M  -Xmx2048M aiurt-module-system-3.2.0.jar
