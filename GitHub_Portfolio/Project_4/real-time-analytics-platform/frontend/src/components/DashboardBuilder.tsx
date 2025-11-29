import React from 'react';
import { Box, Paper, Grid, Button } from '@mui/material';
import { Responsive, WidthProvider } from 'react-grid-layout';
import 'react-grid-layout/css/styles.css';
import 'react-resizable/css/styles.css';

const ResponsiveGridLayout = WidthProvider(Responsive);

export const DashboardBuilder: React.FC = () => {
  const [layouts, setLayouts] = React.useState({
    lg: [
      { i: 'chart1', x: 0, y: 0, w: 6, h: 4 },
      { i: 'chart2', x: 6, y: 0, w: 6, h: 4 },
      { i: 'chart3', x: 0, y: 4, w: 12, h: 4 }
    ]
  });

  return (
    <Box sx={{ p: 3 }}>
      <ResponsiveGridLayout
        className="layout"
        layouts={layouts}
        breakpoints={{ lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 }}
        cols={{ lg: 12, md: 10, sm: 6, xs: 4, xxs: 2 }}
        rowHeight={60}
        onLayoutChange={(layout, layouts) => setLayouts(layouts)}
      >
        <Paper key="chart1" sx={{ p: 2 }}>
          <div>Chart 1</div>
        </Paper>
        <Paper key="chart2" sx={{ p: 2 }}>
          <div>Chart 2</div>
        </Paper>
        <Paper key="chart3" sx={{ p: 2 }}>
          <div>Chart 3</div>
        </Paper>
      </ResponsiveGridLayout>
    </Box>
  );
};
