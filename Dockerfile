FROM anapsix/alpine-java

ENV PORT=3000
ENV BOOTSTRAP_SERVER=localhost:9092

ADD target/app.jar /app.jar

CMD java -jar /app.jar -m unihook.core
