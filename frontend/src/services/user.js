// src/services/user.js

const BASE_URL = 'http://localhost:8080/api';

export async function fetchCurrentUser() {
    const res = await fetch(`${BASE_URL}/users/me`, {
        credentials: 'include',
    });
    if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || 'Failed to fetch user info');
    }
    return res.json();
}

export async function verifyCurrentUser() {
    const res = await fetch(`${BASE_URL}/users/me/verify`, {
        method: 'PUT',
        credentials: 'include',
    });
    if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || 'Failed to verify user');
    }
    return res;
}