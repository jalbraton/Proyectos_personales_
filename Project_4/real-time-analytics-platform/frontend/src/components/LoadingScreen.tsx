import React from 'react';
import { Box, CircularProgress, Typography, LinearProgress } from '@mui/material';
import { motion } from 'framer-motion';

/**
 * Loading Screen Component
 * ========================
 * 
 * Professional loading screen with animations
 * Shows loading progress for better UX
 */

interface LoadingScreenProps {
  message?: string;
  submessage?: string;
  progress?: number; // 0-100
}

export const LoadingScreen: React.FC<LoadingScreenProps> = ({
  message = 'Loading...',
  submessage = 'Please wait',
  progress
}) => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      justifyContent="center"
      alignItems="center"
      minHeight="100vh"
      bgcolor="background.default"
      gap={3}
    >
      {/* Animated Logo/Icon */}
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{
          duration: 0.5,
          repeat: Infinity,
          repeatType: 'reverse'
        }}
      >
        <CircularProgress size={80} thickness={4} />
      </motion.div>

      {/* Main Message */}
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.2 }}
      >
        <Typography variant="h5" fontWeight="600" textAlign="center">
          {message}
        </Typography>
      </motion.div>

      {/* Submessage */}
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.3 }}
      >
        <Typography variant="body2" color="text.secondary" textAlign="center">
          {submessage}
        </Typography>
      </motion.div>

      {/* Progress Bar (optional) */}
      {progress !== undefined && (
        <Box width="300px" mt={2}>
          <LinearProgress
            variant="determinate"
            value={progress}
            sx={{
              height: 6,
              borderRadius: 3,
              bgcolor: 'action.hover',
              '& .MuiLinearProgress-bar': {
                borderRadius: 3
              }
            }}
          />
          <Typography
            variant="caption"
            color="text.secondary"
            textAlign="center"
            display="block"
            mt={1}
          >
            {progress}% complete
          </Typography>
        </Box>
      )}

      {/* Loading dots animation */}
      <Box display="flex" gap={1}>
        {[0, 1, 2].map((i) => (
          <motion.div
            key={i}
            animate={{
              scale: [1, 1.5, 1],
              opacity: [0.3, 1, 0.3]
            }}
            transition={{
              duration: 1,
              repeat: Infinity,
              delay: i * 0.2
            }}
          >
            <Box
              width={8}
              height={8}
              borderRadius="50%"
              bgcolor="primary.main"
            />
          </motion.div>
        ))}
      </Box>
    </Box>
  );
};
