// src/services/user.js

const BASE_URL = 'http://localhost:8080/api';

export async function fetchCurrentUser() {
    const res = await fetch(`${BASE_URL}/users/me`, {
        credentials: 'include',
    });
    if (!res.ok) throw new Error('Failed to fetch user info');
    return res.json();
}