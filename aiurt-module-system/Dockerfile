FROM anapsix/alpine-java:8_server-jre_unlimited

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /aiurt-platfrom/config/jeecg
RUN mkdir -p /data/wwwroot/support/test/
RUN mkdir -p /data/wwwroot/support/upload/
RUN mkdir -p /data/wwwroot/support/dist/

WORKDIR /aiurt-platfrom

EXPOSE 8080

COPY ./target/classes/fonts/chinese /usr/share/fonts/ttf-dejavu/chinese
ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/aiurt-module-system-3.2.0-docker.tar.gz ./

WORKDIR /aiurt-platfrom/aiurt-module-system/bin/
RUN chmod u+x /aiurt-platfrom/aiurt-module-system/bin/aiurt-platfrom-replace-env.sh
ENTRYPOINT ["/aiurt-platfrom/aiurt-module-system/bin/aiurt-platfrom-replace-env.sh"]
