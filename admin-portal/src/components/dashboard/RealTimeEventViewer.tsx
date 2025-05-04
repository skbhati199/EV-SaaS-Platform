'use client';

import React, { useState, useEffect } from 'react';
import { useWebSocketContext, EventType } from '@/app/context/WebSocketContext';

interface RealTimeEventViewerProps {
  className?: string;
}

export function RealTimeEventViewer({ className }: RealTimeEventViewerProps) {
  const { connected, events } = useWebSocketContext();
  const [activeTab, setActiveTab] = useState<EventType>('STATION_STATUS');
  const [showAllEvents, setShowAllEvents] = useState(false);
  
  const displayEvents = showAllEvents ? events[activeTab] : events[activeTab].slice(0, 5);
  
  return (
    <div className={`bg-white dark:bg-gray-900 rounded-lg shadow-md p-4 ${className}`}>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Real-time Events</h2>
        <div className="flex items-center gap-2">
          <div className={`w-2 h-2 rounded-full ${connected ? 'bg-green-500' : 'bg-red-500'}`}></div>
          <span className="text-sm text-gray-500 dark:text-gray-400">
            {connected ? 'Connected' : 'Disconnected'}
          </span>
        </div>
      </div>
      
      <div className="flex gap-1 mb-4 border-b border-gray-200 dark:border-gray-700">
        <button
          onClick={() => setActiveTab('STATION_STATUS')}
          className={`px-3 py-2 text-sm font-medium ${
            activeTab === 'STATION_STATUS'
              ? 'text-blue-600 border-b-2 border-blue-600 dark:text-blue-500 dark:border-blue-500'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          Station Status
        </button>
        <button
          onClick={() => setActiveTab('CHARGING_SESSION')}
          className={`px-3 py-2 text-sm font-medium ${
            activeTab === 'CHARGING_SESSION'
              ? 'text-blue-600 border-b-2 border-blue-600 dark:text-blue-500 dark:border-blue-500'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          Charging Sessions
        </button>
        <button
          onClick={() => setActiveTab('PAYMENT')}
          className={`px-3 py-2 text-sm font-medium ${
            activeTab === 'PAYMENT'
              ? 'text-blue-600 border-b-2 border-blue-600 dark:text-blue-500 dark:border-blue-500'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          Payments
        </button>
        <button
          onClick={() => setActiveTab('INVOICE')}
          className={`px-3 py-2 text-sm font-medium ${
            activeTab === 'INVOICE'
              ? 'text-blue-600 border-b-2 border-blue-600 dark:text-blue-500 dark:border-blue-500'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          Invoices
        </button>
      </div>
      
      {displayEvents.length > 0 ? (
        <div className="space-y-3">
          {displayEvents.map((event, index) => (
            <div 
              key={index} 
              className="p-3 bg-gray-50 dark:bg-gray-800 rounded-md border border-gray-200 dark:border-gray-700"
            >
              <div className="flex items-center justify-between mb-1">
                <span className="font-medium text-gray-900 dark:text-white">
                  {getEventTitle(event, activeTab)}
                </span>
                <span className="text-xs text-gray-500 dark:text-gray-400">
                  {formatTimestamp(event.timestamp)}
                </span>
              </div>
              {renderEventDetails(event, activeTab)}
            </div>
          ))}
          
          {events[activeTab].length > 5 && (
            <button
              onClick={() => setShowAllEvents(!showAllEvents)}
              className="w-full text-sm text-blue-600 dark:text-blue-500 hover:underline py-2"
            >
              {showAllEvents ? 'Show Less' : `Show All (${events[activeTab].length})`}
            </button>
          )}
        </div>
      ) : (
        <div className="text-center py-8 text-gray-500 dark:text-gray-400">
          No {activeTab.toLowerCase().replace('_', ' ')} events yet
        </div>
      )}
    </div>
  );
}

// Helper functions
function getEventTitle(event: any, type: EventType): string {
  switch (type) {
    case 'STATION_STATUS':
      return `Station ${event.stationId.substring(0, 8)} - ${event.newStatus}`;
    case 'CHARGING_SESSION':
      return `Session ${event.eventType} - ${event.sessionId.substring(0, 8)}`;
    case 'PAYMENT':
      return `Payment ${event.eventType} - ${event.currency} ${event.amount}`;
    case 'INVOICE':
      return `Invoice ${event.eventType} - ${event.invoiceNumber}`;
    default:
      return 'Event';
  }
}

function renderEventDetails(event: any, type: EventType) {
  switch (type) {
    case 'STATION_STATUS':
      return (
        <div className="text-sm text-gray-500 dark:text-gray-400">
          <p>Status changed from {event.previousStatus || 'UNKNOWN'} to {event.newStatus}</p>
          {event.reason && <p>Reason: {event.reason}</p>}
        </div>
      );
    case 'CHARGING_SESSION':
      return (
        <div className="text-sm text-gray-500 dark:text-gray-400">
          <p>Station: {event.stationId.substring(0, 8)}</p>
          <p>User: {event.userId.substring(0, 8)}</p>
          {event.energyDeliveredKwh && (
            <p>Energy: {parseFloat(event.energyDeliveredKwh).toFixed(2)} kWh</p>
          )}
        </div>
      );
    case 'PAYMENT':
      return (
        <div className="text-sm text-gray-500 dark:text-gray-400">
          <p>User: {event.userId.substring(0, 8)}</p>
          <p>Status: {event.status}</p>
          <p>Method: {event.paymentMethod}</p>
        </div>
      );
    case 'INVOICE':
      return (
        <div className="text-sm text-gray-500 dark:text-gray-400">
          <p>User: {event.userId.substring(0, 8)}</p>
          <p>Amount: {event.currency} {event.totalAmount}</p>
          <p>Status: {event.status}</p>
        </div>
      );
    default:
      return null;
  }
}

function formatTimestamp(timestamp: string): string {
  if (!timestamp) return '';
  
  try {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  } catch (error) {
    return timestamp;
  }
} 