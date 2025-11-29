import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface TimeSeriesChartProps {
  data: Array<{
    timestamp: number;
    value: number;
    label: string;
  }>;
  height?: number;
}

export const TimeSeriesChart: React.FC<TimeSeriesChartProps> = ({ data, height = 300 }) => {
  return (
    <ResponsiveContainer width="100%" height={height}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis
          dataKey="timestamp"
          tickFormatter={(timestamp) => new Date(timestamp).toLocaleTimeString()}
        />
        <YAxis />
        <Tooltip
          labelFormatter={(timestamp) => new Date(timestamp).toLocaleString()}
        />
        <Legend />
        <Line
          type="monotone"
          dataKey="value"
          stroke="#8884d8"
          strokeWidth={2}
          dot={false}
          name="Requests"
        />
      </LineChart>
    </ResponsiveContainer>
  );
};
