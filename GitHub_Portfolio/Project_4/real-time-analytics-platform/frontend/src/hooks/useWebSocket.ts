import { useState, useEffect, useCallback, useRef } from 'react';
import io, { Socket } from 'socket.io-client';

/**
 * WebSocket Hook
 * ==============
 * 
 * Advanced WebSocket management with:
 * - Auto-reconnection
 * - Event handlers
 * - Connection state
 * - Error handling
 * - JWT authentication
 */

interface UseWebSocketOptions {
  url?: string;
  autoConnect?: boolean;
  reconnection?: boolean;
  reconnectionAttempts?: number;
  reconnectionDelay?: number;
}

interface UseWebSocketReturn {
  socket: Socket | null;
  connected: boolean;
  connecting: boolean;
  error: Error | null;
  emit: (event: string, data?: any) => void;
  on: (event: string, handler: (...args: any[]) => void) => void;
  off: (event: string, handler: (...args: any[]) => void) => void;
  connect: () => void;
  disconnect: () => void;
}

export const useWebSocket = (options: UseWebSocketOptions = {}): UseWebSocketReturn => {
  const {
    url = process.env.REACT_APP_WEBSOCKET_URL || 'http://localhost:8004',
    autoConnect = true,
    reconnection = true,
    reconnectionAttempts = 5,
    reconnectionDelay = 1000
  } = options;

  const [socket, setSocket] = useState<Socket | null>(null);
  const [connected, setConnected] = useState(false);
  const [connecting, setConnecting] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  
  const socketRef = useRef<Socket | null>(null);

  // ==================== Connection Management ====================

  const connect = useCallback(() => {
    if (socketRef.current?.connected) {
      console.warn('WebSocket already connected');
      return;
    }

    setConnecting(true);
    setError(null);

    // Get authentication token
    const token = localStorage.getItem('accessToken');
    
    if (!token) {
      const authError = new Error('No authentication token found');
      setError(authError);
      setConnecting(false);
      return;
    }

    // Create socket instance
    const newSocket = io(url, {
      auth: { token },
      transports: ['websocket', 'polling'],
      reconnection,
      reconnectionAttempts,
      reconnectionDelay,
      timeout: 10000
    });

    // Connection event handlers
    newSocket.on('connect', () => {
      console.log('WebSocket connected:', newSocket.id);
      setConnected(true);
      setConnecting(false);
      setError(null);
    });

    newSocket.on('disconnect', (reason: string) => {
      console.log('WebSocket disconnected:', reason);
      setConnected(false);
      
      if (reason === 'io server disconnect') {
        // Server disconnected, try to reconnect manually
        newSocket.connect();
      }
    });

    newSocket.on('connect_error', (err: Error) => {
      console.error('WebSocket connection error:', err.message);
      setError(err);
      setConnecting(false);
    });

    newSocket.on('reconnect', (attemptNumber: number) => {
      console.log('WebSocket reconnected after', attemptNumber, 'attempts');
      setConnected(true);
      setError(null);
    });

    newSocket.on('reconnect_attempt', (attemptNumber: number) => {
      console.log('WebSocket reconnection attempt:', attemptNumber);
      setConnecting(true);
    });

    newSocket.on('reconnect_error', (err: Error) => {
      console.error('WebSocket reconnection error:', err.message);
      setError(err);
    });

    newSocket.on('reconnect_failed', () => {
      console.error('WebSocket reconnection failed');
      setError(new Error('Failed to reconnect after maximum attempts'));
      setConnecting(false);
    });

    socketRef.current = newSocket;
    setSocket(newSocket);
  }, [url, reconnection, reconnectionAttempts, reconnectionDelay]);

  const disconnect = useCallback(() => {
    if (socketRef.current) {
      console.log('Disconnecting WebSocket');
      socketRef.current.disconnect();
      socketRef.current = null;
      setSocket(null);
      setConnected(false);
    }
  }, []);

  // ==================== Event Management ====================

  const emit = useCallback((event: string, data?: any) => {
    if (!socketRef.current?.connected) {
      console.warn('WebSocket not connected, cannot emit event:', event);
      return;
    }

    socketRef.current.emit(event, data);
  }, []);

  const on = useCallback((event: string, handler: (...args: any[]) => void) => {
    if (!socketRef.current) {
      console.warn('WebSocket not initialized, cannot register handler for:', event);
      return;
    }

    socketRef.current.on(event, handler);
  }, []);

  const off = useCallback((event: string, handler: (...args: any[]) => void) => {
    if (!socketRef.current) {
      return;
    }

    socketRef.current.off(event, handler);
  }, []);

  // ==================== Lifecycle ====================

  useEffect(() => {
    if (autoConnect) {
      connect();
    }

    return () => {
      disconnect();
    };
  }, [autoConnect, connect, disconnect]);

  return {
    socket,
    connected,
    connecting,
    error,
    emit,
    on,
    off,
    connect,
    disconnect
  };
};
