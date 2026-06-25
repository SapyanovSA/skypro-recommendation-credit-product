package skypro.homework.recommendation.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import skypro.homework.recommendation.dto.RecommendationResponse;
import skypro.homework.recommendation.service.RecommendationService;
import skypro.homework.recommendation.service.UserService;

import java.util.List;

@Component
public class RecommendationBot implements SpringLongPollingBot, LongPollingUpdateConsumer {

    private final RecommendationService recommendationService;
    private final UserService userService;
    private final String botToken;
    private final TelegramClient telegramClient;

    public RecommendationBot(RecommendationService recommendationService,
                             UserService userService,
                             @Value("${telegram.bot.token}") String botToken) {
        this.recommendationService = recommendationService;
        this.userService = userService;
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText().trim();
                long chatId = update.getMessage().getChatId();

                if (messageText.startsWith("/start")) {
                    sendHelpMessage(chatId);
                } else if (messageText.startsWith("/recommend")) {
                    handleRecommendCommand(chatId, messageText);
                } else {
                    sendMessage(chatId, "Неизвестная команда. Используйте /recommend username для получения рекомендаций.");
                }
            }
        }
    }

    private void sendHelpMessage(long chatId) {
        String helpText = """
                Привет! Я бот рекомендательной системы банк-клиента.
                
                Доступные команды:
                /recommend <username> — получить список персональных предложений.
                """;
        sendMessage(chatId, helpText);
    }

    private void handleRecommendCommand(long chatId, String messageText) {
        String[] parts = messageText.split("\\s+", 2);

        if (parts.length < 2 || parts[1] == null || parts[1].trim().isEmpty()) {
            sendMessage(chatId, "Пожалуйста, укажите имя пользователя после команды.\nПример: /recommend ivan_ivanov");
            return;
        }

        String username = parts[1].trim();

        var userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            sendMessage(chatId, "Пользователь не найден");
            return;
        }

        var user = userOptional.get();

        try {
            List<RecommendationResponse> recommendations = recommendationService.getRecommendations(user.id());
            StringBuilder responseBuilder = new StringBuilder();

            responseBuilder.append("Здравствуйте ").append(user.firstName()).append(" ").append(user.lastName()).append("\n");
            responseBuilder.append("Новые продукты для вас:\n");

            if (recommendations.isEmpty()) {
                responseBuilder.append("Для вас пока нет новых предложений.");
            } else {
                for (RecommendationResponse rec : recommendations) {
                    responseBuilder.append("– ").append(rec.productName()).append(": ").append(rec.text()).append("\n");
                }
            }

            sendMessage(chatId, responseBuilder.toString().trim());

        } catch (Exception e) {
            sendMessage(chatId, "Пользователь не найден");
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}