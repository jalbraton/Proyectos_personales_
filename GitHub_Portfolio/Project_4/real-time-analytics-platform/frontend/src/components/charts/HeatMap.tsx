import React from 'react';
import { Box } from '@mui/material';

interface HeatMapProps {
  data: number[][];
  height?: number;
}

export const HeatMap: React.FC<HeatMapProps> = ({ data, height = 300 }) => {
  const maxValue = Math.max(...data.flat());
  const cellSize = 40;

  const getColor = (value: number) => {
    const intensity = value / maxValue;
    return `rgba(33, 150, 243, ${intensity})`;
  };

  return (
    <Box sx={{ overflowX: 'auto', height }}>
      <svg width={data[0].length * cellSize} height={data.length * cellSize}>
        {data.map((row, rowIndex) =>
          row.map((value, colIndex) => (
            <g key={`${rowIndex}-${colIndex}`}>
              <rect
                x={colIndex * cellSize}
                y={rowIndex * cellSize}
                width={cellSize - 2}
                height={cellSize - 2}
                fill={getColor(value)}
                rx={4}
              />
              <text
                x={colIndex * cellSize + cellSize / 2}
                y={rowIndex * cellSize + cellSize / 2}
                textAnchor="middle"
                dominantBaseline="middle"
                fill="white"
                fontSize={12}
                fontWeight="bold"
              >
                {value}
              </text>
            </g>
          ))
        )}
      </svg>
    </Box>
  );
};
