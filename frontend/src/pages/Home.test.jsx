import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import Home from "./Home";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

test("Renders Home component", async () => {
  render(
    <BrowserRouter>
      <Home />
    </BrowserRouter>
  );
  expect(screen.getByRole("main", { name: /home page/i })).toBeInTheDocument();
  expect(screen.getByText(/welcome to taskwell!/i)).toBeInTheDocument();
  expect(screen.getByText(/home page test./i)).toBeInTheDocument();
  const registerButton = await screen.getByRole("button", { name: /register/i });
  expect(registerButton).toBeInTheDocument();
  const loginButton = await screen.getByRole("button", { name: /login/i });
  expect(loginButton).toBeInTheDocument();
});

test("Register button redirects to /register", async () => {
  render(
    <BrowserRouter>
      <Home />
    </BrowserRouter>
  );
  const registerButton = await screen.getByRole("button", { name: /register/i });
  fireEvent.click(registerButton);
  expect(mockNavigate).toHaveBeenCalledWith('/register');
});

test("Login button redirects to /login", async () => {
  render(
    <BrowserRouter>
      <Home />
    </BrowserRouter>
  );
  const loginButton = await screen.getByRole("button", { name: /login/i });
  fireEvent.click(loginButton);
  expect(mockNavigate).toHaveBeenCalledWith('/login');
});

test("Register button is focusable", async () => {
  render(
    <BrowserRouter>
      <Home />
    </BrowserRouter>
  );
  const registerButton = await screen.getByRole("button", { name: /register/i });
  registerButton.focus();
  expect(registerButton).toHaveFocus();
});

test("Login button is focusable", async () => {
  render(
    <BrowserRouter>
      <Home />
    </BrowserRouter>
  );
  const loginButton = await screen.getByRole("button", { name: /login/i });
  loginButton.focus();
  expect(loginButton).toHaveFocus();
});