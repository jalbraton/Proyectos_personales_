import React, { useEffect, useState } from 'react';
import { Box, Avatar, Tooltip, Chip, Stack } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';

/**
 * Live Cursors Component
 * ======================
 * 
 * Real-time collaborative cursor tracking
 * Shows cursors of all active collaborators
 */

interface Collaborator {
  userId: string;
  username: string;
  email: string;
  cursor?: {
    x: number;
    y: number;
    pageX: number;
    pageY: number;
  };
  status: 'active' | 'idle' | 'away';
}

interface LiveCursorsProps {
  collaborators: Collaborator[];
}

const CURSOR_COLORS = [
  '#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A',
  '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2'
];

export const LiveCursors: React.FC<LiveCursorsProps> = ({ collaborators }: LiveCursorsProps) => {
  const [cursors, setCursors] = useState<Map<string, Collaborator>>(new Map());

  useEffect(() => {
    const newCursors = new Map<string, Collaborator>();
    
    collaborators.forEach((collab: Collaborator, index: number) => {
      if (collab.cursor) {
        newCursors.set(collab.userId, {
          ...collab,
          color: CURSOR_COLORS[index % CURSOR_COLORS.length]
        } as any);
      }
    });

    setCursors(newCursors);
  }, [collaborators]);

  return (
    <Box
      sx={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        pointerEvents: 'none',
        zIndex: 9999
      }}
    >
      <AnimatePresence>
        {Array.from(cursors.values()).map((cursor) => (
          <motion.div
            key={cursor.userId}
            initial={{ opacity: 0, scale: 0 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0 }}
            transition={{ duration: 0.2 }}
            style={{
              position: 'absolute',
              left: cursor.cursor!.x,
              top: cursor.cursor!.y,
              transform: 'translate(-50%, -50%)',
              pointerEvents: 'none'
            }}
          >
            {/* Cursor SVG */}
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill={(cursor as any).color}
              style={{ filter: 'drop-shadow(0 2px 4px rgba(0,0,0,0.3))' }}
            >
              <path d="M3 3L10.07 19.97L12.58 12.58L19.97 10.07L3 3Z" />
            </svg>

            {/* Username label */}
            <Box
              sx={{
                position: 'absolute',
                left: 24,
                top: 0,
                bgcolor: (cursor as any).color,
                color: 'white',
                px: 1,
                py: 0.5,
                borderRadius: 1,
                fontSize: '0.75rem',
                fontWeight: 'bold',
                whiteSpace: 'nowrap',
                boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
              }}
            >
              {cursor.username}
            </Box>
          </motion.div>
        ))}
      </AnimatePresence>
    </Box>
  );
};

/**
 * User Presence Component
 * =======================
 * 
 * Shows avatars of active collaborators
 */

interface UserPresenceProps {
  collaborators: Collaborator[];
}

export const UserPresence: React.FC<UserPresenceProps> = ({ collaborators }: UserPresenceProps) => {
  return (
    <Stack direction="row" spacing={1} alignItems="center">
      <Chip
        label={`${collaborators.length} online`}
        size="small"
        color="primary"
        variant="outlined"
      />
      
      <Stack direction="row" spacing={-1}>
        {collaborators.slice(0, 5).map((collab: Collaborator, index: number) => (
          <Tooltip
            key={collab.userId}
            title={`${collab.username} (${collab.status})`}
            arrow
          >
            <Avatar
              sx={{
                width: 32,
                height: 32,
                border: '2px solid white',
                bgcolor: CURSOR_COLORS[index % CURSOR_COLORS.length],
                fontSize: '0.875rem',
                zIndex: collaborators.length - index
              }}
            >
              {collab.username.charAt(0).toUpperCase()}
            </Avatar>
          </Tooltip>
        ))}
        
        {collaborators.length > 5 && (
          <Avatar
            sx={{
              width: 32,
              height: 32,
              border: '2px solid white',
              bgcolor: 'grey.500',
              fontSize: '0.75rem'
            }}
          >
            +{collaborators.length - 5}
          </Avatar>
        )}
      </Stack>
    </Stack>
  );
};
