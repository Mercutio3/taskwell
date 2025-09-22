import {fireEvent, render , screen} from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import Unauthorized from "./Unauthorized";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));


test("Renders Unauthorized component", () => {
    render(
        <BrowserRouter>
            <Unauthorized />
        </BrowserRouter>
    );
    expect(screen.getByRole("alert", { name: /unauthorized page/i })).toBeInTheDocument();
    expect(screen.getByText(/401 - unauthorized/i)).toBeInTheDocument();
    expect(screen.getByText(/you must be logged in to access this./i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
});

test("Login button redirects to /login", () => {
    render(
        <BrowserRouter>
            <Unauthorized />
        </BrowserRouter>
    );
    fireEvent.click(screen.getByRole("button", { name: /login/i }));
    expect(mockNavigate).toHaveBeenCalledWith('/login');
});

test("Login button is focusable", () => {
    render(
        <BrowserRouter>
            <Unauthorized />
        </BrowserRouter>
    );
    const button = screen.getByRole("button", { name: /login/i });
    button.focus();
    expect(button).toHaveFocus();
});