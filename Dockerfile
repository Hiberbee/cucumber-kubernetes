ARG jdkVersion=14
FROM gradle:jre$jdkVersion AS base
WORKDIR /app
COPY . .
RUN gradle --no-daemon build
ENTRYPOINT ["gradle"]
ENV FEATURES_DIR="" \
    CI=true
CMD ["--project-dir=/app", "--no-daemon", "--console=plain", "--warning-mode=none", "features"]
