import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BrowserRouter } from "react-router-dom";
import Profile from "./Profile";
import { AuthProvider } from "../context/AuthContext";
import { fetchCurrentUser } from "../services/user";

jest.mock("../services/user", () => ({
  fetchCurrentUser: jest.fn().mockResolvedValue({
    id: 1,
    username: "testuser",
    email: "testuser@example.com",
    verified: true,
  }),
  verifyCurrentUser: jest.fn().mockResolvedValue({}),
}));

jest.mock("../services/api", () => ({
  updateEmail: jest.fn().mockResolvedValue({}),
  updateUsername: jest.fn().mockResolvedValue({}),
}));

test("Renders Profile component with user data", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );
  expect(screen.getByText(/welcome to your profile/i)).toBeInTheDocument();
  const info = await screen.findByLabelText(/user information/i);
  expect(within(info).getByText(/username:/i)).toBeInTheDocument();
  expect(within(info).getByText("testuser")).toBeInTheDocument();
  expect(within(info).getByText(/email:/i)).toBeInTheDocument();
  expect(within(info).getByText("testuser@example.com")).toBeInTheDocument();
  expect(within(info).getByText(/verified:/i)).toBeInTheDocument();
  expect(within(info).getByText("Yes")).toBeInTheDocument();
});

test("Handle username change", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );

  const editButton = await screen.findByRole("button", {
    name: /edit username/i,
  });
  await userEvent.click(editButton);
  const usernameInput = screen.getByRole("textbox", { name: /new username/i });
  const passwordInput = screen.getByLabelText(/current password/i);
  const saveButton = screen.getByRole("button", { name: /save/i });
  await userEvent.clear(usernameInput);
  await userEvent.type(usernameInput, "newusername");
  await userEvent.type(passwordInput, "currentpassword");
  await userEvent.click(saveButton);

  await waitFor(() => {
    expect(fetchCurrentUser).toHaveBeenCalledTimes(3);
  });
  expect(
    await screen.findByText(/username updated successfully!/i),
  ).toBeInTheDocument();
});

test("Handle email change", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );

  const editButton = await screen.findByRole("button", { name: /edit email/i });
  await userEvent.click(editButton);
  const emailInput = screen.getByRole("textbox", { name: /new email/i });
  const passwordInput = screen.getByLabelText(/current password/i);
  const saveButton = screen.getByRole("button", { name: /save/i });
  await userEvent.clear(emailInput);
  await userEvent.type(emailInput, "newemail@example.com");
  await userEvent.type(passwordInput, "currentpassword");
  await userEvent.click(saveButton);

  await waitFor(() => {
    expect(fetchCurrentUser).toHaveBeenCalledTimes(5);
  });
  expect(
    await screen.findByText(/email updated successfully!/i),
  ).toBeInTheDocument();
});

test("Shows error on invalid username", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );

  const editButton = await screen.findByRole("button", {
    name: /edit username/i,
  });
  await userEvent.click(editButton);
  const usernameInput = screen.getByRole("textbox", { name: /new username/i });
  const saveButton = screen.getByRole("button", { name: /save/i });
  await userEvent.clear(usernameInput);
  await userEvent.type(usernameInput, "ab");
  await userEvent.click(saveButton);

  const errorDiv = await screen.findByText(
    /Username must be 3-50 characters/i,
    { selector: '[aria-live="assertive"]' },
  );
  expect(errorDiv).toBeInTheDocument();
});

test("Shows error on invalid email", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );

  const editButton = await screen.findByRole("button", { name: /edit email/i });
  await userEvent.click(editButton);
  const emailInput = screen.getByRole("textbox", { name: /new email/i });
  const saveButton = screen.getByRole("button", { name: /save/i });
  await userEvent.clear(emailInput);
  await userEvent.type(emailInput, "invalidemail");
  await waitFor(() => {
    expect(emailInput.value).toBe("invalidemail");
  });
  await userEvent.click(saveButton);

  const errorDiv = await screen.findByText(
    /Please enter a valid email address/i,
    { selector: '[aria-live="assertive"]' },
  );
  expect(errorDiv).toBeInTheDocument();
});

test("Handles API error on username update", async () => {
  const { updateUsername } = require("../services/api");
  updateUsername.mockRejectedValue(new Error("Failed to update username"));

  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <Profile />
      </BrowserRouter>
    </AuthProvider>,
  );

  const editButton = await screen.findByRole("button", {
    name: /edit username/i,
  });
  await userEvent.click(editButton);
  const usernameInput = screen.getByRole("textbox", { name: /new username/i });
  const passwordInput = screen.getByLabelText(/current password/i);
  const saveButton = screen.getByRole("button", { name: /save/i });
  await userEvent.clear(usernameInput);
  await userEvent.type(usernameInput, "validusername");
  await userEvent.type(passwordInput, "currentpassword");
  await userEvent.click(saveButton);

  const errorDiv = await screen.findByText(/Failed to update username/i, {
    selector: '[aria-live="assertive"]',
  });
  expect(errorDiv).toBeInTheDocument();
});
