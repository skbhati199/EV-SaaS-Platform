import { useEffect, useRef, useState, useCallback } from 'react';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface WebSocketOptions {
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: (error: any) => void;
  autoReconnect?: boolean;
  reconnectDelay?: number;
  subscriptions?: {
    topic: string;
    callback: (message: any) => void;
  }[];
}

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const useWebSocket = (options: WebSocketOptions = {}) => {
  const {
    onConnect,
    onDisconnect,
    onError,
    autoReconnect = true,
    reconnectDelay = 5000,
    subscriptions = [],
  } = options;

  const [connected, setConnected] = useState(false);
  const [reconnecting, setReconnecting] = useState(false);
  const stompClient = useRef<Client | null>(null);
  const subscriptionRefs = useRef<StompSubscription[]>([]);

  // Initialize connection
  const connect = useCallback(() => {
    // Use secure WebSocket connection with dynamic protocol detection
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const baseUrl = API_URL.replace(/^https?:\/\//, ''); // Remove http:// or https://
    const wsUrl = `${protocol}//${baseUrl}/ws`;
    
    const socket = new SockJS(wsUrl);
    const client = new Client({
      webSocketFactory: () => socket,
      debug: function (str) {
        if (process.env.NODE_ENV === 'development') {
          console.log('STOMP: ' + str);
        }
      },
      reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // Set up callbacks
    client.onConnect = () => {
      setConnected(true);
      setReconnecting(false);
      
      // Subscribe to topics
      subscriptionRefs.current = subscriptions.map(({ topic, callback }) => {
        return client.subscribe(topic, (message) => {
          try {
            const body = JSON.parse(message.body);
            callback(body);
          } catch (error) {
            console.error('Error parsing message:', error);
          }
        });
      });
      
      // Custom callback
      if (onConnect) {
        onConnect();
      }
    };

    client.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      console.error('Additional details:', frame.body);
      if (onError) {
        onError(frame);
      }
    };

    client.onWebSocketClose = () => {
      setConnected(false);
      if (onDisconnect) {
        onDisconnect();
      }
      
      // Auto reconnect if enabled
      if (autoReconnect && !client.active) {
        setReconnecting(true);
        setTimeout(() => {
          if (stompClient.current === client) {
            connect();
          }
        }, reconnectDelay);
      }
    };

    // Activate the client
    client.activate();
    stompClient.current = client;
  }, [onConnect, onDisconnect, onError, autoReconnect, reconnectDelay, subscriptions]);

  // Disconnect function
  const disconnect = useCallback(() => {
    if (stompClient.current?.active) {
      // Unsubscribe from all topics
      subscriptionRefs.current.forEach((subscription) => {
        subscription.unsubscribe();
      });
      subscriptionRefs.current = [];
      
      // Disconnect client
      stompClient.current.deactivate();
      setConnected(false);
    }
  }, []);

  // Send a message to a topic
  const sendMessage = useCallback((destination: string, body: any) => {
    if (stompClient.current?.active) {
      stompClient.current.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.error('STOMP client is not connected');
    }
  }, []);

  // Subscribe to a topic
  const subscribe = useCallback((topic: string, callback: (message: any) => void) => {
    if (stompClient.current?.active) {
      const subscription = stompClient.current.subscribe(topic, (message) => {
        try {
          const body = JSON.parse(message.body);
          callback(body);
        } catch (error) {
          console.error('Error parsing message:', error);
        }
      });
      subscriptionRefs.current.push(subscription);
      return () => {
        subscription.unsubscribe();
        subscriptionRefs.current = subscriptionRefs.current.filter(
          (sub) => sub.id !== subscription.id
        );
      };
    }
    return () => {};
  }, []);

  // Initialize connection on mount and cleanup on unmount
  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    connected,
    reconnecting,
    sendMessage,
    subscribe,
    disconnect,
    connect,
  };
}; 