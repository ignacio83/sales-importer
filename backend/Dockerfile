FROM amazoncorretto:17 as builder
WORKDIR /app

ADD ./ .
RUN ./gradlew build

WORKDIR /app/build/libs
RUN jar -xf importer.jar

FROM amazoncorretto:17

USER 1000

ARG EXPLODED=/app/build/libs
COPY --from=builder ${EXPLODED}/BOOT-INF/lib /app/lib
COPY --from=builder ${EXPLODED}/META-INF /app/META-INF
COPY --from=builder ${EXPLODED}/BOOT-INF/classes /app

HEALTHCHECK --start-period=10s --interval=1m --timeout=3s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","com.afi.sales.importer.ApplicationKt"]