// src/services/api.js

const BASE_URL = "http://localhost:8080/api";

export async function registerUser(data) {
  const res = await fetch(`${BASE_URL}/users`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const errorText = await res.text();
    console.error("Registration error:", errorText);
    throw new Error(errorText || "Registration failed");
  }
  return res.json();
}

export async function loginUser(data) {
  const res = await fetch("http://localhost:8080/login", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      username: data.username,
      password: data.password,
    }),
    credentials: "include", // for cookies/sessions
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Login failed");
  }
  return res;
}

export async function updateUsername(userId, newUsername) {
  const res = await fetch(`${BASE_URL}/users/${userId}/username`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: newUsername }),
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to update username");
  }
  return res.json();
}

export async function updateEmail(userId, newEmail) {
  const res = await fetch(`${BASE_URL}/users/${userId}/email`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: newEmail }),
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to update email");
  }
  return res.json();
}

export async function createTask(data) {
  const res = await fetch(`${BASE_URL}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Task creation failed");
  }
  return res.json();
}

export async function getTask(id) {
  const res = await fetch(`${BASE_URL}/tasks/${id}`, {
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to fetch task");
  }
  return res.json();
}

export async function updateTask(id, data) {
  const res = await fetch(`${BASE_URL}/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Task update failed");
  }
  return res.json();
}

export async function deleteTask(id) {
  const res = await fetch(`${BASE_URL}/tasks/${id}`, {
    method: "DELETE",
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to delete task");
  }
  return;
}

export async function completeTask(id) {
  const res = await fetch(`${BASE_URL}/tasks/${id}/complete`, {
    method: "POST",
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to complete task");
  }
  return res.json();
}

export async function uncompleteTask(id) {
  const res = await fetch(`${BASE_URL}/tasks/${id}/uncomplete`, {
    method: "POST",
    credentials: "include",
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || "Failed to uncomplete task");
  }
  return res.json();
}
