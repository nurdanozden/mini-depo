package com.minidepo.ai;

import com.minidepo.config.AppConfig;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LangChain4j Agent yapılandırması.
 * OpenAI modelini ve Tool'ları birleştirerek InventoryAgent oluşturur.
 */
public class AgentConfiguration {

    private static final Logger log = LogManager.getLogger(AgentConfiguration.class);

    public static InventoryAgent createAgent() {
        AppConfig config = AppConfig.getInstance();

        String apiKey = config.get("openai.api.key", "");
        String model = config.get("openai.model", "gpt-4o-mini");
        double temperature = config.getDouble("openai.temperature", 0.7);

        if (apiKey.isBlank() || apiKey.equals("BURAYA_API_KEY_YAZIN")) {
            log.error("OpenAI API key yapılandırılmamış!");
            throw new RuntimeException(
                    "OpenAI API key bulunamadı! src/main/resources/application.properties dosyasında " +
                    "'openai.api.key' değerini ayarlayın.");
        }

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(temperature)
                .logRequests(false)
                .logResponses(false)
                .build();

        log.info("AI Agent yapılandırıldı → Model: {}, Temperature: {}", model, temperature);

        InventoryAgent agent = AiServices.builder(InventoryAgent.class)
                .chatLanguageModel(chatModel)
                .tools(new InventoryTools())
                .build();

        log.info("InventoryAgent hazır — Tool'lar yüklendi.");
        return agent;
    }
}

