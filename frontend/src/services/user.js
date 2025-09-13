// src/services/user.js

const BASE_URL = 'http://localhost:8080/api';

export async function fetchCurrentUser() {
    const res = await fetch(`${BASE_URL}/users/me`, {
        credentials: 'include',
    });
    if (!res.ok) {
        const error = new Error('Failed to fetch user info');
        error.status = res.status;
        throw error;
    }
    return res.json();
}

export async function verifyCurrentUser() {
    const res = await fetch(`${BASE_URL}/users/me/verify`, {
        method: 'PUT',
        credentials: 'include',
    });
    if (!res.ok) throw new Error('Failed to verify user');
    return res;
}