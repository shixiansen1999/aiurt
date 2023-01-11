FROM anapsix/alpine-java:8_server-jre_unlimited

# mysql
ENV MYSQL_IP=172.16.60.101
ENV DB_NAME=aiurt-platform
ENV DB_PROT=9088

#redis
ENV REDIS_IP=172.16.60.101
ENV REDIS_PORT=6379

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /aiurt-platfrom/config/jeecg

WORKDIR /aiurt-platfrom

EXPOSE 8080

ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/aiurt-module-system-3.2.0-docker.tar.gz ./

# configfile
ENV APP_CFG_FILE=/aiurt-platfrom/aiurt-module-system/config/application-docker.yml

# 属性值替换
RUN sed -i -e "s#\${db_ip}#${MYSQL_IP}#g" $APP_CFG_FILE
RUN sed -i -e "s#\${db_name}#${DB_NAME}#g" $APP_CFG_FILE
RUN sed -i -e "s#\${db_port}#${DB_PROT}#g" $APP_CFG_FILE
RUN sed -i -e "s#\${redis_ip}#${REDIS_IP}#g" $APP_CFG_FILE
RUN sed -i -e "s#\${redis_port}#${REDIS_PORT}#g" $APP_CFG_FILE

WORKDIR /aiurt-platfrom/aiurt-module-system
CMD sleep 60;java  -jar -Xms2048M  -Xmx2048M aiurt-module-system-3.2.0.jar
