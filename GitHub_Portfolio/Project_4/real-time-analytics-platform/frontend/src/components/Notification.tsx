import React from 'react';
import { Snackbar, Alert, AlertTitle, IconButton } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';

/**
 * Notification Component
 * =====================
 * 
 * Toast notifications for user feedback
 * Supports success, error, warning, info types
 */

export type NotificationType = 'success' | 'error' | 'warning' | 'info';

interface NotificationProps {
  open: boolean;
  message: string;
  title?: string;
  type?: NotificationType;
  duration?: number;
  onClose: () => void;
}

export const Notification: React.FC<NotificationProps> = ({
  open,
  message,
  title,
  type = 'info',
  duration = 6000,
  onClose
}) => {
  return (
    <Snackbar
      open={open}
      autoHideDuration={duration}
      onClose={onClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
    >
      <Alert
        severity={type}
        variant="filled"
        onClose={onClose}
        action={
          <IconButton
            size="small"
            aria-label="close"
            color="inherit"
            onClick={onClose}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        sx={{ minWidth: 300 }}
      >
        {title && <AlertTitle>{title}</AlertTitle>}
        {message}
      </Alert>
    </Snackbar>
  );
};

/**
 * Hook for managing notifications
 */
export const useNotification = () => {
  const [notification, setNotification] = React.useState<{
    open: boolean;
    message: string;
    title?: string;
    type: NotificationType;
  }>({
    open: false,
    message: '',
    type: 'info'
  });

  const showNotification = (
    message: string,
    type: NotificationType = 'info',
    title?: string
  ) => {
    setNotification({
      open: true,
      message,
      title,
      type
    });
  };

  const showSuccess = (message: string, title?: string) => {
    showNotification(message, 'success', title);
  };

  const showError = (message: string, title?: string) => {
    showNotification(message, 'error', title);
  };

  const showWarning = (message: string, title?: string) => {
    showNotification(message, 'warning', title);
  };

  const showInfo = (message: string, title?: string) => {
    showNotification(message, 'info', title);
  };

  const hideNotification = () => {
    setNotification((prev) => ({ ...prev, open: false }));
  };

  return {
    notification,
    showNotification,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    hideNotification
  };
};
