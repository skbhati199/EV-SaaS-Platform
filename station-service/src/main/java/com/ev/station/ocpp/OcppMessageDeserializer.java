package com.ev.station.ocpp;

import com.ev.station.ocpp.OcppMessage.MessageTypeId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class OcppMessageDeserializer extends JsonDeserializer<MessageTypeId> {
    
    @Override
    public MessageTypeId deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray() && node.size() > 0) {
            JsonNode typeNode = node.get(0);
            if (typeNode.canConvertToInt()) {
                int typeValue = typeNode.asInt();
                return MessageTypeId.fromValue(typeValue);
            }
        }
        throw new IOException("Unable to deserialize MessageTypeId from: " + node);
    }
} 