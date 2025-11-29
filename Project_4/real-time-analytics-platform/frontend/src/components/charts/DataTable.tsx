import React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

interface DataTableProps {
  data: Array<Record<string, any>>;
  columns: GridColDef[];
}

export const DataTable: React.FC<DataTableProps> = ({ data, columns }) => {
  const rows = data.map((item, index) => ({ id: index, ...item }));

  return (
    <DataGrid
      rows={rows}
      columns={columns}
      pageSize={10}
      rowsPerPageOptions={[5, 10, 25]}
      checkboxSelection
      disableSelectionOnClick
      autoHeight
      sx={{
        '& .MuiDataGrid-cell': {
          fontSize: '0.875rem'
        }
      }}
    />
  );
};
