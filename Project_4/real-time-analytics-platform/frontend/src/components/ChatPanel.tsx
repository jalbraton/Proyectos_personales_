import React, { useState, useEffect } from 'react';
import { Box, Paper, IconButton, TextField, Typography, Avatar, Chip } from '@mui/material';
import { Send, Close, Minimize } from '@mui/icons-material';
import { useWebSocket } from '../hooks/useWebSocket';
import { motion, AnimatePresence } from 'framer-motion';

interface Message {
  id: string;
  userId: string;
  username: string;
  message: string;
  type: 'text' | 'system';
  timestamp: number;
}

interface ChatPanelProps {
  roomId: string;
}

export const ChatPanel: React.FC<ChatPanelProps> = ({ roomId }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const { emit, on, off } = useWebSocket();

  useEffect(() => {
    const handleMessageReceived = (data: Message) => {
      setMessages(prev => [...prev, data]);
    };

    on('message:received', handleMessageReceived);

    return () => {
      off('message:received', handleMessageReceived);
    };
  }, [on, off]);

  const handleSendMessage = () => {
    if (!inputMessage.trim()) return;

    emit('message:send', {
      roomId,
      message: inputMessage,
      type: 'text'
    });

    setInputMessage('');
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <>
      {/* Chat Button */}
      {!isOpen && (
        <IconButton
          onClick={() => setIsOpen(true)}
          sx={{
            position: 'fixed',
            bottom: 24,
            right: 24,
            width: 56,
            height: 56,
            bgcolor: 'primary.main',
            color: 'white',
            '&:hover': { bgcolor: 'primary.dark' }
          }}
        >
          <Typography variant="h6">💬</Typography>
        </IconButton>
      )}

      {/* Chat Panel */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 100 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 100 }}
            style={{
              position: 'fixed',
              bottom: 24,
              right: 24,
              width: 360,
              height: isMinimized ? 56 : 480,
              zIndex: 1000
            }}
          >
            <Paper elevation={8} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              {/* Header */}
              <Box
                sx={{
                  p: 2,
                  bgcolor: 'primary.main',
                  color: 'white',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <Typography variant="h6">Team Chat</Typography>
                <Box>
                  <IconButton size="small" onClick={() => setIsMinimized(!isMinimized)} sx={{ color: 'white' }}>
                    <Minimize />
                  </IconButton>
                  <IconButton size="small" onClick={() => setIsOpen(false)} sx={{ color: 'white' }}>
                    <Close />
                  </IconButton>
                </Box>
              </Box>

              {!isMinimized && (
                <>
                  {/* Messages */}
                  <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
                    {messages.map((msg) => (
                      <Box
                        key={msg.id}
                        sx={{
                          mb: 2,
                          display: 'flex',
                          alignItems: 'flex-start',
                          gap: 1
                        }}
                      >
                        <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main' }}>
                          {msg.username.charAt(0).toUpperCase()}
                        </Avatar>
                        <Box sx={{ flex: 1 }}>
                          <Typography variant="caption" fontWeight="bold">
                            {msg.username}
                          </Typography>
                          <Paper sx={{ p: 1, bgcolor: 'grey.100' }}>
                            <Typography variant="body2">{msg.message}</Typography>
                          </Paper>
                          <Typography variant="caption" color="text.secondary">
                            {new Date(msg.timestamp).toLocaleTimeString()}
                          </Typography>
                        </Box>
                      </Box>
                    ))}
                  </Box>

                  {/* Input */}
                  <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
                    <Box sx={{ display: 'flex', gap: 1 }}>
                      <TextField
                        fullWidth
                        size="small"
                        placeholder="Type a message..."
                        value={inputMessage}
                        onChange={(e) => setInputMessage(e.target.value)}
                        onKeyPress={handleKeyPress}
                      />
                      <IconButton color="primary" onClick={handleSendMessage}>
                        <Send />
                      </IconButton>
                    </Box>
                  </Box>
                </>
              )}
            </Paper>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};
