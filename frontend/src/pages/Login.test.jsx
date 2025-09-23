import Login from "./Login";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import * as api from '../services/api';
import { useNavigate } from "react-router-dom";
import userEvent from "@testing-library/user-event";

const mockNavigate = jest.fn();


jest.mock('../services/api', () => ({
    loginUser: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

test("Renders Login component", () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );
    expect(screen.getByPlaceholderText("Username")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
    expect(screen.getByText(/don't have an account/i)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /register/i })).toHaveAttribute("href", "/register");
});

test("Shows error for empty fields", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    await waitFor(() => {
        expect(screen.getAllByText(/username and password are required/i).length).toBeGreaterThan(0);
    });
});

test("Shows error for invalid login", async () => {
    api.loginUser.mockRejectedValue(new Error("Login failed. Please check your credentials."));
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'wronguser' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'wrongpass' } });
    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    await waitFor(() => {
        expect(screen.getAllByText(/login failed/i).length).toBeGreaterThan(0);
    });
});

test("Calls loginUser API on form submit", async () => {
    api.loginUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );
    
    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    await waitFor(() => {
        expect(api.loginUser).toHaveBeenCalledWith({ username: 'testuser', password: 'Goodpass1!' });
    });
});

test("Shows spinner when loading", async () => {
    api.loginUser.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    expect(screen.getAllByLabelText(/loading/i).length).toBeGreaterThan(0);
});

test("Redirects on successful login", async () => {
    api.loginUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /login/i }));

    await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
});

test("User can tab through inputs and submit", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>
    );

    const user = userEvent.setup();
    const usernameInput = screen.getByPlaceholderText("Username");
    const passwordInput = screen.getByPlaceholderText("Password");
    const loginButton = screen.getByRole("button", { name: /login/i });

    await user.tab();
    expect(usernameInput).toHaveFocus();

    await user.tab();
    expect(passwordInput).toHaveFocus();

    await user.tab();
    expect(loginButton).toHaveFocus();
});