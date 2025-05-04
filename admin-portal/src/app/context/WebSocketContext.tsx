'use client';

import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { useWebSocket } from '@/hooks/useWebSocket';
import { useToast } from '@/components/ui/use-toast';

// Define event types
export type EventType = 'STATION_STATUS' | 'CHARGING_SESSION' | 'PAYMENT' | 'INVOICE';

// Define the shape of our WebSocket context
interface WebSocketContextType {
  connected: boolean;
  reconnecting: boolean;
  events: Record<EventType, any[]>;
  subscribeToTopic: (topic: string, callback: (message: any) => void) => () => void;
  sendMessage: (destination: string, body: any) => void;
}

// Create the context with default values
const WebSocketContext = createContext<WebSocketContextType>({
  connected: false,
  reconnecting: false,
  events: {
    STATION_STATUS: [],
    CHARGING_SESSION: [],
    PAYMENT: [],
    INVOICE: [],
  },
  subscribeToTopic: () => () => {},
  sendMessage: () => {},
});

// Maximum events to store per type
const MAX_EVENTS = 50;

// WebSocket provider component
export const WebSocketProvider = ({ children }: { children: ReactNode }) => {
  const { toast } = useToast();
  const [events, setEvents] = useState<Record<EventType, any[]>>({
    STATION_STATUS: [],
    CHARGING_SESSION: [],
    PAYMENT: [],
    INVOICE: [],
  });

  // Handle events from different topics
  const handleStationStatusEvent = (event: any) => {
    addEvent('STATION_STATUS', event);
  };

  const handleChargingSessionEvent = (event: any) => {
    addEvent('CHARGING_SESSION', event);
  };

  const handlePaymentEvent = (event: any) => {
    addEvent('PAYMENT', event);
    // Show toast notification for payment events
    toast({
      title: `Payment ${event.eventType}`,
      description: `Payment of ${event.currency} ${event.amount} was ${event.eventType.toLowerCase()}`,
      variant: event.eventType === 'COMPLETED' ? 'default' : 'destructive',
    });
  };

  const handleInvoiceEvent = (event: any) => {
    addEvent('INVOICE', event);
    // Show toast notification for invoice events
    toast({
      title: `Invoice ${event.eventType}`,
      description: `Invoice ${event.invoiceNumber} was ${event.eventType.toLowerCase()}`,
      variant: event.eventType === 'CREATED' ? 'default' : 'destructive',
    });
  };

  // Add event to the events state
  const addEvent = (type: EventType, event: any) => {
    setEvents((prevEvents) => {
      const newEvents = [...prevEvents[type], event];
      if (newEvents.length > MAX_EVENTS) {
        newEvents.shift(); // Remove oldest event if we exceed MAX_EVENTS
      }
      return {
        ...prevEvents,
        [type]: newEvents,
      };
    });
  };

  // WebSocket hook
  const { connected, reconnecting, subscribe, sendMessage } = useWebSocket({
    subscriptions: [
      { topic: '/topic/station-status', callback: handleStationStatusEvent },
      { topic: '/topic/charging-sessions', callback: handleChargingSessionEvent },
      { topic: '/topic/payments', callback: handlePaymentEvent },
      { topic: '/topic/invoices', callback: handleInvoiceEvent },
    ],
    onConnect: () => {
      console.log('WebSocket connected');
      toast({
        title: 'Real-time connection established',
        description: 'You will now receive live updates',
      });
    },
    onDisconnect: () => {
      console.log('WebSocket disconnected');
    },
    onError: (error) => {
      console.error('WebSocket error:', error);
      toast({
        title: 'Connection error',
        description: 'Failed to establish real-time connection',
        variant: 'destructive',
      });
    },
  });

  // Effect to show reconnecting toast
  useEffect(() => {
    if (reconnecting) {
      toast({
        title: 'Connection lost',
        description: 'Attempting to reconnect...',
        variant: 'destructive',
      });
    }
  }, [reconnecting, toast]);

  const contextValue: WebSocketContextType = {
    connected,
    reconnecting,
    events,
    subscribeToTopic: subscribe,
    sendMessage,
  };

  return (
    <WebSocketContext.Provider value={contextValue}>
      {children}
    </WebSocketContext.Provider>
  );
};

// Custom hook to use the WebSocket context
export const useWebSocketContext = () => useContext(WebSocketContext); 