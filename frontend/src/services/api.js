// src/services/api.js

const BASE_URL = 'http://localhost:8080/api';

export async function registerUser(data) {
  const res = await fetch(`${BASE_URL}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('Registration failed');
  return res.json();
}

export async function loginUser(data) {
  const res = await fetch(`${BASE_URL}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
    credentials: 'include', // if using cookies/sessions
  });
  if (!res.ok) throw new Error('Login failed');
  return res.json();
}

export async function createTask(data) {
  const res = await fetch(`${BASE_URL}/tasks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
    credentials: 'include', // if auth required
  });
  if (!res.ok) throw new Error('Task creation failed');
  return res.json();
}
