FROM rabbitmq:3.11.0-management

CMD ["rabbitmq-server"]

EXPOSE 5672 15672

VOLUME rabbitmq_data:/var/lib/rabbitmq

RESTART unless-stopped