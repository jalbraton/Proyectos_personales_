import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface User {
  id: string;
  email: string;
  username: string;
  roles: string[];
}

interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
  isAuthenticated: boolean;
}

interface UseAuthReturn extends AuthState {
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  register: (email: string, username: string, password: string) => Promise<void>;
}

export const useAuth = (): UseAuthReturn => {
  const [state, setState] = useState<AuthState>({
    user: null,
    loading: true,
    error: null,
    isAuthenticated: false
  });

  const navigate = useNavigate();

  const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8000';

  // Check if user is authenticated on mount
  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('accessToken');
      
      if (!token) {
        setState(prev => ({ ...prev, loading: false }));
        return;
      }

      try {
        const response = await axios.get(`${API_URL}/api/auth/me`, {
          headers: { Authorization: `Bearer ${token}` }
        });

        setState({
          user: response.data,
          loading: false,
          error: null,
          isAuthenticated: true
        });
      } catch (error) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setState({
          user: null,
          loading: false,
          error: null,
          isAuthenticated: false
        });
      }
    };

    checkAuth();
  }, [API_URL]);

  const login = useCallback(async (username: string, password: string) => {
    setState(prev => ({ ...prev, loading: true, error: null }));

    try {
      const response = await axios.post(`${API_URL}/api/auth/login`, 
        new URLSearchParams({
          username,
          password
        }),
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }
      );

      const { access_token, refresh_token } = response.data;

      localStorage.setItem('accessToken', access_token);
      localStorage.setItem('refreshToken', refresh_token);

      // Get user info
      const userResponse = await axios.get(`${API_URL}/api/auth/me`, {
        headers: { Authorization: `Bearer ${access_token}` }
      });

      setState({
        user: userResponse.data,
        loading: false,
        error: null,
        isAuthenticated: true
      });

      navigate('/dashboard');
    } catch (error: any) {
      setState({
        user: null,
        loading: false,
        error: error.response?.data?.detail || 'Login failed',
        isAuthenticated: false
      });
      throw error;
    }
  }, [API_URL, navigate]);

  const logout = useCallback(async () => {
    const token = localStorage.getItem('accessToken');

    try {
      await axios.post(`${API_URL}/api/auth/logout`, null, {
        headers: { Authorization: `Bearer ${token}` }
      });
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      
      setState({
        user: null,
        loading: false,
        error: null,
        isAuthenticated: false
      });

      navigate('/login');
    }
  }, [API_URL, navigate]);

  const register = useCallback(async (email: string, username: string, password: string) => {
    setState(prev => ({ ...prev, loading: true, error: null }));

    try {
      await axios.post(`${API_URL}/api/auth/register`, {
        email,
        username,
        password
      });

      // Auto-login after registration
      await login(username, password);
    } catch (error: any) {
      setState(prev => ({
        ...prev,
        loading: false,
        error: error.response?.data?.detail || 'Registration failed'
      }));
      throw error;
    }
  }, [API_URL, login]);

  return {
    ...state,
    login,
    logout,
    register
  };
};
