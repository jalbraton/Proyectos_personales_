/**
 * WebSocket Real-Time Communication Service
 * ==========================================

Enterprise WebSocket service featuring:
- Socket.IO for bidirectional communication
- Redis adapter for horizontal scaling
- CRDT-based collaboration (Yjs integration)
- Presence tracking
- Room management
- Real-time cursor sharing
- Document synchronization
- Event broadcasting
"""

const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const Redis = require('ioredis');
const { createAdapter } = require('@socket.io/redis-adapter');
const jwt = require('jsonwebtoken');
const pino = require('pino');
const * as Y = require('yjs');

// Configuration
const PORT = process.env.PORT || 8004;
const REDIS_URL = process.env.REDIS_URL || 'redis://redis:6379';
const JWT_SECRET = process.env.JWT_SECRET || 'your-super-secret-key';

// Logger
const logger = pino({
    level: process.env.LOG_LEVEL || 'info',
    transport: {
        target: 'pino-pretty',
        options: { colorize: true }
    }
});

// Initialize Express & HTTP server
const app = express();
const server = http.createServer(app);

// Initialize Socket.IO with Redis adapter for clustering
const io = socketIo(server, {
    cors: {
        origin: '*',
        methods: ['GET', 'POST'],
        credentials: true
    },
    transports: ['websocket', 'polling'],
    pingTimeout: 60000,
    pingInterval: 25000
});

// Redis clients for Socket.IO adapter
const pubClient = new Redis(REDIS_URL);
const subClient = pubClient.duplicate();

pubClient.on('connect', () => logger.info('Redis pub client connected'));
subClient.on('connect', () => logger.info('Redis sub client connected'));

io.adapter(createAdapter(pubClient, subClient));

// ==================== Data Structures ====================

// Active users by room
const activeUsers = new Map(); // roomId -> Set<userId>

// User presence information
const userPresence = new Map(); // userId -> { socketId, username, roomId, cursor, status }

// CRDT documents (Yjs)
const yDocuments = new Map(); // documentId -> Y.Doc

// Room metadata
const rooms = new Map(); // roomId -> { name, type, members, created, metadata }

// ==================== Utilities ====================

class PresenceManager {
    static addUser(userId, userData) {
        userPresence.set(userId, {
            ...userData,
            joinedAt: Date.now(),
            lastSeen: Date.now()
        });
        
        const { roomId } = userData;
        if (!activeUsers.has(roomId)) {
            activeUsers.set(roomId, new Set());
        }
        activeUsers.get(roomId).add(userId);
        
        logger.info({ userId, roomId }, 'User joined room');
    }
    
    static removeUser(userId) {
        const user = userPresence.get(userId);
        if (user) {
            const { roomId } = user;
            const roomUsers = activeUsers.get(roomId);
            if (roomUsers) {
                roomUsers.delete(userId);
                if (roomUsers.size === 0) {
                    activeUsers.delete(roomId);
                }
            }
            userPresence.delete(userId);
            
            logger.info({ userId, roomId }, 'User left room');
        }
    }
    
    static updateCursor(userId, cursorPosition) {
        const user = userPresence.get(userId);
        if (user) {
            user.cursor = cursorPosition;
            user.lastSeen = Date.now();
        }
    }
    
    static updateStatus(userId, status) {
        const user = userPresence.get(userId);
        if (user) {
            user.status = status;
            user.lastSeen = Date.now();
        }
    }
    
    static getRoomUsers(roomId) {
        const userIds = activeUsers.get(roomId) || new Set();
        return Array.from(userIds).map(userId => userPresence.get(userId));
    }
    
    static getUserCount(roomId) {
        return (activeUsers.get(roomId) || new Set()).size;
    }
}

class CollaborationEngine {
    static getOrCreateDocument(documentId) {
        if (!yDocuments.has(documentId)) {
            const yDoc = new Y.Doc();
            yDocuments.set(documentId, yDoc);
            
            // Setup change listener for persistence
            yDoc.on('update', (update, origin) => {
                // In production, persist to database
                logger.debug({ documentId }, 'Document updated');
            });
            
            logger.info({ documentId }, 'Created new CRDT document');
        }
        return yDocuments.get(documentId);
    }
    
    static applyUpdate(documentId, update) {
        const yDoc = this.getOrCreateDocument(documentId);
        Y.applyUpdate(yDoc, Buffer.from(update));
    }
    
    static getState(documentId) {
        const yDoc = this.getOrCreateDocument(documentId);
        return Y.encodeStateAsUpdate(yDoc);
    }
    
    static getSnapshot(documentId) {
        const yDoc = this.getOrCreateDocument(documentId);
        
        // Convert Y.Doc to JSON representation
        const data = {};
        yDoc.share.forEach((value, key) => {
            if (value instanceof Y.Text) {
                data[key] = value.toString();
            } else if (value instanceof Y.Array) {
                data[key] = value.toJSON();
            } else if (value instanceof Y.Map) {
                data[key] = value.toJSON();
            }
        });
        
        return data;
    }
}

class RoomManager {
    static createRoom(roomId, metadata) {
        if (!rooms.has(roomId)) {
            rooms.set(roomId, {
                id: roomId,
                ...metadata,
                created: Date.now(),
                members: new Set()
            });
            logger.info({ roomId }, 'Room created');
        }
        return rooms.get(roomId);
    }
    
    static joinRoom(roomId, userId) {
        const room = rooms.get(roomId);
        if (room) {
            room.members.add(userId);
            return true;
        }
        return false;
    }
    
    static leaveRoom(roomId, userId) {
        const room = rooms.get(roomId);
        if (room) {
            room.members.delete(userId);
            if (room.members.size === 0 && room.temporary) {
                rooms.delete(roomId);
                logger.info({ roomId }, 'Temporary room deleted');
            }
            return true;
        }
        return false;
    }
    
    static getRoomInfo(roomId) {
        const room = rooms.get(roomId);
        if (room) {
            return {
                ...room,
                members: Array.from(room.members),
                activeUsers: PresenceManager.getUserCount(roomId)
            };
        }
        return null;
    }
    
    static listRooms() {
        return Array.from(rooms.values()).map(room => ({
            id: room.id,
            name: room.name,
            type: room.type,
            memberCount: room.members.size,
            activeUsers: PresenceManager.getUserCount(room.id)
        }));
    }
}

// ==================== Socket.IO Middleware ====================

io.use((socket, next) => {
    const token = socket.handshake.auth.token;
    
    if (!token) {
        return next(new Error('Authentication token required'));
    }
    
    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        socket.user = decoded;
        logger.info({ userId: decoded.user_id, username: decoded.username }, 'Socket authenticated');
        next();
    } catch (error) {
        logger.error({ error: error.message }, 'Socket authentication failed');
        next(new Error('Invalid authentication token'));
    }
});

// ==================== Socket.IO Events ====================

io.on('connection', (socket) => {
    const { user_id: userId, username, email } = socket.user;
    
    logger.info({
        socketId: socket.id,
        userId,
        username
    }, 'Client connected');
    
    // ===== Room Management =====
    
    socket.on('room:join', ({ roomId, roomName, roomType }) => {
        // Leave previous room if any
        const rooms = Array.from(socket.rooms).filter(r => r !== socket.id);
        rooms.forEach(r => socket.leave(r));
        
        // Join new room
        socket.join(roomId);
        
        // Create room if it doesn't exist
        RoomManager.createRoom(roomId, {
            name: roomName || roomId,
            type: roomType || 'workspace',
            temporary: true
        });
        
        RoomManager.joinRoom(roomId, userId);
        
        // Add user presence
        PresenceManager.addUser(userId, {
            socketId: socket.id,
            username,
            email,
            roomId,
            cursor: null,
            status: 'active'
        });
        
        // Send current room state to joining user
        socket.emit('room:joined', {
            roomId,
            users: PresenceManager.getRoomUsers(roomId),
            roomInfo: RoomManager.getRoomInfo(roomId)
        });
        
        // Broadcast to other users in room
        socket.to(roomId).emit('user:joined', {
            userId,
            username,
            email,
            timestamp: Date.now()
        });
        
        logger.info({ userId, username, roomId }, 'User joined room');
    });
    
    socket.on('room:leave', ({ roomId }) => {
        socket.leave(roomId);
        RoomManager.leaveRoom(roomId, userId);
        PresenceManager.removeUser(userId);
        
        socket.to(roomId).emit('user:left', {
            userId,
            username,
            timestamp: Date.now()
        });
        
        logger.info({ userId, roomId }, 'User left room');
    });
    
    // ===== Real-time Collaboration =====
    
    socket.on('document:sync', ({ documentId, roomId }) => {
        // Send full document state to client
        const state = CollaborationEngine.getState(documentId);
        socket.emit('document:state', {
            documentId,
            state: Array.from(state)
        });
        
        logger.debug({ documentId, userId }, 'Document state synced');
    });
    
    socket.on('document:update', ({ documentId, roomId, update }) => {
        // Apply update to CRDT document
        CollaborationEngine.applyUpdate(documentId, update);
        
        // Broadcast update to other users in room
        socket.to(roomId).emit('document:update', {
            documentId,
            update,
            userId,
            timestamp: Date.now()
        });
        
        logger.debug({ documentId, userId }, 'Document updated');
    });
    
    // ===== Cursor & Presence =====
    
    socket.on('cursor:move', ({ roomId, position }) => {
        PresenceManager.updateCursor(userId, position);
        
        socket.to(roomId).emit('cursor:update', {
            userId,
            username,
            position,
            timestamp: Date.now()
        });
    });
    
    socket.on('presence:update', ({ roomId, status }) => {
        PresenceManager.updateStatus(userId, status);
        
        socket.to(roomId).emit('presence:change', {
            userId,
            username,
            status,
            timestamp: Date.now()
        });
        
        logger.debug({ userId, status }, 'Presence updated');
    });
    
    // ===== Chat & Notifications =====
    
    socket.on('message:send', ({ roomId, message, type }) => {
        const messageData = {
            id: require('crypto').randomUUID(),
            userId,
            username,
            message,
            type: type || 'text',
            timestamp: Date.now()
        };
        
        // Broadcast to room
        io.to(roomId).emit('message:received', messageData);
        
        logger.info({ userId, roomId, messageType: type }, 'Message sent');
    });
    
    socket.on('notification:send', ({ targetUserId, notification }) => {
        const targetUser = Array.from(userPresence.values())
            .find(u => u.userId === targetUserId);
        
        if (targetUser) {
            io.to(targetUser.socketId).emit('notification:received', {
                ...notification,
                from: userId,
                fromUsername: username,
                timestamp: Date.now()
            });
        }
    });
    
    // ===== Data Streaming =====
    
    socket.on('data:subscribe', ({ channel, filters }) => {
        socket.join(`data:${channel}`);
        
        logger.info({ userId, channel }, 'Subscribed to data channel');
    });
    
    socket.on('data:unsubscribe', ({ channel }) => {
        socket.leave(`data:${channel}`);
        
        logger.info({ userId, channel }, 'Unsubscribed from data channel');
    });
    
    // ===== Disconnect =====
    
    socket.on('disconnect', (reason) => {
        const rooms = Array.from(socket.rooms).filter(r => r !== socket.id);
        
        rooms.forEach(roomId => {
            RoomManager.leaveRoom(roomId, userId);
            
            socket.to(roomId).emit('user:left', {
                userId,
                username,
                reason,
                timestamp: Date.now()
            });
        });
        
        PresenceManager.removeUser(userId);
        
        logger.info({
            socketId: socket.id,
            userId,
            username,
            reason
        }, 'Client disconnected');
    });
});

// ==================== REST API for Broadcasting ====================

app.use(express.json());

app.post('/broadcast/room/:roomId', (req, res) => {
    const { roomId } = req.params;
    const { event, data } = req.body;
    
    io.to(roomId).emit(event, data);
    
    res.json({ success: true, room: roomId, event });
});

app.post('/broadcast/channel/:channel', (req, res) => {
    const { channel } = req.params;
    const { data } = req.body;
    
    io.to(`data:${channel}`).emit('data:update', {
        channel,
        data,
        timestamp: Date.now()
    });
    
    res.json({ success: true, channel });
});

app.get('/rooms', (req, res) => {
    res.json({ rooms: RoomManager.listRooms() });
});

app.get('/rooms/:roomId', (req, res) => {
    const { roomId } = req.params;
    const roomInfo = RoomManager.getRoomInfo(roomId);
    
    if (roomInfo) {
        res.json(roomInfo);
    } else {
        res.status(404).json({ error: 'Room not found' });
    }
});

app.get('/health', (req, res) => {
    res.json({
        status: 'healthy',
        service: 'websocket',
        timestamp: new Date().toISOString(),
        connections: io.engine.clientsCount,
        rooms: rooms.size
    });
});

// ==================== Start Server ====================

server.listen(PORT, () => {
    logger.info(`WebSocket service running on port ${PORT}`);
    logger.info('Redis adapter initialized for horizontal scaling');
});

module.exports = { app, io };
