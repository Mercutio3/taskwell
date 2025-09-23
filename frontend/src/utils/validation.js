export function isValidUsername(username) {
  if (typeof username !== "string") return false;
  if (username.length < 3 || username.length > 50) return false;
  // Only letters, numbers, dots, underscores; no consecutive dots/underscores
  const validPattern = /^(?!.*([_.])\1)[a-zA-Z0-9._]+$/;
  return validPattern.test(username);
}

export function isValidEmail(email) {
  // Stricter email validation: must have one @, at least one dot after @, and valid characters
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);
}

export function isValidPassword(password) {
  return (
    typeof password === "string" &&
    password.length >= 8 &&
    password.length <= 50 &&
    /[A-Z]/.test(password) && // at least one uppercase
    /[a-z]/.test(password) && // at least one lowercase
    /\d/.test(password) && // at least one number
    /[^A-Za-z0-9]/.test(password) // at least one special character
  );
}
