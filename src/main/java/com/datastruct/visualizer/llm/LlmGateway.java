package com.datastruct.visualizer.llm;

import java.io.IOException;
import java.util.List;

/**
 * Unified abstraction for invoking a Large Language Model.
 */
public interface LlmGateway {

    /**
     * Sends a chat completion request and returns the assistant's reply as plain text (non-stream).
     *
     * @param messages full chat history
     * @return assistant content
     */
    String chat(List<ChatMessage> messages) throws IOException;
}

