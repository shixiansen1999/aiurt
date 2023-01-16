FROM anapsix/alpine-java:8_server-jre_unlimited

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /aiurt-platfrom/config/jeecg

WORKDIR /aiurt-platfrom

EXPOSE 8080

ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/aiurt-module-system-3.2.0-docker.tar.gz ./

WORKDIR /aiurt-platfrom/aiurt-module-system/bin/
RUN chmod u+x /aiurt-platfrom/aiurt-module-system/bin/aiurt-platfrom-replace-env.sh
ENTRYPOINT ["/aiurt-platfrom/aiurt-module-system/bin/aiurt-platfrom-replace-env.sh"]
