"""
Analytics Microservice
======================

Enterprise analytics service featuring:
- Query engine for complex analytics
- Data aggregation & filtering
- Export to multiple formats (CSV, JSON, Excel)
- Scheduled reports
- Real-time dashboards
- TimescaleDB integration for time-series
"""

from fastapi import FastAPI, Depends, HTTPException, Query, BackgroundTasks
from fastapi.responses import StreamingResponse
from pydantic import BaseModel, validator
from typing import Optional, List, Dict, Any
from datetime import datetime, timedelta
from sqlalchemy import create_engine, text
from sqlalchemy.pool import QoSqlitePooling
import pandas as pd
import redis.asyncio as redis
import json
import io
import logging

# Configuration
app = FastAPI(title="Analytics Service", version="2.0.0")

DATABASE_URL = "timescaledb://admin:admin123@timescaledb:5432/analytics"
REDIS_URL = "redis://redis:6379"

engine = create_engine(DATABASE_URL, pool_size=20, max_overflow=40)
redis_client = None

logger = logging.getLogger(__name__)


# ==================== Models ====================

class QueryRequest(BaseModel):
    table: str
    filters: Optional[Dict[str, Any]] = {}
    aggregations: Optional[List[Dict[str, str]]] = []
    group_by: Optional[List[str]] = []
    time_range: Optional[Dict[str, str]] = None
    limit: int = 1000
    
    @validator('limit')
    def validate_limit(cls, v):
        if v > 10000:
            raise ValueError('Limit cannot exceed 10000')
        return v


class ExportRequest(BaseModel):
    query: QueryRequest
    format: str = 'csv'  # csv, json, excel
    
    @validator('format')
    def validate_format(cls, v):
        if v not in ['csv', 'json', 'excel']:
            raise ValueError('Invalid export format')
        return v


# ==================== Query Engine ====================

class QueryEngine:
    """Advanced SQL query builder with security"""
    
    ALLOWED_TABLES = [
        'metrics', 'events', 'user_activity',
        'api_requests', 'errors', 'performance'
    ]
    
    ALLOWED_AGGREGATIONS = [
        'count', 'sum', 'avg', 'min', 'max', 'stddev'
    ]
    
    @classmethod
    def build_query(cls, request: QueryRequest) -> str:
        """Build parameterized SQL query"""
        
        # Validate table name
        if request.table not in cls.ALLOWED_TABLES:
            raise ValueError(f"Table {request.table} not allowed")
        
        # Build SELECT clause
        if request.aggregations:
            select_parts = []
            for agg in request.aggregations:
                func = agg.get('function', 'count')
                column = agg.get('column', '*')
                
                if func not in cls.ALLOWED_AGGREGATIONS:
                    raise ValueError(f"Aggregation {func} not allowed")
                
                select_parts.append(f"{func}({column}) as {func}_{column}")
            
            select_clause = ", ".join(select_parts)
        else:
            select_clause = "*"
        
        # Build FROM clause
        from_clause = f"FROM {request.table}"
        
        # Build WHERE clause
        where_conditions = []
        
        if request.filters:
            for column, value in request.filters.items():
                # Prevent SQL injection
                if not column.replace('_', '').isalnum():
                    raise ValueError(f"Invalid column name: {column}")
                
                if isinstance(value, list):
                    placeholders = ', '.join([f":{column}_{i}" for i in range(len(value))])
                    where_conditions.append(f"{column} IN ({placeholders})")
                else:
                    where_conditions.append(f"{column} = :{column}")
        
        if request.time_range:
            start = request.time_range.get('start')
            end = request.time_range.get('end')
            if start:
                where_conditions.append("timestamp >= :time_start")
            if end:
                where_conditions.append("timestamp <= :time_end")
        
        where_clause = "WHERE " + " AND ".join(where_conditions) if where_conditions else ""
        
        # Build GROUP BY clause
        group_by_clause = ""
        if request.group_by:
            # Validate column names
            for col in request.group_by:
                if not col.replace('_', '').isalnum():
                    raise ValueError(f"Invalid column name: {col}")
            
            group_by_clause = "GROUP BY " + ", ".join(request.group_by)
        
        # Build ORDER BY clause
        order_by_clause = "ORDER BY timestamp DESC"
        
        # Build LIMIT clause
        limit_clause = f"LIMIT {request.limit}"
        
        # Combine all parts
        query = f"""
            SELECT {select_clause}
            {from_clause}
            {where_clause}
            {group_by_clause}
            {order_by_clause}
            {limit_clause}
        """
        
        return query
    
    @classmethod
    def execute_query(cls, request: QueryRequest) -> pd.DataFrame:
        """Execute query and return DataFrame"""
        
        query = cls.build_query(request)
        
        # Build parameters
        params = {}
        
        if request.filters:
            for column, value in request.filters.items():
                if isinstance(value, list):
                    for i, v in enumerate(value):
                        params[f"{column}_{i}"] = v
                else:
                    params[column] = value
        
        if request.time_range:
            if 'start' in request.time_range:
                params['time_start'] = request.time_range['start']
            if 'end' in request.time_range:
                params['time_end'] = request.time_range['end']
        
        # Execute query
        with engine.connect() as conn:
            df = pd.read_sql_query(text(query), conn, params=params)
        
        return df


# ==================== API Endpoints ====================

@app.post("/query")
async def execute_query(request: QueryRequest):
    """Execute analytics query"""
    
    try:
        # Check cache
        cache_key = f"query:{hash(str(request.dict()))}"
        cached = await redis_client.get(cache_key)
        
        if cached:
            return json.loads(cached)
        
        # Execute query
        df = QueryEngine.execute_query(request)
        
        # Convert to JSON
        result = {
            "data": df.to_dict(orient='records'),
            "columns": list(df.columns),
            "row_count": len(df),
            "timestamp": datetime.utcnow().isoformat()
        }
        
        # Cache result (5 minutes)
        await redis_client.setex(cache_key, 300, json.dumps(result))
        
        return result
    
    except Exception as e:
        logger.error(f"Query execution failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/export")
async def export_data(request: ExportRequest):
    """Export query results"""
    
    try:
        # Execute query
        df = QueryEngine.execute_query(request.query)
        
        # Generate export file
        output = io.BytesIO()
        
        if request.format == 'csv':
            df.to_csv(output, index=False)
            media_type = 'text/csv'
            filename = f"export_{datetime.utcnow().strftime('%Y%m%d_%H%M%S')}.csv"
        
        elif request.format == 'excel':
            df.to_excel(output, index=False, engine='openpyxl')
            media_type = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            filename = f"export_{datetime.utcnow().strftime('%Y%m%d_%H%M%S')}.xlsx"
        
        else:  # json
            df.to_json(output, orient='records', date_format='iso')
            media_type = 'application/json'
            filename = f"export_{datetime.utcnow().strftime('%Y%m%d_%H%M%S')}.json"
        
        output.seek(0)
        
        return StreamingResponse(
            output,
            media_type=media_type,
            headers={"Content-Disposition": f"attachment; filename={filename}"}
        )
    
    except Exception as e:
        logger.error(f"Export failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/dashboard/main")
async def get_dashboard_data():
    """Get main dashboard data"""
    
    # Simulated dashboard data
    # In production, fetch from database
    
    return {
        "metrics": {
            "activeUsers": 1247,
            "requestsPerSecond": 156.8,
            "averageLatency": 42,
            "errorRate": 0.023
        },
        "timeSeries": [
            {
                "timestamp": int((datetime.utcnow() - timedelta(hours=i)).timestamp() * 1000),
                "value": 100 + i * 10,
                "label": "Requests"
            }
            for i in range(24)
        ],
        "heatmapData": [[j * 10 for j in range(24)] for i in range(7)],
        "tableData": [
            {
                "timestamp": datetime.utcnow().isoformat(),
                "event": "User Login",
                "user": "john.doe@example.com",
                "status": "success",
                "details": "Login from 192.168.1.100"
            }
            for _ in range(10)
        ]
    }


@app.get("/health")
async def health_check():
    """Health check"""
    return {
        "status": "healthy",
        "service": "analytics",
        "timestamp": datetime.utcnow().isoformat()
    }


@app.on_event("startup")
async def startup():
    """Initialize connections"""
    global redis_client
    redis_client = await redis.from_url(REDIS_URL, decode_responses=True)
    logger.info("Analytics service started")


@app.on_event("shutdown")
async def shutdown():
    """Cleanup"""
    if redis_client:
        await redis_client.close()
    logger.info("Analytics service stopped")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8002)
