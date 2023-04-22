FROM node:20 as builder
WORKDIR /app

ADD ./ .
RUN yarn install

FROM nginx:1.23.4

COPY --from=builder /app/public /usr/share/nginx/html

HEALTHCHECK --start-period=10s --interval=1m --timeout=3s \
    CMD curl -f http://localhost/ || exit 1

EXPOSE 80