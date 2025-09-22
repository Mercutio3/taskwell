import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import * as api from '../services/api';
import { useNavigate } from "react-router-dom";
import userEvent from "@testing-library/user-event";
import Register from "./Register";
import { act } from "@testing-library/react";

const mockNavigate = jest.fn();

jest.mock('../services/api', () => ({
    registerUser: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

beforeEach(() => {
    jest.clearAllMocks();
});

test("Renders Register component", () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );
    expect(screen.getByPlaceholderText("Username")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Email")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /register/i })).toBeInTheDocument();
    expect(screen.getByText(/already have an account/i)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /login/i })).toHaveAttribute("href", "/login");
});

test("Success message on valid registration", async () => {
    api.registerUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getByText(/registration successful/i)).toBeInTheDocument();
    });
});

test("Redirects to login on successful registration", async () => {
    jest.useFakeTimers();
    api.registerUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getByText(/registration successful/i)).toBeInTheDocument();
    });

    await act(async () => {
        jest.runAllTimers();
    });

    expect(mockNavigate).toHaveBeenCalledWith('/login');
    jest.useRealTimers();
});

test("User can tab through inputs and submit", async () => {
    render (
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    const user = userEvent.setup();
    const usernameInput = screen.getByPlaceholderText("Username");
    const emailInput = screen.getByPlaceholderText("Email");
    const passwordInput = screen.getByPlaceholderText("Password");
    const confirmPasswordInput = screen.getByPlaceholderText("Confirm Password");
    const registerButton = screen.getByRole("button", { name: /register/i });

    await user.tab();
    expect(usernameInput).toHaveFocus();

    await user.tab();
    expect(emailInput).toHaveFocus();

    await user.tab();
    expect(passwordInput).toHaveFocus();

    await user.tab();
    expect(confirmPasswordInput).toHaveFocus();

    await user.tab();
    expect(registerButton).toHaveFocus();
});

test("Shows spinner when loading", async () => {
    api.registerUser.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));
    
    expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
});

test("Spinner disappears after loading", async () => {
    api.registerUser.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    // Wait for spinner to appear
    await waitFor(() => {
        expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
    });

    // Wait for spinner to disappear
    await waitFor(() => {
        expect(screen.queryByLabelText(/loading/i)).not.toBeInTheDocument();
    });
});

test("Shows error for empty fields", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getByText(/username must be between 3-50 characters/i)).toBeInTheDocument();
    });
});

test("Shows error for invalid username", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'ab' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getByText(/username must be between 3-50 characters/i)).toBeInTheDocument();
    });
});

test("Shows error for invalid email", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'invalidemail' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });

    const form = screen.getByRole("form", { name: /registration form/i });
    await act(async () => {
        fireEvent.submit(form);
    });

    await waitFor(() => {
        expect(screen.getAllByText(/please enter a valid email address/i).length).toBeGreaterThan(0);
    });
});

test("Shows error for password mismatch", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'DifferentPassword1!' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getAllByText(/passwords do not match/i).length).toBeGreaterThan(0);
    });
});

test("Shows error for invalid password", async () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'short' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'short' } });
    fireEvent.click(screen.getByRole("button", { name: /register/i }));

    await waitFor(() => {
        expect(screen.getAllByText(/password must be at least 8 characters/i).length).toBeGreaterThan(0);
    });
});

test("Form disables submit button when loading", async () => {
    api.registerUser.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 1000)));
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'testemail@example.com' } });
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'Goodpass1!' } });
    fireEvent.click(screen.getByRole("button", { type: "submit" }));

    // Check for disabled button and spinner
    const button = screen.getByRole("button", { type: "submit" });
    expect(button).toBeDisabled();
    expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
});

test("Pressing enter on any input submits the form", async () => {
    api.registerUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    const user = userEvent.setup();

    await user.type(screen.getByPlaceholderText("Username"), "testuser");
    await user.type(screen.getByPlaceholderText("Email"), "testemail@example.com");
    await user.type(screen.getByPlaceholderText("Password"), "Goodpass1!");
    await user.type(screen.getByPlaceholderText("Confirm Password"), "Goodpass1!");

    // Press Enter on each input and check submission
    for (const placeholder of ["Username", "Email", "Password", "Confirm Password"]) {
        await user.clear(screen.getByPlaceholderText(placeholder));
        await user.type(screen.getByPlaceholderText(placeholder), "testvalue{enter}");
        await waitFor(() => {
            expect(screen.getByText(/registration successful/i)).toBeInTheDocument();
        });
    }
});

test("No API call if validation fails", async () => {
    api.registerUser.mockResolvedValue({});
    render(
        <BrowserRouter>
            <AuthProvider>
                <Register />
            </AuthProvider>
        </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("Username"), { target: { value: 'ab' } }); // Invalid username
    fireEvent.change(screen.getByPlaceholderText("Email"), { target: { value: 'invalidemail' } }); // Invalid email
    fireEvent.change(screen.getByPlaceholderText("Password"), { target: { value: 'bad' } }); // Invalid password
    fireEvent.change(screen.getByPlaceholderText("Confirm Password"), { target: { value: 'mismatch' } }); // Mismatched confirm password
    fireEvent.click(screen.getByRole("button", { name: /register/i }));
    
    await waitFor(() => {
        expect(api.registerUser).not.toHaveBeenCalled();
    });
});