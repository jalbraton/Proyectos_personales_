import React, { useEffect, useState, useCallback } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import { useAuth } from '../hooks/useAuth';
import { Box, Grid, Paper, Typography, Button, Tooltip, Fade } from '@mui/material';
import { Refresh as RefreshIcon, CloudOff as CloudOffIcon } from '@mui/icons-material';
import { DashboardBuilder } from '../components/DashboardBuilder';
import { LiveCursors, UserPresence } from '../components/LiveCursors';
import { ChatPanel } from '../components/ChatPanel';
import { TimeSeriesChart } from '../components/charts/TimeSeriesChart';
import { HeatMap } from '../components/charts/HeatMap';
import { DataTable } from '../components/charts/DataTable';
import { LoadingScreen } from '../components/LoadingScreen';
import { Notification, useNotification } from '../components/Notification';

/**
 * Main Real-Time Dashboard Component
 * ===================================
 * 
 * Enterprise dashboard featuring:
 * - Real-time data visualization
 * - WebSocket collaboration
 * - Live cursors & presence
 * - CRDT-based document sync
 * - Multiple chart types
 * - Drag-and-drop layout
 */

interface DashboardData {
  metrics: {
    activeUsers: number;
    requestsPerSecond: number;
    averageLatency: number;
    errorRate: number;
  };
  timeSeries: Array<{
    timestamp: number;
    value: number;
    label: string;
  }>;
  heatmapData: number[][];
  tableData: Array<Record<string, any>>;
}

interface Collaborator {
  userId: string;
  username: string;
  email: string;
  cursor?: { x: number; y: number; pageX: number; pageY: number };
  status: 'active' | 'idle' | 'away';
}

export const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const { connected, emit, on, off, error: wsError } = useWebSocket();
  const { notification, showError, showSuccess, showInfo, hideNotification } = useNotification();
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [collaborators, setCollaborators] = useState<Collaborator[]>([]);
  const [roomId] = useState(`dashboard-${user?.id || 'default'}`);

  // ==================== Real-time Data Subscription ====================

  useEffect(() => {
    if (!connected) return;

    // Join collaboration room
    emit('room:join', {
      roomId,
      roomName: 'Main Dashboard',
      roomType: 'dashboard'
    });

    // Subscribe to data channels
    emit('data:subscribe', {
      channel: 'metrics',
      filters: { userId: user?.id }
    });

    emit('data:subscribe', {
      channel: 'analytics',
      filters: { dashboardId: 'main' }
    });

    // Handle room join confirmation
    const handleRoomJoined = (data: any) => {
      console.log('Joined room:', data);
      setCollaborators(data.users || []);
      setLoading(false);
      showSuccess('Connected to real-time dashboard', 'Welcome!');
    };

    // Handle user joined
    const handleUserJoined = (data: Collaborator) => {
      console.log('User joined:', data);
      setCollaborators((prev: Collaborator[]) => [...prev, data]);
      showInfo(`${data.username} joined the dashboard`);
    };

    // Handle user left
    const handleUserLeft = (data: { userId: string; username?: string }) => {
      console.log('User left:', data);
      setCollaborators((prev: Collaborator[]) => prev.filter((u: Collaborator) => u.userId !== data.userId));
      if (data.username) {
        showInfo(`${data.username} left the dashboard`);
      }
    };

    // Handle real-time data updates
    const handleDataUpdate = (data: any) => {
      console.log('Data update received:', data);
      
      if (data.channel === 'metrics') {
        setDashboardData((prev: DashboardData | null) => ({
          ...prev!,
          metrics: data.data
        }));
      } else if (data.channel === 'analytics') {
        setDashboardData((prev: DashboardData | null) => ({
          ...prev!,
          timeSeries: data.data.timeSeries || prev!.timeSeries,
          heatmapData: data.data.heatmap || prev!.heatmapData,
          tableData: data.data.table || prev!.tableData
        }));
      }
    };

    // Register event handlers
    on('room:joined', handleRoomJoined);
    on('user:joined', handleUserJoined);
    on('user:left', handleUserLeft);
    on('data:update', handleDataUpdate);

    // Cleanup
    return () => {
      off('room:joined', handleRoomJoined);
      off('user:joined', handleUserJoined);
      off('user:left', handleUserLeft);
      off('data:update', handleDataUpdate);
      
      emit('room:leave', { roomId });
      emit('data:unsubscribe', { channel: 'metrics' });
      emit('data:unsubscribe', { channel: 'analytics' });
    };
  }, [connected, emit, on, off, roomId, user]);

  // ==================== Initial Data Fetch ====================

  const fetchInitialData = useCallback(async () => {
    try {
      setError(null);
      const response = await fetch('/api/analytics/dashboard/main', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      setDashboardData(data);
      setLoading(false);
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      setError(`Failed to load dashboard data: ${errorMessage}`);
      setLoading(false);
      showError('Failed to load dashboard data', 'Connection Error');
    }
  }, [showError]);

  useEffect(() => {
    fetchInitialData();

    // Refresh every 5 minutes (fallback)
    const interval = setInterval(fetchInitialData, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, [fetchInitialData]);

  // Monitor WebSocket connection errors
  useEffect(() => {
    if (wsError) {
      showError(`WebSocket connection error: ${wsError.message}`, 'Connection Issue');
    }
  }, [wsError, showError]);

  // ==================== Cursor Tracking ====================

  const handleMouseMove = useCallback((event: React.MouseEvent) => {
    if (!connected) return;

    const position = {
      x: event.clientX,
      y: event.clientY,
      pageX: event.pageX,
      pageY: event.pageY
    };

    emit('cursor:move', { roomId, position });
  }, [connected, emit, roomId]);

  // ==================== Rendering ====================

  if (error) {
    return (
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
        gap={3}
        p={3}
      >
        <CloudOffIcon sx={{ fontSize: 80, color: 'error.main' }} />
        <Typography variant="h5" color="error" textAlign="center">
          {error}
        </Typography>
        <Typography variant="body1" color="text.secondary" textAlign="center" maxWidth={500}>
          We're having trouble connecting to the server. This could be a temporary network issue.
        </Typography>
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={fetchInitialData}
          size="large"
        >
          Retry Connection
        </Button>
      </Box>
    );
  }

  if (loading || !dashboardData) {
    return (
      <LoadingScreen
        message="Loading Dashboard"
        submessage="Connecting to real-time analytics services..."
      />
    );
  }

  return (
    <Box
      sx={{ p: 3, minHeight: '100vh', bgcolor: 'background.default' }}
      onMouseMove={handleMouseMove}
    >
      {/* Header */}
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 3,
          flexWrap: 'wrap',
          gap: 2
        }}
      >
        <Box display="flex" alignItems="center" gap={2}>
          <Typography variant="h4" component="h1" fontWeight="bold">
            Real-Time Analytics Dashboard
          </Typography>
          
          {/* Connection Status Badge */}
          <Tooltip title={connected ? 'Connected to real-time services' : 'Disconnected - reconnecting...'}>
            <Box
              sx={{
                px: 2,
                py: 0.5,
                bgcolor: connected ? 'success.main' : 'warning.main',
                color: 'white',
                borderRadius: 2,
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                transition: 'all 0.3s ease'
              }}
            >
              <Box
                sx={{
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  bgcolor: 'white',
                  animation: connected ? 'pulse 2s infinite' : 'none',
                  '@keyframes pulse': {
                    '0%, 100%': { opacity: 1 },
                    '50%': { opacity: 0.4 }
                  }
                }}
              />
              <Typography variant="body2" fontWeight="bold">
                {connected ? 'LIVE' : 'OFFLINE'}
              </Typography>
            </Box>
          </Tooltip>
        </Box>
        
        <Box display="flex" alignItems="center" gap={2}>
          <UserPresence collaborators={collaborators} />
          
          <Tooltip title="Refresh data">
            <Button
              variant="outlined"
              size="small"
              startIcon={<RefreshIcon />}
              onClick={fetchInitialData}
            >
              Refresh
            </Button>
          </Tooltip>
        </Box>
      </Box>

      {/* Metrics Overview */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Paper
            sx={{
              p: 3,
              display: 'flex',
              flexDirection: 'column',
              bgcolor: 'primary.main',
              color: 'primary.contrastText'
            }}
            elevation={3}
          >
            <Typography variant="h3" fontWeight="bold">
              {dashboardData.metrics.activeUsers.toLocaleString()}
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              Active Users
            </Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Paper
            sx={{
              p: 3,
              display: 'flex',
              flexDirection: 'column',
              bgcolor: 'secondary.main',
              color: 'secondary.contrastText'
            }}
            elevation={3}
          >
            <Typography variant="h3" fontWeight="bold">
              {dashboardData.metrics.requestsPerSecond.toFixed(1)}
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              Requests / Second
            </Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Paper
            sx={{
              p: 3,
              display: 'flex',
              flexDirection: 'column',
              bgcolor: 'success.main',
              color: 'success.contrastText'
            }}
            elevation={3}
          >
            <Typography variant="h3" fontWeight="bold">
              {dashboardData.metrics.averageLatency.toFixed(0)}ms
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              Average Latency
            </Typography>
          </Paper>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Paper
            sx={{
              p: 3,
              display: 'flex',
              flexDirection: 'column',
              bgcolor: dashboardData.metrics.errorRate > 0.05 ? 'error.main' : 'info.main',
              color: 'white'
            }}
            elevation={3}
          >
            <Typography variant="h3" fontWeight="bold">
              {(dashboardData.metrics.errorRate * 100).toFixed(2)}%
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              Error Rate
            </Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3}>
        {/* Time Series Chart */}
        <Grid item xs={12} lg={8}>
          <Paper sx={{ p: 3, height: 400 }} elevation={2}>
            <Typography variant="h6" gutterBottom>
              Real-Time Metrics
            </Typography>
            <TimeSeriesChart
              data={dashboardData.timeSeries}
              height={320}
            />
          </Paper>
        </Grid>

        {/* Heat Map */}
        <Grid item xs={12} lg={4}>
          <Paper sx={{ p: 3, height: 400 }} elevation={2}>
            <Typography variant="h6" gutterBottom>
              Activity Heatmap
            </Typography>
            <HeatMap
              data={dashboardData.heatmapData}
              height={320}
            />
          </Paper>
        </Grid>

        {/* Data Table */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }} elevation={2}>
            <Typography variant="h6" gutterBottom>
              Recent Events
            </Typography>
            <DataTable
              data={dashboardData.tableData}
              columns={[
                { field: 'timestamp', headerName: 'Timestamp', width: 180 },
                { field: 'event', headerName: 'Event', width: 200 },
                { field: 'user', headerName: 'User', width: 150 },
                { field: 'status', headerName: 'Status', width: 120 },
                { field: 'details', headerName: 'Details', flex: 1 }
              ]}
            />
          </Paper>
        </Grid>
      </Grid>

      {/* Live Cursors Overlay */}
      <LiveCursors collaborators={collaborators} />

      {/* Chat Panel */}
      <ChatPanel roomId={roomId} />

      {/* Notifications */}
      <Notification
        open={notification.open}
        message={notification.message}
        title={notification.title}
        type={notification.type}
        onClose={hideNotification}
      />
    </Box>
  );
};
